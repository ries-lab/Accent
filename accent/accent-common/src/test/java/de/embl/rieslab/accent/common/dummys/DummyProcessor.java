package de.embl.rieslab.accent.common.dummys;

import de.embl.rieslab.accent.common.data.image.FloatImage;
import de.embl.rieslab.accent.common.interfaces.Loader;
import de.embl.rieslab.accent.common.interfaces.PipelineController;
import de.embl.rieslab.accent.common.processor.CalibrationProcessor;

public class DummyProcessor extends CalibrationProcessor {

	public DummyProcessor(String folder, PipelineController controller, Loader loader) {
		super(folder, controller, loader);
	}

	@Override
	protected void computeAvgAndVar(Loader loader, FloatImage[] avgs, FloatImage[] vars, int[] stackSizes) {
		// Do nothing
	}		
}