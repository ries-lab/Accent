package de.embl.rieslab.accent.common.dummys;

import de.embl.rieslab.accent.common.data.image.AvgVarStacks;
import de.embl.rieslab.accent.common.data.image.FloatImage;
import de.embl.rieslab.accent.common.interfaces.Loader;
import de.embl.rieslab.accent.common.interfaces.PipelineController;
import de.embl.rieslab.accent.common.processor.CalibrationProcessor;

public class DummyProcessor extends CalibrationProcessor {

	public DummyProcessor(String folder, PipelineController controller, DummyLoader loader) {
		super(folder, controller, loader);
	}

	@Override
	protected AvgVarStacks computeAvgAndVar(Loader loader) {
		FloatImage[] avgs = new FloatImage[loader.getNumberOfChannels()];
		FloatImage[] vars = new FloatImage[loader.getNumberOfChannels()];

		for(int i=0;i<loader.getNumberOfChannels();i++) {
			avgs[i] = ((DummyLoader) loader).avgs[i];
			vars[i] = ((DummyLoader) loader).vars[i];
		}
		return new AvgVarStacks(avgs, vars);
	}		
}