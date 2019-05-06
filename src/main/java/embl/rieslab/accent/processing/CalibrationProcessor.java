package main.java.embl.rieslab.accent.processing;

import java.io.File;
import java.util.ArrayList;

import javax.swing.SwingUtilities;

import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.regression.SimpleRegression;

import main.java.embl.rieslab.accent.PipelineController;
import main.java.embl.rieslab.accent.data.calibration.Calibration;
import main.java.embl.rieslab.accent.data.calibration.CalibrationIO;
import main.java.embl.rieslab.accent.data.images.FloatImage;
import main.java.embl.rieslab.accent.loader.Loader;

public abstract class CalibrationProcessor<T> extends Thread {

	private PipelineController controller;
	private Loader<T> loader;
	private boolean stop = false;
	private boolean running = false;
	
	private Calibration results;
	private String calibPath;
	private final String folder;

	private long startTime, stopTime;
	
	public final static int START = 0;
	public final static int DONE = -1;
	public final static int STOP = -2;
	public final static int PROGRESS = -3;
	
	public CalibrationProcessor(String folder, PipelineController controller, Loader<T> loader) {
		this.folder = folder;
		this.controller = controller;
		this.loader = loader;
		
		startTime = 0;
		stopTime = 0;
	}
	
	@Override
	public void run() {
		stop = false;
		running = true;
		runProcess();

	}

	public boolean startProcess() {
		this.start();
		return true;
	}
	
	public void stopProcess() {
		stop = true;
		running = false;
	}

	public boolean isRunning() {
		return running;
	}

	public double getExecutionTime() {
		return ((double) stopTime-startTime)/1000.0;
	}


	public String getCalibrationPath() {
		return calibPath;
	}


	public Calibration getCalibration() {
		return results;
	}
	
	protected Integer runProcess() {
		startTime = System.currentTimeMillis();
		showProgressOnEDT(START, null, 0, 0, 0);

		FloatImage[] avgs = new FloatImage[loader.getSize()];
		FloatImage[] vars = new FloatImage[loader.getSize()];
		int[] stackSizes = new int[loader.getSize()];
		
		computeAvgAndVar(loader, avgs, vars, stackSizes);
		
		if(stop) {
			showProgressOnEDT(STOP, null, 0, 0, 0);
			return 0;
		} else {
			showProgressOnEDT(PROGRESS, "", 80);
		}
		
		for(int q=0;q<loader.getSize();q++) {
			// save images
			avgs[q].saveAsTiff(folder + "\\results\\" + "Avg_" + avgs[q].getExposure() + "ms.tiff");
			vars[q].saveAsTiff(folder + "\\results\\" + "Var_" + avgs[q].getExposure() + "ms.tiff");
		}

		
		if(stop) {
			showProgressOnEDT(STOP, null, 0, 0, 0);
			return 0;
		} else {
			showProgressOnEDT(PROGRESS, "Regression", 85);
		}
		
		results = performLinearRegressions(folder, avgs, vars);
		
		if(stop) {
			showProgressOnEDT(STOP, null, 0, 0, 0);
			return 0;
		} else {
			showProgressOnEDT(PROGRESS, "Regression", 90);
		}
		
		calibPath = writeCalibrationToFile();
		
		if(calibPath != null) {
			writeCalibrationToImages();
		}
		
		if(stop) {
			showProgressOnEDT(STOP, null, 0, 0, 0);
			return 0;
		}
		
		stopTime = System.currentTimeMillis();
		showProgressOnEDT(DONE, null, 0, 0, 0);
		
		return 0;
	}
	
	protected void showProgressOnEDT(int flag, String message, int step, int totalSteps, int percentage) {
		SwingUtilities.invokeLater(new Runnable() {
	        public void run() {
	        	showProgress(flag, message, step, totalSteps, percentage);
	        }
	    });
	}	
	
	
	protected void showProgressOnEDT(int flag, String message, int percentage) {
		SwingUtilities.invokeLater(new Runnable() {
	        public void run() {
	        	showProgress(flag, message, percentage);
	        }
	    });
	}
	
	private void showProgress(int flag, String message, int step, int totalSteps, int percentage) {
		if(flag == START) {
			controller.processingHasStarted();
			controller.updateProcessorProgress("Processing ...",0);
		} else if(flag == DONE) {
			running = false;
			controller.processingHasEnded();
			controller.updateProcessorProgress("Done.",100);
		} else if(flag == STOP) {
			running = false;
			controller.processingHasStopped();
			controller.updateProcessorProgress("Interrupted.",50);
		} else if(flag == PROGRESS) {
			controller.updateProcessorProgress(message+step+"/"+totalSteps, percentage);
		}	
	}
	
	private void showProgress(int flag, String message, int percentage) {
		if(flag == START) {
			controller.processingHasStarted();
			controller.updateProcessorProgress("Processing ...",0);
		} else if(flag == DONE) {
			running = false;
			controller.processingHasEnded();
			controller.updateProcessorProgress("Done.",100);
		} else if(flag == STOP) {
			running = false;
			controller.processingHasStopped();
			controller.updateProcessorProgress("Interrupted.",50);
		} else if(flag == PROGRESS) {
			controller.updateProcessorProgress(message, percentage);
		}	
	}
	
	protected abstract void computeAvgAndVar(Loader<T> loader, FloatImage[] avgs, FloatImage[] vars, int[] stackSizes);
	
	protected Calibration performLinearRegressions(String folder, FloatImage[] avgs, FloatImage[] vars) {
		int width = (int) avgs[0].getWidth();
		int height = (int) avgs[0].getHeight();
		int totalLength = height * width;
		
		// instantiates the arrays for the linear regression
		ArrayList<double[][]> avg_exp_list = new ArrayList<double[][]>();
		ArrayList<double[][]> var_exp_list = new ArrayList<double[][]>();
		ArrayList<double[][]> var_avg_list = new ArrayList<double[][]>();
	
		for (int k = 0; k < totalLength; k++) {
			avg_exp_list.add(new double[avgs.length][2]);
			var_exp_list.add(new double[avgs.length][2]);
			var_avg_list.add(new double[avgs.length][2]);
		}	
		
		for(int q=0;q<avgs.length;q++) {
			// fills arrays for linear regression
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					if(stop) {
						return null;
					}
					
					
					avg_exp_list.get(x + width * y)[q][0] = avgs[q].getExposure() * 1000;
					avg_exp_list.get(x + width * y)[q][1] = avgs[q].getPixelValue(x, y);
					var_exp_list.get(x + width * y)[q][0] = avgs[q].getExposure() * 1000;
					var_exp_list.get(x + width * y)[q][1] = vars[q].getPixelValue(x, y);
					var_avg_list.get(x + width * y)[q][0] = avgs[q].getPixelValue(x, y);
					var_avg_list.get(x + width * y)[q][1] = vars[q].getPixelValue(x, y);
				}
			}
		}
		
		SimpleRegression[] avg_exp_reg = new SimpleRegression[totalLength];
		SimpleRegression[] var_exp_reg = new SimpleRegression[totalLength];
		SimpleRegression[] var_avg_reg = new SimpleRegression[totalLength];
		double[] baseline = new double[totalLength];
		double[] dcpt = new double[totalLength];
		double[] rnsq = new double[totalLength];
		double[] tnsqpt = new double[totalLength];
		double[] gain = new double[totalLength];
		double[] rsq_avg = new double[totalLength];
		double[] rsq_var = new double[totalLength];
		double[] rsq_gain = new double[totalLength];
		
		for (int i = 0; i < totalLength; i++) {
			if(stop) {
				return null;
			}
			
			avg_exp_reg[i] = new SimpleRegression();
			avg_exp_reg[i].addData(avg_exp_list.get(i));
			baseline[i] = avg_exp_reg[i].getIntercept();
			dcpt[i] = avg_exp_reg[i].getSlope();
			rsq_avg[i] = avg_exp_reg[i].getRSquare();
			
			var_exp_reg[i] = new SimpleRegression();
			var_exp_reg[i].addData(var_exp_list.get(i));
			rnsq[i] = var_exp_reg[i].getIntercept();
			tnsqpt[i] = var_exp_reg[i].getSlope();
			rsq_var[i] = var_exp_reg[i].getRSquare();

			var_avg_reg[i] = new SimpleRegression();
			var_avg_reg[i].addData(var_avg_list.get(i));
			gain[i] = var_avg_reg[i].getSlope();
			rsq_gain[i] = var_avg_reg[i].getRSquare();

		}
		
		// sanity check on the median: replace negative gains by the median
		double median = StatUtils.percentile(gain, 50);
		for (int i = 0; i < totalLength; i++) {
			if(Double.isNaN(gain[i]) || Double.compare(gain[i], 0) <= 0.0) {
				gain[i] = median;
			}
		}
		

		Calibration results = new Calibration();
		
		// saves results in the calibration
		results.width = width;
		results.height = height;
		results.baseline = baseline;
		results.dc_per_sec = dcpt;
		results.gain = gain;
		results.rn_sq = rnsq;
		results.tn_sq_per_sec = tnsqpt;
		results.r_sq_avg = rsq_avg;
		results.r_sq_var = rsq_var;
		results.r_sq_gain = rsq_gain;

		return results;
	}

	protected String writeCalibrationToFile() {
		// Writes configuration to disk
		String calibPath = folder+"\\results."+CalibrationIO.CALIB_EXT;
		CalibrationIO.write(new File(calibPath), results);
		return calibPath;
	}
	
	protected void writeCalibrationToImages() {
		// Writes the results as images
		new FloatImage(results.width, results.height, results.baseline, 0).saveAsTiff(folder+"\\results\\Baseline.tiff");
		new FloatImage(results.width, results.height, results.dc_per_sec, 0).saveAsTiff(folder+"\\results\\DC_per_sec.tiff");
		new FloatImage(results.width, results.height, results.gain, 0).saveAsTiff(folder+"\\results\\Gain.tiff");
		new FloatImage(results.width, results.height, results.rn_sq, 0).saveAsTiff(folder+"\\results\\RN_sq.tiff");
		new FloatImage(results.width, results.height, results.tn_sq_per_sec, 0).saveAsTiff(folder+"\\restults\\TN_sq_per_sec.tiff");
		new FloatImage(results.width, results.height, results.r_sq_avg, 0).saveAsTiff(folder+"\\results\\R_sq_avg.tiff");
		new FloatImage(results.width, results.height, results.r_sq_var, 0).saveAsTiff(folder+"\\results\\R_sq_var.tiff");
		new FloatImage(results.width, results.height, results.r_sq_gain, 0).saveAsTiff(folder+"\\results\\R_sq_gain.tiff");
	}
	
	protected PipelineController getController() {
		return controller;
	}
}
