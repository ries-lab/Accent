package de.embl.rieslab.accent.common.generator;

import java.io.File;
import java.util.List;

import javax.swing.SwingWorker;

import de.embl.rieslab.accent.common.data.calibration.Calibration;
import de.embl.rieslab.accent.common.data.calibration.CalibrationMap;
import de.embl.rieslab.accent.common.interfaces.data.ArrayToImage;
import de.embl.rieslab.accent.common.interfaces.data.CalibrationImage;
import de.embl.rieslab.accent.common.interfaces.data.ImageSaver;
import de.embl.rieslab.accent.common.interfaces.data.RawImage;
import de.embl.rieslab.accent.common.interfaces.pipeline.Generator;
import de.embl.rieslab.accent.common.interfaces.pipeline.PipelineController;

/**
 * Generates average and variance maps.
 * 
 * @author Joran Deschamps
 *
 */
public class AvgVarMapsGenerator<U extends RawImage, T extends CalibrationImage> extends SwingWorker<Integer, Integer> implements Generator {

	private PipelineController<U,T> controller;
	private ArrayToImage<T> imconverter;
	private ImageSaver<T> imsaver;
	private Calibration calib;
	private double[] exposures;
	private boolean running_ = false;
	private String path;
	
	/**
	 * Constructor.
	 * @param controller
	 */
	public AvgVarMapsGenerator(PipelineController<U,T> controller) {
		if(controller == null)
			throw new NullPointerException("Controller cannot be null.");
		
		this.controller = controller;
		this.imconverter = controller.getImageConverter();
		this.imsaver = controller.getImageSaver();
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

			T avg_im = imconverter.getImage(3, CalibrationMap.generateAvgMap(calib, exp), calib.getWidth(), calib.getHeight(), exp);
			T var_im = imconverter.getImage(3, CalibrationMap.generateVarMap(calib, exp), calib.getWidth(), calib.getHeight(), exp);

			if(Double.compare(exp, (int) exp) == 0){
				imsaver.saveAsTiff(avg_im, path+"\\"+"generated_Avg_"+((int) exp)+"ms.tiff");
				imsaver.saveAsTiff(var_im, path+"\\"+"generated_Var_"+((int) exp)+"ms.tiff");
			} else {
				imsaver.saveAsTiff(avg_im, path+"\\"+"generated_Avg_"+exp+"ms.tiff");
				imsaver.saveAsTiff(var_im, path+"\\"+"generated_Var_"+exp+"ms.tiff");
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
