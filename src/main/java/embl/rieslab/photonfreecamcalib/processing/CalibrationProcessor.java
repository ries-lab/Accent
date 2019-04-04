package main.java.embl.rieslab.photonfreecamcalib.processing;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.micromanager.Studio;
import org.micromanager.data.Coords;
import org.micromanager.data.Datastore;
import org.micromanager.data.Image;
import org.micromanager.data.internal.DefaultCoords;

import ij.ImagePlus;
import ij.io.FileSaver;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import main.java.embl.rieslab.photonfreecamcalib.PipelineController;
import main.java.embl.rieslab.photonfreecamcalib.calibration.Calibration;
import main.java.embl.rieslab.photonfreecamcalib.calibration.CalibrationIO;
import main.java.embl.rieslab.photonfreecamcalib.utils.utils;

public class CalibrationProcessor extends SwingWorker<Integer, Integer> implements Processor{
	
	private Studio studio;
	private String[] directories;
	private PipelineController controller;
	private boolean stop = false;
	private boolean running = false;

	private Calibration results;
	private String calibPath;
	
	private final static int START = 0;
	private final static int DONE = -1;
	private final static int STOP = -2;
	
	public CalibrationProcessor(Studio studio, String[] directories, PipelineController controller) {
		if(studio == null || directories == null || controller == null) {
			throw new NullPointerException();
		}
		
		this.studio = studio;
		this.directories = directories;
		this.controller = controller;
		
		results = new Calibration();
	}
	
	
	@Override
	public void start() {
		stop = false;
		running = true;
		this.execute();
	}

	@Override
	public void stop() {
		stop = true;
		running = false;
	}

	@Override
	public boolean isRunning() {
		return running;
	}

	@Override
	protected Integer doInBackground() throws Exception {
		
		publish(START);

		double percentile = 100/(directories.length+1);
		
		// extract width and height
		Datastore store = studio.data().loadData(directories[0], true);
		Coords.CoordsBuilder builder = new DefaultCoords.Builder();
		builder.channel(0).z(0).stagePosition(0).time(0);
		Image im = store.getImage(builder.build());
		int height = im.getHeight();
		int width = im.getWidth();

		int totLength = width * height;
		
		// instantiate the arrays for the linear regression
		ArrayList<double[][]> avg_exp_list = new ArrayList<double[][]>();
		ArrayList<double[][]> var_exp_list = new ArrayList<double[][]>();
		ArrayList<double[][]> var_avg_list = new ArrayList<double[][]>();
		for (int k = 0; k < totLength; k++) {
			avg_exp_list.add(new double[directories.length][2]);
			var_exp_list.add(new double[directories.length][2]);
			var_avg_list.add(new double[directories.length][2]);
		}
		
		for(int counter=0; counter<directories.length; counter++) {
			String file = directories[counter];
			
			try {
				// compute average and variance images
				store = studio.data().loadData(file, true);
				float stackSize = (float) store.getNumImages();

				// gets the first image of the stack
				builder.channel(0).z(0).stagePosition(0).time(0);

				ImageProcessor improc = studio.data().ij().createProcessor(store.getImage(builder.build()));

				FloatProcessor avg_im = new FloatProcessor(width, height);
				FloatProcessor avgsq_im = new FloatProcessor(width, height);
				avg_im.setFloatArray(improc.getFloatArray());
				avgsq_im.setFloatArray(improc.getFloatArray());

				FloatProcessor var_im = new FloatProcessor(width, height);

				// loops over the stack and adds pixel-wise the value of each pixels and their
				// square (normalized by the stack size)
				for (int z = 1; z < stackSize; z++) {

					// gets image at position z in the stack
					builder.time(z);
					improc = studio.data().ij().createProcessor(store.getImage(builder.build()));

					// updates progress bar
					publish((int) (percentile * counter + percentile * z / stackSize));

					for (int x = 0; x < width; x++) {

						if (stop) {
							break;
						}

						for (int y = 0; y < height; y++) {

							if (stop) {
								break;
							}

							avg_im.setf(x, y, avg_im.getf(x, y) + improc.getf(x, y));
							avgsq_im.setf(x, y, avgsq_im.getf(x, y) + improc.getf(x, y) * improc.getf(x, y));
						}
					}

					if (stop) {
						break;
					}

				}

				if (stop) {
					store.close();
					break;
				}

				// computes the variance image from the average square and the average values
				for (int x = 0; x < width; x++) {
					for (int y = 0; y < height; y++) {
						avg_im.setf(x, y, avg_im.getf(x, y) / stackSize);
						float var = (float) (avgsq_im.getf(x, y) / stackSize - avg_im.getf(x, y) * avg_im.getf(x, y));
						if (var <= 0.0) {
							var = 65535; // 16bits unsigned max, for IJ
						}
						var_im.setf(x, y, var);
					}
				}

				// save as images
				int exposure = utils.extractExposurefromFolderName(file);

				FileSaver avgsaver = new FileSaver(new ImagePlus("Avg_" + exposure + "ms", avg_im));
				avgsaver.saveAsTiff(getParentPath(file) + "/" + "Avg_" + exposure + "ms.tiff");

				FileSaver sdsaver = new FileSaver(new ImagePlus("Var_" + exposure + "ms", var_im));
				sdsaver.saveAsTiff(getParentPath(file) + "/" + "Var_" + exposure + "ms.tiff");
				
				store.close();	
				
				// fills arrays
				for (int y = 0; y < height; y++) {
	
					if (stop) {
						break;
					}
	
					for (int x = 0; x < width; x++) {
	
						if (stop) {
							break;
						}
	
						avg_exp_list.get(x + width * y)[counter][0] = exposure;
						avg_exp_list.get(x + width * y)[counter][1] = avg_im.getf(x, y);
						var_exp_list.get(x + width * y)[counter][0] = exposure;
						var_exp_list.get(x + width * y)[counter][1] = var_im.getf(x, y);
						var_avg_list.get(x + width * y)[counter][0] = avg_im.getf(x, y);
						var_avg_list.get(x + width * y)[counter][1] = var_im.getf(x, y);
					}
				}

			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "Unable to load:\n" + file + "\n\nWas it acquired with Micro-Manager?",
						"Error", JOptionPane.INFORMATION_MESSAGE);

				e.printStackTrace();
				publish(STOP);
			}	
		}
		
		if(!stop) {
			
			// linear regression
			SimpleRegression[] avg_exp_reg = new SimpleRegression[height * width];
			SimpleRegression[] var_exp_reg = new SimpleRegression[height * width];
			SimpleRegression[] var_avg_reg = new SimpleRegression[height * width];
			double[] baseline = new double[height * width];
			double[] dcpt = new double[height * width];
			double[] rnsq = new double[height * width];
			double[] tnsqpt = new double[height * width];
			double[] gain = new double[height * width];
						
			for (int i = 0; i < totLength; i++) {
				if (stop) {
					break;
				}
				
				avg_exp_reg[i] = new SimpleRegression();
				avg_exp_reg[i].addData(avg_exp_list.get(i));
				baseline[i] = avg_exp_reg[i].getIntercept();
				dcpt[i] = avg_exp_reg[i].getSlope();
				
				var_exp_reg[i] = new SimpleRegression();
				var_exp_reg[i].addData(var_exp_list.get(i));
				rnsq[i] = var_exp_reg[i].getIntercept();
				tnsqpt[i] = var_exp_reg[i].getSlope();

				var_avg_reg[i] = new SimpleRegression();
				var_avg_reg[i].addData(var_avg_list.get(i));
				gain[i] = var_avg_reg[i].getSlope();

				publish((int) (percentile * directories.length + percentile * i / totLength));
			}

			if (!stop) {			
				// sanity check on the median: replace negative gains by the median
				double median = StatUtils.percentile(gain, 50);
				for (int i = 0; i < totLength; i++) {
					if(Double.isNaN(gain[i]) || Double.compare(gain[i], 0) <= 0.0) {
						gain[i] = median;
					}
				}
				// saves results in the calibration
				results.width = width;
				results.height = height;
				results.baseline = baseline;
				results.dc_per_sec = dcpt;
				results.gain = gain;
				results.rn_sq = rnsq;
				results.tn_sq_per_sec = tnsqpt;

				// Writes configuration to disk
				String parentFolder = new File(directories[0]).getParentFile().getAbsolutePath();
				calibPath = parentFolder+"\\results.calb";
				CalibrationIO.write(new File(calibPath), results);
				
				// Writes the results as images
				FileSaver baselineim = new FileSaver(new ImagePlus("Baseline",new FloatProcessor(width, height, results.baseline))); 
				baselineim.saveAsTiff(parentFolder+"\\"+"Baseline.tiff");

				FileSaver dcpert = new FileSaver(new ImagePlus("DC_per_sec",new FloatProcessor(width, height, results.dc_per_sec))); 
				dcpert.saveAsTiff(parentFolder+"\\"+"DC_per_sec.tiff");

				FileSaver gainim = new FileSaver(new ImagePlus("Gain",new FloatProcessor(width, height, results.gain))); 
				gainim.saveAsTiff(parentFolder+"\\"+"Gain.tiff");

				FileSaver rnsqim = new FileSaver(new ImagePlus("RN_sq",new FloatProcessor(width, height, results.rn_sq))); 
				rnsqim.saveAsTiff(parentFolder+"\\"+"RN_sq.tiff");

				FileSaver tnsqpert = new FileSaver(new ImagePlus("TN_sq_per_sec",new FloatProcessor(width, height, results.tn_sq_per_sec))); 
				tnsqpert.saveAsTiff(parentFolder+"\\"+"TN_sq_per_sec.tiff");
			}
		}

		if(stop) {
			publish(STOP);
		} else {
			publish(DONE);
		}
		
		return 0;
	}
	
	@Override
	protected void process(List<Integer> chunks) {
		for(Integer i:chunks) {
			if(i == START) {
				controller.processingHasStarted();
			} else if(i == DONE) {
				controller.processingHasEnded();
			} else if(i == STOP) {
				controller.processingHasStopped();
			} else {
				int progress = i;
				controller.updateProcessorProgress(progress);
			}
		}
	}
	
	
	public static String getParentPath(String dataFolder) {
		return new File(dataFolder).getParent();
	}
}
