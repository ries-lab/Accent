package main.java.embl.rieslab.accent.processing;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingWorker;

import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.regression.SimpleRegression;

import main.java.embl.rieslab.accent.PipelineController;
import main.java.embl.rieslab.accent.calibration.Calibration;
import main.java.embl.rieslab.accent.calibration.CalibrationIO;
import main.java.embl.rieslab.accent.data.FloatImage;
import main.java.embl.rieslab.accent.loader.Loader;

public abstract class CalibrationProcessor<T> extends SwingWorker<Integer, Integer> implements Processor {

	private PipelineController controller;
	private Loader<T> loader;
	private boolean stop = false;
	private boolean running = false;
	
	private Calibration results;
	private String calibPath;
	private final String folder;

	private long startTime, stopTime;
	
	private final static int START = 0;
	private final static int DONE = -1;
	private final static int STOP = -2;
	
	public CalibrationProcessor(String folder, PipelineController controller, Loader<T> loader) {
		this.folder = folder;
		this.controller = controller;
		this.loader = loader;
		
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
	public double getExecutionTime() {
		return ((double) stopTime-startTime)/1000.0;
	}


	@Override
	public String getCalibrationPath() {
		return calibPath;
	}


	@Override
	public Calibration getCalibration() {
		return results;
	}
	
	@Override
	protected Integer doInBackground() throws Exception {
		startTime = System.currentTimeMillis();
		publish(START);	

		FloatImage[] avgs = new FloatImage[loader.getSize()];
		FloatImage[] vars = new FloatImage[loader.getSize()];
		int[] stackSizes = new int[loader.getSize()];
		
		computeAvgAndVar(loader, avgs, vars, stackSizes);
		
		if(stop) {
			publish(STOP);
			return 0;
		} else {
			publish(1);
		}
		
		for(int q=0;q<loader.getSize();q++) {
			// save images
			avgs[q].saveAsTiff(folder + "/" + "Avg_" + avgs[q].getExposure() + "ms.tiff");
			vars[q].saveAsTiff(folder + "/" + "Var_" + avgs[q].getExposure() + "ms.tiff");
		}

		
		if(stop) {
			publish(STOP);
			return 0;
		} else {
			publish(2);
		}
		
		results = performLinearRegressions(folder, avgs, vars);
		calibPath = writeCalibrationToFile();
		writeCalibrationToImages();
		
		if(stop) {
			publish(STOP);
			return 0;
		} else {
			publish(3);
		}
		
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
		
		for (int i = 0; i < totalLength; i++) {
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
		

		Calibration results = new Calibration();
		// saves results in the calibration
		results.width = width;
		results.height = height;
		results.baseline = baseline;
		results.dc_per_sec = dcpt;
		results.gain = gain;
		results.rn_sq = rnsq;
		results.tn_sq_per_sec = tnsqpt;

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
		new FloatImage(results.width, results.height, results.baseline, 0).saveAsTiff(folder+"\\"+"Baseline.tiff");
		new FloatImage(results.width, results.height, results.dc_per_sec, 0).saveAsTiff(folder+"\\"+"DC_per_sec.tiff");
		new FloatImage(results.width, results.height, results.gain, 0).saveAsTiff(folder+"\\"+"Gain.tiff");
		new FloatImage(results.width, results.height, results.rn_sq, 0).saveAsTiff(folder+"\\"+"RN_sq.tiff");
		new FloatImage(results.width, results.height, results.tn_sq_per_sec, 0).saveAsTiff(folder+"\\"+"TN_sq_per_sec.tiff");
	}
	
	protected PipelineController getController() {
		return controller;
	}
}
