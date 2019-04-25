package main.java.embl.rieslab.photonfreecamcalib.processing;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import javax.swing.SwingWorker;

import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.regression.SimpleRegression;

import ij.ImagePlus;
import ij.io.FileSaver;
import ij.process.FloatProcessor;
import main.java.embl.rieslab.photonfreecamcalib.PipelineController;
import main.java.embl.rieslab.photonfreecamcalib.calibration.Calibration;
import main.java.embl.rieslab.photonfreecamcalib.calibration.CalibrationIO;
import main.java.embl.rieslab.photonfreecamcalib.data.FloatImage;

public class ConcurrentCalibrationProcessor  extends SwingWorker<Integer, Integer> implements Processor{

	private PipelineController controller;
	private boolean stop = false;
	private boolean running = false;

	private Calibration results;
	private String calibPath;
	private final String folder;

	private long startTime, stopTime;
	
	private final static int START = 0;
	private final static int DONE = -1;
	private final static int STOP = -2;
	
	private final ArrayList<ArrayBlockingQueue<FloatImage>> queues;
	
	public ConcurrentCalibrationProcessor(String folder, ArrayList<ArrayBlockingQueue<FloatImage>> queues, PipelineController testPipelineController) {
		if(queues == null|| testPipelineController == null) {
			throw new NullPointerException();
		}
		
		for(int i=0;i<queues.size();i++) {
			if(queues.get(i) == null) {
				throw new NullPointerException();
			}
		}
		
		this.folder = folder;
		this.queues = queues;
		this.controller = testPipelineController;
		
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

		FloatImage[] avgs = new FloatImage[queues.size()];
		FloatImage[] vars = new FloatImage[queues.size()];
		int[] stackSizes = new int[queues.size()];
		
		boolean done = false;
		while(!done) {
			boolean allEmpty = true;
			for(int q=0;q<queues.size();q++) {
				if(!queues.get(q).isEmpty()) {
					allEmpty = false;
					
					// first round
					if(avgs[q] == null) {
						stackSizes[q] = 1;
						avgs[q] = queues.get(q).poll();
						vars[q] = avgs[q].copy();
						vars[q].square();
					} else {
						// poll the newest image
						FloatImage im = queues.get(q).poll();
						avgs[q].addPixels(im.getPixels());
						vars[q].addSquarePixels(im.getPixels());
						stackSizes[q]++;
					}
				}
			}
			
			// if stopped by the user
			if(stop){
				done = true;
			}
			
			// all queues were empty
			if(allEmpty && controller.isAcquisitionDone()) {
				done = true;
			}
			
		}

		publish(1);
		
		// instantiates the arrays for the linear regression
		ArrayList<double[][]> avg_exp_list = new ArrayList<double[][]>();
		ArrayList<double[][]> var_exp_list = new ArrayList<double[][]>();
		ArrayList<double[][]> var_avg_list = new ArrayList<double[][]>();
		if(!stop) {
			
			for (int k = 0; k < avgs[0].getWidth()*avgs[0].getHeight(); k++) {
				avg_exp_list.add(new double[queues.size()][2]);
				var_exp_list.add(new double[queues.size()][2]);
				var_avg_list.add(new double[queues.size()][2]);
			}
			
			// computes avg and var for each exposure and save the images
			for(int q=0;q<queues.size();q++) {
				avgs[q].dividePixels((float) stackSizes[q]);
				vars[q].toVariance(avgs[q].getPixels(), (float) stackSizes[q]);
	
				// save images
				FileSaver avgsaver = new FileSaver(new ImagePlus("Avg_" + avgs[q].getExposure() + "ms", avgs[q].getProcessor()));
				avgsaver.saveAsTiff(folder + "/" + "Avg_" + avgs[q].getExposure() + "ms.tiff");
	
				FileSaver sdsaver = new FileSaver(new ImagePlus("Var_" + avgs[q].getExposure() + "ms", vars[q].getProcessor()));
				sdsaver.saveAsTiff(folder + "/" + "Var_" + avgs[q].getExposure() + "ms.tiff");
	
				
				// fills arrays for linear regression
				for (int y = 0; y < avgs[0].getHeight(); y++) {

					if(stop) {
						break;
					}
					
					for (int x = 0; x < avgs[0].getWidth(); x++) {
						avg_exp_list.get(x + avgs[0].getWidth() * y)[q][0] = avgs[q].getExposure()*1000;
						avg_exp_list.get(x + avgs[0].getWidth() * y)[q][1] = avgs[q].getProcessor().getf(x, y);
						var_exp_list.get(x + avgs[0].getWidth() * y)[q][0] = avgs[q].getExposure()*1000;
						var_exp_list.get(x + avgs[0].getWidth() * y)[q][1] = vars[q].getProcessor().getf(x, y);
						var_avg_list.get(x + avgs[0].getWidth() * y)[q][0] = avgs[q].getProcessor().getf(x, y);
						var_avg_list.get(x + avgs[0].getWidth() * y)[q][1] = vars[q].getProcessor().getf(x, y);
					}
				}
				
				if(stop) {
					break;
				}
			}
		
			
			
		}
		
		publish(2);
		
		if(!stop) {
			
			int width = avgs[0].getWidth();
			int height = avgs[0].getHeight(); 
			int totalLength = height * width;
			
			// linear regression
			SimpleRegression[] avg_exp_reg = new SimpleRegression[totalLength];
			SimpleRegression[] var_exp_reg = new SimpleRegression[totalLength];
			SimpleRegression[] var_avg_reg = new SimpleRegression[totalLength];
			double[] baseline = new double[totalLength];
			double[] dcpt = new double[totalLength];
			double[] rnsq = new double[totalLength];
			double[] tnsqpt = new double[totalLength];
			double[] gain = new double[totalLength];
						
			for (int i = 0; i < totalLength; i++) {
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

			}

			if (!stop) {			
				// sanity check on the median: replace negative gains by the median
				double median = StatUtils.percentile(gain, 50);
				for (int i = 0; i < totalLength; i++) {
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
				calibPath = folder+"\\results."+CalibrationIO.CALIB_EXT;
				CalibrationIO.write(new File(calibPath), results);
				
				// Writes the results as images
				FileSaver baselineim = new FileSaver(new ImagePlus("Baseline",new FloatProcessor(width, height, results.baseline))); 
				baselineim.saveAsTiff(folder+"\\"+"Baseline.tiff");

				FileSaver dcpert = new FileSaver(new ImagePlus("DC_per_sec",new FloatProcessor(width, height, results.dc_per_sec))); 
				dcpert.saveAsTiff(folder+"\\"+"DC_per_sec.tiff");

				FileSaver gainim = new FileSaver(new ImagePlus("Gain",new FloatProcessor(width, height, results.gain))); 
				gainim.saveAsTiff(folder+"\\"+"Gain.tiff");

				FileSaver rnsqim = new FileSaver(new ImagePlus("RN_sq",new FloatProcessor(width, height, results.rn_sq))); 
				rnsqim.saveAsTiff(folder+"\\"+"RN_sq.tiff");

				FileSaver tnsqpert = new FileSaver(new ImagePlus("TN_sq_per_sec",new FloatProcessor(width, height, results.tn_sq_per_sec))); 
				tnsqpert.saveAsTiff(folder+"\\"+"TN_sq_per_sec.tiff");
			}
		}

		publish(3);
		
		stopTime = System.currentTimeMillis();
		
		if(stop) {
			publish(STOP);
		} else {
			publish(DONE);
		}
		
		running = false;
		
		return 0;
	}
	
	@Override
	protected void process(List<Integer> chunks) {
		for(Integer i:chunks) {
			if(i == START) {
				controller.processingHasStarted();
				controller.updateProcessorProgress("Processing ...",0);
			} else if(i == DONE) {
				controller.processingHasEnded();
				controller.updateProcessorProgress("Done.",100);
			} else if(i == STOP) {
				controller.processingHasStopped();
				controller.updateProcessorProgress("Interrupted.",50);
			} else {
				controller.updateProcessorProgress("Step: "+i+"/"+3, i*33);
			}
		}
	}
	
	@Override
	public double getExecutionTime() {
		return ((double) stopTime-startTime)/1000.0;
	}


	@Override
	public String getCalibrationPath() {
		return folder+"\\results."+CalibrationIO.CALIB_EXT;
	}


	@Override
	public Calibration getCalibration() {
		return results;
	}
}