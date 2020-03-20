package de.embl.rieslab.accent.common.processor;

import java.io.File;
import java.util.ArrayList;

import javax.swing.SwingUtilities;

import org.apache.commons.math3.stat.regression.SimpleRegression;

import de.embl.rieslab.accent.common.data.calibration.Calibration;
import de.embl.rieslab.accent.common.data.calibration.CalibrationIO;
import de.embl.rieslab.accent.common.data.image.FloatImage;
import de.embl.rieslab.accent.common.interfaces.Loader;
import de.embl.rieslab.accent.common.interfaces.PipelineController;

public abstract class CalibrationProcessor extends Thread {

	private PipelineController controller;
	private Loader loader;
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
	
	public CalibrationProcessor(String folder, PipelineController controller, Loader loader) {
		
		if(loader.getSize() < 3) {
			throw new IllegalArgumentException("At least three exposures are required.");
		}
		
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
		
		// compute avg and var images
		computeAvgAndVar(loader, avgs, vars, stackSizes);
		
		if(stop) {
			showProgressOnEDT(STOP, null, 0, 0, 0);
			return 0;
		} else {
			showProgressOnEDT(PROGRESS, "", 80);
		}
		
		// saves images
		for(int q=0;q<loader.getSize();q++) {
			if(avgs[q] != null && vars[q]!=null) {
				avgs[q].saveAsTiff(folder + "\\Avg_" + avgs[q].getExposure() + "ms.tiff");
				vars[q].saveAsTiff(folder + "\\Var_" + vars[q].getExposure() + "ms.tiff");
			}
		}

		
		if(stop) {
			showProgressOnEDT(STOP, null, 0, 0, 0);
			return 0;
		} else {
			showProgressOnEDT(PROGRESS, "Regression", 85);
		}
		
		// linear regression
		results = performLinearRegressions(folder, avgs, vars);
				
		if(stop) {
			showProgressOnEDT(STOP, null, 0, 0, 0);
			return 0;
		} else {
			showProgressOnEDT(PROGRESS, "Regression", 90);
		}
		
		// write calibration file to the disk
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
	
	protected abstract void computeAvgAndVar(Loader loader, FloatImage[] avgs, FloatImage[] vars, int[] stackSizes);
	
	protected Calibration performLinearRegressions(String folder, FloatImage[] avgs, FloatImage[] vars) {
		int width = (int) avgs[0].getWidth();
		int height = (int) avgs[0].getHeight();
		int totalLength = height * width;
		
		// instantiates the arrays for the linear regression
		ArrayList<double[][]> avg_exp_list = new ArrayList<double[][]>(totalLength);
		ArrayList<double[][]> var_exp_list = new ArrayList<double[][]>(totalLength);
		ArrayList<double[][]> var_avg_list = new ArrayList<double[][]>(totalLength);
	
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
					avg_exp_list.get(x + width * y)[q][0] = avgs[q].getExposure() / 1000.;
					avg_exp_list.get(x + width * y)[q][1] = avgs[q].getPixelValue(x, y);
					var_exp_list.get(x + width * y)[q][0] = avgs[q].getExposure() / 1000.;
					var_exp_list.get(x + width * y)[q][1] = vars[q].getPixelValue(x, y);
					var_avg_list.get(x + width * y)[q][0] = avgs[q].getPixelValue(x, y);
					var_avg_list.get(x + width * y)[q][1] = vars[q].getPixelValue(x, y);
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
	
	protected void writeCalibrationToImages() {
		// Writes the results as images
		FloatImage baseline = new FloatImage(results.getWidth(), results.getHeight(), results.getBaseline(), 0);
		baseline.saveAsTiff(folder+"\\Baseline.tiff");
		//ImagePlus baseline_imp = new ImagePlus("Baseline", baseline.getImage());
		//new HistogramWindow(baseline_imp).showHistogram(baseline_imp, baseline_imp.getAllStatistics());
		
		FloatImage dc_per_sec = new FloatImage(results.getWidth(), results.getHeight(), results.getDcPerSec(), 0);
		dc_per_sec.saveAsTiff(folder+"\\DC_per_sec.tiff");
		//HistogramWindow hw_dc_per_sec = new HistogramWindow(new ImagePlus("DC per sec", dc_per_sec.getImage()));
		
		FloatImage gain = new FloatImage(results.getWidth(), results.getHeight(), results.getGain(), 0);
		gain.saveAsTiff(folder+"\\Gain.tiff");
		//HistogramWindow hw_gain = new HistogramWindow(new ImagePlus("Gain", gain.getImage()));
		
		FloatImage rn_sq = new FloatImage(results.getWidth(), results.getHeight(), results.getRnSq(), 0);
		rn_sq.saveAsTiff(folder+"\\RN_sq.tiff");
		//HistogramWindow hw_rn_sq = new HistogramWindow(new ImagePlus("RN square", rn_sq.getImage()));
		
		FloatImage tn_sq_per_sec = new FloatImage(results.getWidth(), results.getHeight(), results.getTnSqPerSec(), 0);
		tn_sq_per_sec.saveAsTiff(folder+"\\TN_sq_per_sec.tiff");
		//HistogramWindow hw_tn_sq_per_sec = new HistogramWindow(new ImagePlus("TN square per sec", tn_sq_per_sec.getImage()));
		
		FloatImage r_sq_avg = new FloatImage(results.getWidth(), results.getHeight(), results.getRsqAvg(), 0);
		r_sq_avg.saveAsTiff(folder+"\\R_sq_avg.tiff");
		//HistogramWindow hw_r_sq_avg = new HistogramWindow(new ImagePlus("R square of the avg", r_sq_avg.getImage()));
		
		FloatImage r_sq_var = new FloatImage(results.getWidth(), results.getHeight(), results.getRSqVar(), 0);
		r_sq_var.saveAsTiff(folder+"\\R_sq_var.tiff");
		//HistogramWindow hw_r_sq_var = new HistogramWindow(new ImagePlus("R square of the var", r_sq_var.getImage()));
		
		FloatImage r_sq_gain = new FloatImage(results.getWidth(), results.getHeight(), results.getRSqGain(), 0);
		r_sq_gain.saveAsTiff(folder+"\\R_sq_gain.tiff");
		//HistogramWindow hw_r_sq_gain = new HistogramWindow(new ImagePlus("R square of the gain", r_sq_gain.getImage()));
	}
	
	protected PipelineController getController() {
		return controller;
	}

	public Loader getLoader() {
		return loader;
	}
}
