package de.embl.rieslab.accent.fiji.processor;

import de.embl.rieslab.accent.common.data.image.AvgVarStacks;
import de.embl.rieslab.accent.common.interfaces.pipeline.Loader;
import de.embl.rieslab.accent.common.interfaces.pipeline.PipelineController;
import de.embl.rieslab.accent.common.processor.CalibrationProcessor;
import de.embl.rieslab.accent.fiji.data.image.PlaneImg;
import de.embl.rieslab.accent.fiji.data.image.StackImg;

public class ImgProcessor extends CalibrationProcessor<StackImg, PlaneImg>{

	public ImgProcessor(String folder, PipelineController<StackImg, PlaneImg> controller, Loader<StackImg> loader) {
		super(folder, controller, loader);
	}

	@Override
	protected AvgVarStacks<PlaneImg> computeAvgAndVar() {
		Loader<StackImg> loader = getLoader();

		todo
		
		return null;
	}

}
