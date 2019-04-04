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
	
	private final static int START = 0;
	private final static int DONE = -1;
	private final static int STOP = -2;
	
	private final ArrayList<ArrayBlockingQueue<FloatImage>> queues;
	
	public ConcurrentCalibrationProcessor(String folder, ArrayList<ArrayBlockingQueue<FloatImage>> queues, PipelineController controller) {
		if(queues == null|| controller == null) {
			throw new NullPointerException();
		}
		
		for(int i=0;i<queues.size();i++) {
			if(queues.get(i) == null) {
				throw new NullPointerException();
			}
		}
		
		this.folder = folder;
		this.queues = queues;
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
		
		System.out.println("Queue size is "+queues.size());
		
		publish(START);	

		FloatImage[] avgs = new FloatImage[queues.size()];
		FloatImage[] vars = new FloatImage[queues.size()];
		int[] stackSizes = new int[queues.size()];
		
		boolean done = false;
		while(!done) {
			boolean allEmpty = true;
			for(int q=0;q<queues.size();q++) {
				if(!queues.get(q).isEmpty()) {
					System.out.println("Queue "+q+" has "+queues.get(q).size()+" elements");
					allEmpty = false;
					
					// first round
					if(avgs[q] == null) {
						System.out.println("First round with "+q);
						stackSizes[q] = 1;
						avgs[q] = queues.get(q).poll();
						vars[q] = avgs[q].copy();
						vars[q].square();
					} else {
						System.out.println("Not a first round");
						// poll the newest image
						FloatImage im = queues.get(q).poll();
						avgs[q].addPixels(im.getPixels());
						vars[q].addSquarePixels(im.getPixels());
						System.out.println("Round"+stackSizes[q]+" with "+q);
						stackSizes[q]++;
					}
				}
			}
			
			// if stopped by the user
			if(stop){
				System.out.println("Stopping demanded");
				done = true;
			}
			
			// all queues were empty
			if(allEmpty) {
				System.out.println("- All queues are empty, stopping ?");
				
				// waits 2 second, then test if all empty
				Thread.sleep(2000);
				done = true;
				for(int q=0;q<queues.size();q++) {
					if(!queues.get(q).isEmpty()) {
						done = false;
					}
				}
				if(done) {
					System.out.println("All queues are empty, stopping at size: "+stackSizes[0]);
				} else {
					System.out.println("Resume, queues are not empty anymore");
				}
			}
			
		}

		System.out.println("Done with queues");
		publish(33);
		
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
						avg_exp_list.get(x + avgs[0].getWidth() * y)[q][0] = avgs[q].getExposure();
						avg_exp_list.get(x + avgs[0].getWidth() * y)[q][1] = avgs[q].getProcessor().getf(x, y);
						var_exp_list.get(x + avgs[0].getWidth() * y)[q][0] = avgs[q].getExposure();
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
		
		publish(66);
		
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
				String parentFolder = new File(folder).getParentFile().getAbsolutePath();
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

		publish(99);
		
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
}