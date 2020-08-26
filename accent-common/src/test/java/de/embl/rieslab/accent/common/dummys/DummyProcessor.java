package de.embl.rieslab.accent.common.dummys;

import de.embl.rieslab.accent.common.data.image.AvgVarStacks;
import de.embl.rieslab.accent.common.data.roi.SimpleRoi;
import de.embl.rieslab.accent.common.interfaces.pipeline.Loader;
import de.embl.rieslab.accent.common.interfaces.pipeline.PipelineController;
import de.embl.rieslab.accent.common.processor.CalibrationProcessor;

public class DummyProcessor extends CalibrationProcessor<DummyImage, DummyImage> {

	public DummyProcessor(String folder, SimpleRoi roi, PipelineController<DummyImage, DummyImage> controller, DummyLoader loader) {
		super(folder, roi, controller, loader);
	}

	@Override
	protected AvgVarStacks<DummyImage> computeAvgAndVar() {
		Loader<DummyImage> loader = getLoader();
		
		DummyImage[] avgs = new DummyImage[loader.getNumberOfChannels()];
		DummyImage[] vars = new DummyImage[loader.getNumberOfChannels()];

		for(int i=0;i<loader.getNumberOfChannels();i++) {
			avgs[i] = ((DummyLoader) loader).avgs[i];
			vars[i] = ((DummyLoader) loader).vars[i];
		}
		return new AvgVarStacks<DummyImage>(avgs, vars);
	}
}