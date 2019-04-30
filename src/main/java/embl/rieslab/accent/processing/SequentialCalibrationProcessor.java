package main.java.embl.rieslab.accent.processing;

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

import main.java.embl.rieslab.accent.PipelineController;
import main.java.embl.rieslab.accent.calibration.Calibration;
import main.java.embl.rieslab.accent.calibration.CalibrationIO;
import main.java.embl.rieslab.accent.data.FloatImage;
import main.java.embl.rieslab.accent.utils.utils;

public class SequentialCalibrationProcessor extends SwingWorker<Integer, Integer> implements Processor{
	
	private Studio studio;
	private String[] directories;
	private PipelineController controller;
	private boolean stop = false;
	private boolean running = false;

	private long startTime, stopTime;
	
	private Calibration results;
	private String calibPath;
	
	private final static int START = 0;
	private final static int DONE = -1;
	private final static int STOP = -2;
	
	public SequentialCalibrationProcessor(Studio studio, String[] directories, PipelineController pipelineController) {
		if(studio == null || directories == null || pipelineController == null) {
			throw new NullPointerException();
		}
		
		this.studio = studio;
		this.directories = directories;
		this.controller = pipelineController;
		
		results = new Calibration();
		
		startTime = 0;
		stopTime = 0;
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

		startTime = System.currentTimeMillis();
		publish(START);

		double percentile = 100./(directories.length+1);
		
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
			int exposure = utils.extractExposurefromFolderName(file);
			
			try {
				store = studio.data().loadData(file, true);
				float stackSize = (float) store.getNumImages();

				// gets the first image of the stack
				builder.channel(0).z(0).stagePosition(0).time(0);

				Image snapim = store.getImage(builder.build());

				FloatImage avg_im = new FloatImage(snapim, exposure);
				FloatImage var_im = new FloatImage(snapim, exposure);
				var_im.square();

				// loops over the stack and adds pixel-wise the value of each pixels and their
				// square (normalized by the stack size)
				for (int z = 1; z < stackSize; z++) {

					// gets image at position z in the stack
					builder.time(z);
					snapim = store.getImage(builder.build());

					// updates progress bar
					publish((int) (percentile * counter + percentile * z / stackSize));

					avg_im.addPixels(snapim);
					var_im.addSquarePixels(snapim);
				}

				// computes the variance image from the average square and the average values
				avg_im.dividePixels(stackSize);
				var_im.toVariance(avg_im.getImage(), stackSize);

				// save as images
				avg_im.saveAsTiff(getParentPath(file) + "/" + "Avg_" + exposure + "ms.tiff");
				var_im.saveAsTiff(getParentPath(file) + "/" + "Var_" + exposure + "ms.tiff");
				
				store.close();	
				
				// fills arrays
				for (int y = 0; y < height; y++) {
	
					for (int x = 0; x < width; x++) {
	
						if (stop) {
							publish(STOP);
							return 0;
						}
	
						avg_exp_list.get(x + width * y)[counter][0] = exposure*1000;
						avg_exp_list.get(x + width * y)[counter][1] = avg_im.getPixelValue(x, y);
						var_exp_list.get(x + width * y)[counter][0] = exposure*1000;
						var_exp_list.get(x + width * y)[counter][1] = var_im.getPixelValue(x, y);
						var_avg_list.get(x + width * y)[counter][0] = avg_im.getPixelValue(x, y);
						var_avg_list.get(x + width * y)[counter][1] = var_im.getPixelValue(x, y);
					}
				}

			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "Unable to load:\n" + file + "\n\nWas it acquired with Micro-Manager?",
						"Error", JOptionPane.INFORMATION_MESSAGE);

				e.printStackTrace();
				publish(STOP);
			}	
		}
		
			
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
				publish(STOP);
				return 0;
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
		calibPath = parentFolder+"\\results."+CalibrationIO.CALIB_EXT;
		CalibrationIO.write(new File(calibPath), results);
		
		// Writes the results as images
		new FloatImage(width, height, results.baseline, 0).saveAsTiff(parentFolder+"\\"+"Baseline.tiff");

		new FloatImage(width, height, results.dc_per_sec, 0).saveAsTiff(parentFolder+"\\"+"DC_per_sec.tiff");

		new FloatImage(width, height, results.gain, 0).saveAsTiff(parentFolder+"\\"+"Gain.tiff");

		new FloatImage(width, height, results.rn_sq, 0).saveAsTiff(parentFolder+"\\"+"RN_sq.tiff");

		new FloatImage(width, height, results.tn_sq_per_sec, 0).saveAsTiff(parentFolder+"\\"+"TN_sq_per_sec.tiff");

		stopTime = System.currentTimeMillis();
		publish(DONE);
		
		return 0;
	}
	
	@Override
	protected void process(List<Integer> chunks) {
		for(Integer i:chunks) {
			if(i == START) {
				controller.processingHasStarted();
			} else if(i == DONE) {
				running = false;
				controller.processingHasEnded();
				controller.updateProcessorProgress("Done.",100);
			} else if(i == STOP) {
				running = false;
				controller.processingHasStopped();
				controller.updateProcessorProgress("Interrupted.",50);
			} else {
				int progress = i;
				int step = (int) (progress * (directories.length+1) / 100)+1;
				controller.updateProcessorProgress("Step: "+step+"/"+(directories.length+1), progress);
			}
		}
	}
	
	
	public static String getParentPath(String dataFolder) {
		return new File(dataFolder).getParent();
	}


	@Override
	public double getExecutionTime() {
		return ((double) stopTime-startTime)/1000.0;
	}


	@Override
	public String getCalibrationPath() {
		String parentFolder = new File(directories[0]).getParentFile().getAbsolutePath();

		return parentFolder+"\\results."+CalibrationIO.CALIB_EXT;
	}


	@Override
	public Calibration getCalibration() {
		return results;
	}
}
