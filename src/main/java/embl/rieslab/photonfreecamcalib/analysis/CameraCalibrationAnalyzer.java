package main.java.embl.rieslab.photonfreecamcalib.analysis;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingWorker;

import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.micromanager.data.Coords;
import org.micromanager.data.internal.DefaultCoords;

import ij.ImagePlus;
import ij.io.Opener;
import main.java.embl.rieslab.photonfreecamcalib.PipelineController;
import main.java.embl.rieslab.photonfreecamcalib.calibration.Calibration;
import main.java.embl.rieslab.photonfreecamcalib.calibration.CalibrationIO;
import main.java.embl.rieslab.photonfreecamcalib.utils.utils;

public class CameraCalibrationAnalyzer extends SwingWorker<Integer, Integer> implements Analyzer {
	
	private String[] avgs, vars;
	private PipelineController controller;
	private boolean stop = false;
	private boolean running = false;
	private Calibration results;
	
	public CameraCalibrationAnalyzer(String[] avgs, String[] vars, PipelineController controller) {
		if(avgs == null || vars == null || controller == null) {
			throw new NullPointerException();
		}
		
		if(avgs.length != vars.length) {
			throw new IllegalArgumentException();
		}
		
		this.avgs = avgs;
		this.vars = vars;
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

		publish(0);

		int numPoints = avgs.length;
		
		Coords.CoordsBuilder builder = new DefaultCoords.Builder();
		builder.channel(0).stagePosition(0).time(0).z(0);

		Opener opener = new Opener();
		ImagePlus im = opener.openImage(avgs[0]);

		int width = im.getWidth();
		int height = im.getHeight();

		ArrayList<double[][]> avg_exp_list = new ArrayList<double[][]>();
		ArrayList<double[][]> var_exp_list = new ArrayList<double[][]>();
		ArrayList<double[][]> var_avg_list = new ArrayList<double[][]>();
		for (int k = 0; k < height * width; k++) {
			avg_exp_list.add(new double[numPoints][2]);
			var_exp_list.add(new double[numPoints][2]);
			var_avg_list.add(new double[numPoints][2]);
		}

		float[] avg_pix;
		float[] var_pix;
		for (int z = 0; z < numPoints; z++) { // loops over the different exposures
			builder.z(z);
			avg_pix = (float[]) opener.openImage(avgs[z]).getProcessor().convertToFloatProcessor().getPixels();
			var_pix = (float[]) opener.openImage(vars[z]).getProcessor().convertToFloatProcessor().getPixels();
			
			if(avg_pix == null || var_pix == null) {
				stop = true;
				break;
			}

			int exposure = utils.extractExposurefromTiff(avgs[z]);

			for (int j = 0; j < height; j++) {

				if (stop) {
					break;
				}

				for (int i = 0; i < width; i++) {

					if (stop) {
						break;
					}

					avg_exp_list.get(i + width * j)[z][0] = exposure;
					avg_exp_list.get(i + width * j)[z][1] = avg_pix[i + width * j];
					var_exp_list.get(i + width * j)[z][0] = exposure;
					var_exp_list.get(i + width * j)[z][1] = var_pix[i + width * j];
					var_avg_list.get(i + width * j)[z][0] = avg_pix[i + width * j];
					var_avg_list.get(i + width * j)[z][1] = var_pix[i + width * j];
				}
			}
		}

		if (!stop) {
			SimpleRegression[] avg_exp_reg = new SimpleRegression[height * width];
			SimpleRegression[] var_exp_reg = new SimpleRegression[height * width];
			SimpleRegression[] var_avg_reg = new SimpleRegression[height * width];
			double[] baseline = new double[height * width];
			double[] dcpt = new double[height * width];
			double[] rnsq = new double[height * width];
			double[] tnsqpt = new double[height * width];
			double[] gain = new double[height * width];
			int totlength = width * height;
			for (int i = 0; i < totlength; i++) {
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

				publish(100 * i / totlength);
			}

			if (!stop) {
				// saves results in the calibration
				results.width = width;
				results.height = height;
				results.baseline = baseline;
				results.DC_per_t = dcpt;
				results.gain = gain;
				results.RN_sq = rnsq;
				results.TN_sq_per_t = tnsqpt;

				// Writes configuration to disk
				CalibrationIO.write(new File(new File(avgs[0]).getParentFile().getAbsolutePath()+"\\results.calb"), results);
				
				
			}

		}


		if(stop) {
			publish(-2);
		} else {
			publish(-1);
		}
		
		return 0;
	}

	@Override
	protected void process(List<Integer> chunks) {
		for(Integer i:chunks) {
			if(i == 0) {
				controller.analysisHasStarted();
			} else if(i == -1) {
				controller.analysisHasEnded();
			} else if(i == -2) {
				controller.analysisHasStopped();
			} else {
				int progress = i;
				controller.updateAnalyzerProgress(progress);
			}
		}
	}
	
	public static int extractExposure(String dataFolder) {
		
		if(dataFolder.substring(dataFolder.length()-2).equals("ms")) {
			int length = 0;
			int index  = dataFolder.length()-3;

			while(Character.isDigit(dataFolder.charAt(index))) {
				length ++;
				index --;
			}

			return Integer.parseInt(dataFolder.substring(index+1, index+1+length));
		}
		
		return 0;
	}
	
	public static String getParentPath(String dataFolder) {
		return new File(dataFolder).getParent();
	}


	@Override
	public Calibration getResults() {
		return results;
	}
}
