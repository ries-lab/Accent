package de.embl.rieslab.accent.common.processor;

import java.io.File;
import java.util.ArrayList;

import javax.swing.SwingUtilities;

import org.apache.commons.math3.stat.regression.SimpleRegression;

import de.embl.rieslab.accent.common.data.calibration.Calibration;
import de.embl.rieslab.accent.common.data.calibration.CalibrationIO;
import de.embl.rieslab.accent.common.data.image.AvgVarStacks;
import de.embl.rieslab.accent.common.interfaces.data.CalibrationImage;
import de.embl.rieslab.accent.common.interfaces.pipeline.Loader;
import de.embl.rieslab.accent.common.interfaces.pipeline.PipelineController;

public abstract class CalibrationProcessor<T extends CalibrationImage> extends Thread {

	private PipelineController<T> controller;
	private Loader<T> loader;
	protected boolean stop = false;
	private boolean running = false;
	
	private Calibration results;
	private String calibPath;
	private final String folder;

	private long startTime, stopTime;
	
	public final static int START = 0;
	public final static int DONE = -1;
	public final static int STOP = -2;
	public final static int PROGRESS = -3;
	
	public CalibrationProcessor(String folder, PipelineController<T> controller, Loader<T> loader) {
		if(folder == null || controller == null || loader == null)		
			throw new NullPointerException();
		
		if(loader.getNumberOfChannels() < 3)
			throw new IllegalArgumentException("At least three exposures are required.");
			
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
		
		// compute avg and var images
		AvgVarStacks<T> avgvar = computeAvgAndVar();
		T[] avgs = avgvar.getAvgs();
		T[] vars = avgvar.getVars();
		
		if(stop) {
			showProgressOnEDT(STOP, null, 0, 0, 0);
			return 0;
		} else {
			showProgressOnEDT(PROGRESS, "", 80);
		}

		// saves images
		for(int q=0;q<loader.getNumberOfChannels();q++) {
			if(avgs[q] != null && vars[q]!=null) {
				if(Double.compare(avgs[q].getExposure(), (int)avgs[q].getExposure()) == 0) {
					avgs[q].saveAsTiff(folder + "\\Avg_" + (int) avgs[q].getExposure() + "ms.tiff"); // to avoid "10.0ms"
					vars[q].saveAsTiff(folder + "\\Var_" + (int) vars[q].getExposure() + "ms.tiff");
				} else {
					avgs[q].saveAsTiff(folder + "\\Avg_" + avgs[q].getExposure() + "ms.tiff");
					vars[q].saveAsTiff(folder + "\\Var_" + vars[q].getExposure() + "ms.tiff");
				}
			}
		}
		
		if(stop) {
			showProgressOnEDT(STOP, null, 0, 0, 0);
			return 0;
		} else {
			showProgressOnEDT(PROGRESS, "Regression", 85);
		}
		
		// linear regression
		results = performLinearRegressions(avgs, vars);
				
		if(stop) {
			showProgressOnEDT(STOP, null, 0, 0, 0);
			return 0;
		} else {
			showProgressOnEDT(PROGRESS, "Regression", 90);
		}

		// write calibration file to the disk
		calibPath = writeCalibrationToFile();
		if(calibPath != null) {
			writeCalibrationToImages(folder, results);
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
	
	/**
	 * Compute the average and variance images from the Loader input and store them in avgs and vars.
	 */
	protected abstract AvgVarStacks<T> computeAvgAndVar();
	
	protected Calibration performLinearRegressions(T[] avgs, T[] vars) {
		int width = (int) avgs[0].getWidth();
		int height = (int) avgs[0].getHeight();
		int totalLength = height * width;
		
		// instantiates the arrays for the linear regression:
		// Ne = number of exposures
		// L = w x h = number of pixels
		// each list is > Ne x 2 x L x doubles
		ArrayList<double[][]> avg_exp_list = new ArrayList<double[][]>(totalLength);
		ArrayList<double[][]> var_exp_list = new ArrayList<double[][]>(totalLength);
		ArrayList<double[][]> var_avg_list = new ArrayList<double[][]>(totalLength);
	
		for (int k = 0; k < totalLength; k++) {
			avg_exp_list.add(new double[avgs.length][2]);
			var_exp_list.add(new double[avgs.length][2]);
			var_avg_list.add(new double[avgs.length][2]);
		}	
		
		for(int q=0;q<avgs.length;q++) { // for each exposure time
			// fills arrays for linear regression
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					if(stop) {
						return null;
					}
					avg_exp_list.get(x + width * y)[q][0] = avgs[q].getExposure() / 1000.; // exposure in sec
					avg_exp_list.get(x + width * y)[q][1] = avgs[q].getPixelValue(x, y); // average pixel value at this exposure
					var_exp_list.get(x + width * y)[q][0] = avgs[q].getExposure() / 1000.; // exposure in sec
					var_exp_list.get(x + width * y)[q][1] = vars[q].getPixelValue(x, y); // variance of the pixel at this exposure
					var_avg_list.get(x + width * y)[q][0] = avgs[q].getPixelValue(x, y); // average pixel value at this exposure
					var_avg_list.get(x + width * y)[q][1] = vars[q].getPixelValue(x, y); // variance of the pixel at this exposure
				}
			}
		}
		
		SimpleRegression sreg;
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
			
			// perform regressions
			sreg = new SimpleRegression();
			sreg.addData(avg_exp_list.get(i));
			baseline[i] = sreg.getIntercept();
			dcpt[i] = sreg.getSlope();
			rsq_avg[i] = sreg.getRSquare();
			
			sreg = new SimpleRegression();
			sreg.addData(var_exp_list.get(i));
			rnsq[i] = sreg.getIntercept();
			tnsqpt[i] = sreg.getSlope();
			rsq_var[i] = sreg.getRSquare();

			sreg = new SimpleRegression();
			sreg.addData(var_avg_list.get(i));
			gain[i] = sreg.getSlope();
			rsq_gain[i] = sreg.getRSquare();
		}
		
		// sanity check on the median: replace negative gains by the median
/*		double median = StatUtils.percentile(gain, 50);
		for (int i = 0; i < totalLength; i++) {
			if(Double.isNaN(gain[i]) || Double.compare(gain[i], 0) <= 0.0) {
				gain[i] = median;
			}
		}
		
*/
		Calibration results = new Calibration(width, height);
		
		// saves results in the calibration
		results.setBaseline(baseline);
		results.setDcPerSec(dcpt);
		results.setGain(gain);
		results.setRnSq(rnsq);
		results.setTnSqPerSec(tnsqpt);
		results.setRSqAvg(rsq_avg);
		results.setRSqVar(rsq_var);
		results.setRSqGain(rsq_gain);

		return results;
	}

	protected String writeCalibrationToFile() {
		// Writes configuration to disk
		String calibPath = folder+"\\results."+CalibrationIO.CALIB_EXT;
		CalibrationIO.write(new File(calibPath), results);
		return calibPath;
	}
	
	protected abstract void writeCalibrationToImages(String folder, Calibration results);
	
	protected PipelineController<T> getController() {
		return controller;
	}

	public Loader<T> getLoader() {
		return loader;
	}
}
