package de.embl.rieslab.accent.common.dummys;

import de.embl.rieslab.accent.common.data.image.AvgVarStacks;
import de.embl.rieslab.accent.common.data.image.BareImage;
import de.embl.rieslab.accent.common.data.image.FloatImage;
import de.embl.rieslab.accent.common.interfaces.pipeline.Loader;
import de.embl.rieslab.accent.common.interfaces.pipeline.PipelineController;
import de.embl.rieslab.accent.common.processor.CalibrationProcessor;

public class DummyProcessor extends CalibrationProcessor<FloatImage> {

	public DummyProcessor(String folder, PipelineController<FloatImage> controller, DummyLoader loader) {
		super(folder, controller, loader);
	}

	@Override
	protected AvgVarStacks<FloatImage> computeAvgAndVar() {
		Loader<FloatImage> loader = getLoader();
		
		FloatImage[] avgs = new FloatImage[loader.getNumberOfChannels()];
		FloatImage[] vars = new FloatImage[loader.getNumberOfChannels()];

		for(int i=0;i<loader.getNumberOfChannels();i++) {
			avgs[i] = ((DummyLoader) loader).avgs[i];
			vars[i] = ((DummyLoader) loader).vars[i];
		}
		return new AvgVarStacks(avgs, vars);
	}		
}