package de.embl.rieslab.accent.common.processor;

import java.io.File;
import java.util.ArrayList;

import javax.swing.SwingUtilities;

import org.apache.commons.math3.stat.regression.SimpleRegression;

import de.embl.rieslab.accent.common.data.calibration.Calibration;
import de.embl.rieslab.accent.common.data.calibration.CalibrationIO;
import de.embl.rieslab.accent.common.data.image.AvgVarStacks;
import de.embl.rieslab.accent.common.data.roi.SimpleRoi;
import de.embl.rieslab.accent.common.interfaces.data.CalibrationImage;
import de.embl.rieslab.accent.common.interfaces.data.RawImage;
import de.embl.rieslab.accent.common.interfaces.pipeline.Loader;
import de.embl.rieslab.accent.common.interfaces.pipeline.PipelineController;

public abstract class CalibrationProcessor<U extends RawImage, T extends CalibrationImage> extends Thread {

	private PipelineController<U,T> controller;
	private Loader<U> loader;
	protected boolean stop = false;
	private boolean running = false;
	
	private Calibration results;
	private String calibPath;
	private final String folder;
	private final SimpleRoi roi;

	private long startTime, stopTime;
	
	public final static int START = 0;
	public final static int DONE = -1;
	public final static int STOP = -2;
	public final static int PROGRESS = -3;
	
	public final static String BASELINE = "Baseline.tiff";
	public final static String DCPERSEC = "DC_per_sec.tiff";
	public final static String GAIN = "Gain.tiff";
	public final static String RNSQ = "RN_sq.tiff";
	public final static String TNSQPERSEC = "TN_sq_per_sec.tiff";
	public final static String RSQAVG = "R_sq_avg.tiff";
	public final static String RSQVAR = "R_sq_var.tiff";
	public final static String RSQGAIN = "R_sq_gain.tiff";
	
	public final static String DEFAULT_ROI = "roi.roi";
	
	public CalibrationProcessor(String folder, SimpleRoi roi, PipelineController<U,T> controller, Loader<U> loader) {
		if(folder == null || controller == null || loader == null)		
			throw new NullPointerException();
		
		if(loader.getNumberOfChannels() < 2)
			throw new IllegalArgumentException("At least two exposures are required.");
			
		this.folder = folder;
		this.controller = controller;
		this.loader = loader;
		this.roi = roi;
		
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
		showProgressOnEDT(START, "Stopping", 0);
		
		// compute avg and var images
		AvgVarStacks<T> avgvar = computeAvgAndVar();
	
		if(stop) {
			showProgressOnEDT(STOP, "Stopping", 0);
			return 0;
		} else {
			showProgressOnEDT(PROGRESS, "", 80);
		}

		// saves images
		T[] avgs = avgvar.getAvgs();
		T[] vars = avgvar.getVars();
		for(int q=0;q<loader.getNumberOfChannels();q++) {
			if(avgs[q] != null && vars[q]!=null) {
				if(Double.compare(avgs[q].getExposure(), (int)avgs[q].getExposure()) == 0) {
					String avg_name = folder +File.separator+ "Avg_" + avgs[q].getExposure() + "ms.tiff";
					controller.getImageSaver().saveAsTiff(avgs[q], folder+File.separator+"Avg_" + (int) avgs[q].getExposure() + "ms.tiff"); // cast to avoid .0 decimal in the name
					controller.getImageSaver().saveAsTiff(vars[q], folder+File.separator+"Var_" + (int) vars[q].getExposure() + "ms.tiff");
				} else {
					controller.getImageSaver().saveAsTiff(avgs[q], folder +File.separator+ "Avg_" + avgs[q].getExposure() + "ms.tiff");
					controller.getImageSaver().saveAsTiff(vars[q], folder +File.separator+ "Var_" + vars[q].getExposure() + "ms.tiff");
				}
			}
		}
		
		if(stop) {
			showProgressOnEDT(STOP, "Stopping", 0);
			return 0;
		} else {
			showProgressOnEDT(PROGRESS, "Regression", 85);
		}
		
		// linear regression
		results = performLinearRegressions(avgs, vars);
				
		if(stop) {
			showProgressOnEDT(STOP, "Stopping", 0);
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
			showProgressOnEDT(STOP, "Stopping", 0);
			return 0;
		}
		
		stopTime = System.currentTimeMillis();
		showProgressOnEDT(DONE, "Done", 0);
		
		return 0;
	}
	
	protected void showProgressOnEDT(int flag, String message, int percentage) {
		SwingUtilities.invokeLater(new Runnable() {
	        public void run() {
	        	showProgress(flag, message, percentage);
	        }
	    });
	}
		
	private void showProgress(int flag, String message, int percentage) {
		if(flag == START) {
			controller.processorHasStarted();
			controller.updateProcessorProgress("Processing ...",0);
		} else if(flag == DONE) {
			running = false;
			controller.processorHasEnded();
			controller.updateProcessorProgress("Done.",100);
		} else if(flag == STOP) {
			running = false;
			controller.processorHasStopped();
			controller.updateProcessorProgress("Interrupted.",50);
		} else if(flag == PROGRESS) {
			controller.updateProcessorProgress(message, percentage);
		}	
	}
	
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
		float[] baseline = new float[totalLength];
		float[] dcpt = new float[totalLength];
		float[] rnsq = new float[totalLength];
		float[] tnsqpt = new float[totalLength];
		float[] gain = new float[totalLength];
		float[] rsq_avg = new float[totalLength];
		float[] rsq_var = new float[totalLength];
		float[] rsq_gain = new float[totalLength];
		
		for (int i = 0; i < totalLength; i++) {
			if(stop) {
				return null;
			}
			
			// perform regressions
			sreg = new SimpleRegression();
			sreg.addData(avg_exp_list.get(i));
			baseline[i] = (float) sreg.getIntercept();
			dcpt[i] = (float) sreg.getSlope();
			rsq_avg[i] = (float) sreg.getRSquare();
			
			sreg = new SimpleRegression();
			sreg.addData(var_exp_list.get(i));
			rnsq[i] = (float) sreg.getIntercept();
			tnsqpt[i] = (float) sreg.getSlope();
			rsq_var[i] = (float) sreg.getRSquare();

			sreg = new SimpleRegression();
			sreg.addData(var_avg_list.get(i));
			gain[i] = (float) sreg.getSlope();
			rsq_gain[i] = (float) sreg.getRSquare();
		}
		
		// sanity check on the median: replace negative gains by the median
/*		double median = StatUtils.percentile(gain, 50);
		for (int i = 0; i < totalLength; i++) {
			if(Double.isNaN(gain[i]) || Double.compare(gain[i], 0) <= 0.0) {
				gain[i] = median;
			}
		}
		
*/
		// roi comes from the GUI textfields, either automatically filled by
		// a roi saved to the disk or by the users. We always take the width and height 
		// from the images themselves
		Calibration results  = new Calibration(roi.x0, roi.y0, width, height);
		
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
		String calibPath = folder+File.separator+"results."+CalibrationIO.CALIB_EXT;
		CalibrationIO.write(new File(calibPath), results);
		return calibPath;
	}
		
	protected PipelineController<U,T> getController() {
		return controller;
	}

	public Loader<U> getLoader() {
		return loader;
	}
	
	/**
	 * Writes calibration images to the disk.
	 * 
	 * @param folder Folder in which to save the images
	 * @param results Calibration results
	 */
	protected void writeCalibrationToImages(String folder, Calibration results) {
		T baseline = controller.getArrayToImageConverter().getImage(results.getBaseline(), results.getWidth(), results.getHeight(), 0);
		getController().getImageSaver().saveAsTiff(baseline,folder+File.separator+BASELINE);
		
		T dc_per_sec = controller.getArrayToImageConverter().getImage(results.getDcPerSec(), results.getWidth(), results.getHeight(), 0);
		getController().getImageSaver().saveAsTiff(dc_per_sec,folder+File.separator+DCPERSEC);
		
		T gain = controller.getArrayToImageConverter().getImage(results.getGain(), results.getWidth(), results.getHeight(), 0);
		getController().getImageSaver().saveAsTiff(gain,folder+File.separator+GAIN);
		
		T rn_sq = controller.getArrayToImageConverter().getImage(results.getRnSq(), results.getWidth(), results.getHeight(), 0);
		getController().getImageSaver().saveAsTiff(rn_sq,folder+File.separator+RNSQ);
		
		T tn_sq_per_sec = controller.getArrayToImageConverter().getImage(results.getTnSqPerSec(), results.getWidth(), results.getHeight(), 0);
		getController().getImageSaver().saveAsTiff(tn_sq_per_sec,folder+File.separator+TNSQPERSEC);
		
		T r_sq_avg = controller.getArrayToImageConverter().getImage(results.getRSqAvg(), results.getWidth(), results.getHeight(), 0);
		getController().getImageSaver().saveAsTiff(r_sq_avg,folder+File.separator+RSQAVG);
		
		T r_sq_var = controller.getArrayToImageConverter().getImage(results.getRSqVar(), results.getWidth(), results.getHeight(), 0);
		getController().getImageSaver().saveAsTiff(r_sq_var,folder+File.separator+RSQVAR);
		
		T r_sq_gain = controller.getArrayToImageConverter().getImage(results.getRSqGain(), results.getWidth(), results.getHeight(), 0);
		getController().getImageSaver().saveAsTiff(r_sq_gain,folder+File.separator+RSQGAIN);
	}

	/**
	 * Compute the average and variance images from the Loader input and store them in avgs and vars.
	 */
	protected abstract AvgVarStacks<T> computeAvgAndVar();
}
