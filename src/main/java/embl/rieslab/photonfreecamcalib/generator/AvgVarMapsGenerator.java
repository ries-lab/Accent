package main.java.embl.rieslab.photonfreecamcalib.generator;

import java.io.File;
import java.util.List;

import javax.swing.SwingWorker;

import ij.ImagePlus;
import ij.io.FileSaver;
import main.java.embl.rieslab.photonfreecamcalib.PipelineController;
import main.java.embl.rieslab.photonfreecamcalib.calibration.Calibration;
import main.java.embl.rieslab.photonfreecamcalib.calibration.CalibrationMap;
import main.java.embl.rieslab.photonfreecamcalib.data.FloatImage;

public class AvgVarMapsGenerator extends SwingWorker<Integer, Integer> implements Generator {

	private PipelineController controller;
	private Calibration calib;
	private Integer[] exposures;
	private boolean running_ = false;
	private String path;
		
	public AvgVarMapsGenerator(PipelineController controller) {
		this.controller = controller;
	}
	
	@Override
	public boolean isRunning() {
		return running_;
	}

	@Override
	public void generate(String path, Calibration calibration, Integer[] exposures) {
		if(calibration == null || exposures == null || path == null) {
			throw new NullPointerException();
		}
		
		if(exposures.length == 0 || !( new File(path).isDirectory())) {
			throw new IllegalArgumentException();
		}
		
		calib = calibration;
		this.exposures = exposures;
		this.path = path;
		running_ = true;
		this.execute();
	}

	@Override
	protected Integer doInBackground() throws Exception {
		
		int counter = 0;
		for(Integer exp: exposures) {

			publish(counter ++);
			
			FloatImage avg_im = CalibrationMap.generateAvgMap(calib, exp);
			FloatImage var_im = CalibrationMap.generateVarMap(calib, exp);
			
			FileSaver avg_saver = new FileSaver(new ImagePlus("Average_"+exp+"ms",avg_im.getProcessor())); 
			avg_saver.saveAsTiff(path+"\\"+"Avg_"+exp+"ms.tiff");
			
			FileSaver var_saver = new FileSaver(new ImagePlus("Variance_"+exp+"ms",var_im.getProcessor())); 
			var_saver.saveAsTiff(path+"\\"+"Var_"+exp+"ms.tiff");
			
		}
		
		publish(-1);
		return 0;
	}
	@Override
	protected void process(List<Integer> chunks) {
		for(Integer i:chunks) {
			if(i == -1) {
				running_ = false;
				controller.setGeneratorProgress("Done.");
			} else {
				controller.setGeneratorProgress("Exposure: "+i+"/"+exposures.length);
			}
		}
	}
}
