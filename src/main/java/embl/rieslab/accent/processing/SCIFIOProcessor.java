package main.java.embl.rieslab.accent.processing;

import main.java.embl.rieslab.accent.PipelineController;
import main.java.embl.rieslab.accent.data.FloatImage;
import main.java.embl.rieslab.accent.loader.Loader;
import net.imglib2.img.Img;

public class SCIFIOProcessor extends CalibrationProcessor<Img<?>>{

	public SCIFIOProcessor(String folder, PipelineController controller, Loader<Img<?>> loader) {
		super(folder, controller, loader);
	}

	@Override
	protected void computeAvgAndVar(Loader<Img<?>> loader, FloatImage[] avgs, FloatImage[] vars, int[] stackSizes) {
		
		
	}

}
