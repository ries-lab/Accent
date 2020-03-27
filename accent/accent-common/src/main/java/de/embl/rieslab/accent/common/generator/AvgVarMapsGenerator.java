package de.embl.rieslab.accent.common.generator;

import java.io.File;
import java.util.List;

import javax.swing.SwingWorker;

import de.embl.rieslab.accent.common.data.calibration.Calibration;
import de.embl.rieslab.accent.common.data.calibration.CalibrationMap;
import de.embl.rieslab.accent.common.data.image.FloatImage;
import de.embl.rieslab.accent.common.interfaces.Generator;
import de.embl.rieslab.accent.common.interfaces.PipelineController;

/**
 * Generates average and variance maps.
 * 
 * @author Joran Deschamps
 *
 */
public class AvgVarMapsGenerator extends SwingWorker<Integer, Integer> implements Generator {

	private PipelineController controller;
	private Calibration calib;
	private double[] exposures;
	private boolean running_ = false;
	private String path;
	
	/**
	 * Constructor.
	 * @param controller
	 */
	public AvgVarMapsGenerator(PipelineController controller) {
		if(controller == null)
			throw new NullPointerException("Controller cannot be null.");
		
		this.controller = controller;
	}

	@Override
	public boolean isRunning() {
		return running_;
	}

	@Override
	public void generate(String path, Calibration calibration, double[] exposures) {
		if(calibration == null || exposures == null || path == null) {
			throw new NullPointerException();
		}
		
		if(exposures.length == 0 || !(new File(path).isDirectory())) {
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
		for(double exp: exposures) {

			publish(counter ++);

			FloatImage avg_im = CalibrationMap.generateAvgMap(calib, exp);
			FloatImage var_im = CalibrationMap.generateVarMap(calib, exp);
			if(Double.compare(exp, (int) exp) == 0){
				avg_im.saveAsTiff(path+"\\"+"generated_Avg_"+((int) exp)+"ms.tiff");

				var_im.saveAsTiff(path+"\\"+"generated_Var_"+((int) exp)+"ms.tiff");
				
			} else {
				avg_im.saveAsTiff(path+"\\"+"generated_Avg_"+exp+"ms.tiff");

				var_im.saveAsTiff(path+"\\"+"generated_Var_"+exp+"ms.tiff");
			}
			
		}
		
		publish(-1);
		return 0;
	}
	@Override
	protected void process(List<Integer> chunks) {
		for(Integer i:chunks) {
			if(i == -1) {
				running_ = false;
				controller.updateGeneratorProgress("Done.");
			} else {
				controller.updateGeneratorProgress("Exposure: "+i+"/"+exposures.length);
			}
		}
	}
}
