package main.java.embl.rieslab.accent.processing;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import javax.swing.SwingWorker;

import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.regression.SimpleRegression;

import main.java.embl.rieslab.accent.PipelineController;
import main.java.embl.rieslab.accent.calibration.Calibration;
import main.java.embl.rieslab.accent.calibration.CalibrationIO;
import main.java.embl.rieslab.accent.data.FloatImage;
import main.java.embl.rieslab.accent.data.ImageExposurePair;

public class ConcurrentCalibrationProcessor extends SwingWorker<Integer, Integer> implements Processor{

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
	
	private final ArrayList<ArrayBlockingQueue<ImageExposurePair>> queues;
	
	public ConcurrentCalibrationProcessor(String folder, ArrayList<ArrayBlockingQueue<ImageExposurePair>> queues, PipelineController testPipelineController) {
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
						avgs[q] = new FloatImage(queues.get(q).poll());
						vars[q] = new FloatImage(avgs[q]); // this is suboptimal as it first copies each pixel and then will square it
						vars[q].square();
					} else {
						// poll the newest image
						ImageExposurePair im = queues.get(q).poll();
						avgs[q].addPixels(im.getImage());
						vars[q].addSquarePixels(im.getImage());
						stackSizes[q]++;
					}
				}
			}
			
			// if stopped by the user
			if(stop){
				publish(STOP);
				return 0;
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
		
		int width = (int) avgs[0].getWidth();
		int height = (int) avgs[0].getHeight();
		int totalLength = height * width;

		for (int k = 0; k < totalLength; k++) {
			avg_exp_list.add(new double[queues.size()][2]);
			var_exp_list.add(new double[queues.size()][2]);
			var_avg_list.add(new double[queues.size()][2]);
		}
		
		// computes avg and var for each exposure and save the images
		for(int q=0;q<queues.size();q++) {
			
			if(stop){
				publish(STOP);
				return 0;
			}
			
			avgs[q].dividePixels((float) stackSizes[q]);
			vars[q].toVariance(avgs[q].getImage(), (float) stackSizes[q]);

			// save images
			avgs[q].saveAsTiff(folder + "/" + "Avg_" + avgs[q].getExposure() + "ms.tiff");
			vars[q].saveAsTiff(folder + "/" + "Var_" + avgs[q].getExposure() + "ms.tiff");

			
			// fills arrays for linear regression
			for (int y = 0; y < height; y++) {
				
				if(stop){
					publish(STOP);
					return 0;
				}
				
				for (int x = 0; x < width; x++) {

					avg_exp_list.get(x + width * y)[q][0] = avgs[q].getExposure()*1000;
					avg_exp_list.get(x + width * y)[q][1] = avgs[q].getPixelValue(x, y);
					var_exp_list.get(x + width * y)[q][0] = avgs[q].getExposure()*1000;
					var_exp_list.get(x + width * y)[q][1] = vars[q].getPixelValue(x, y);
					var_avg_list.get(x + width * y)[q][0] = avgs[q].getPixelValue(x, y);
					var_avg_list.get(x + width * y)[q][1] = vars[q].getPixelValue(x, y);
				}
			}
		}

		publish(2);
		
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

		}

	
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
		new FloatImage(width, height, results.baseline, 0).saveAsTiff(folder+"\\"+"Baseline.tiff");

		new FloatImage(width, height, results.dc_per_sec, 0).saveAsTiff(folder+"\\"+"DC_per_sec.tiff");

		new FloatImage(width, height, results.gain, 0).saveAsTiff(folder+"\\"+"Gain.tiff");

		new FloatImage(width, height, results.rn_sq, 0).saveAsTiff(folder+"\\"+"RN_sq.tiff");

		new FloatImage(width, height, results.tn_sq_per_sec, 0).saveAsTiff(folder+"\\"+"TN_sq_per_sec.tiff");

		publish(3);
		
		stopTime = System.currentTimeMillis();
		publish(DONE);
		
		return 0;
	}
	
	@Override
	protected void process(List<Integer> chunks) {
		for(Integer i:chunks) {
			if(i == START) {
				controller.processingHasStarted();
				controller.updateProcessorProgress("Processing ...",0);
			} else if(i == DONE) {
				running = false;
				controller.processingHasEnded();
				controller.updateProcessorProgress("Done.",100);
			} else if(i == STOP) {
				running = false;
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