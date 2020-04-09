package de.embl.rieslab.accent.fiji.processor;

import de.embl.rieslab.accent.common.data.image.AvgVarStacks;
import de.embl.rieslab.accent.common.interfaces.pipeline.Loader;
import de.embl.rieslab.accent.common.interfaces.pipeline.PipelineController;
import de.embl.rieslab.accent.common.processor.CalibrationProcessor;
import de.embl.rieslab.accent.fiji.data.image.ImgCalibrationImage;

public class ImgProcessor extends CalibrationProcessor<ImgCalibrationImage>{

	public ImgProcessor(String folder, PipelineController<ImgCalibrationImage> controller, Loader<ImgCalibrationImage> loader) {
		super(folder, controller, loader);
	}

	@Override
	protected AvgVarStacks<ImgCalibrationImage> computeAvgAndVar() {
		Loader<ImgCalibrationImage> loader = getLoader();

		
		
		return null;
	}

}
