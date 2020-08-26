package de.embl.rieslab.accent.mm2.processor;

import de.embl.rieslab.accent.common.data.image.AvgVarStacks;
import de.embl.rieslab.accent.common.data.roi.SimpleRoi;
import de.embl.rieslab.accent.common.interfaces.pipeline.Loader;
import de.embl.rieslab.accent.common.interfaces.pipeline.PipelineController;
import de.embl.rieslab.accent.common.processor.CalibrationProcessor;
import de.embl.rieslab.accent.mm2.data.image.BareImage;
import de.embl.rieslab.accent.mm2.data.image.FloatImage;

public class StacksProcessor extends CalibrationProcessor<BareImage, FloatImage> {

	public StacksProcessor(String folder, SimpleRoi roi, PipelineController<BareImage, FloatImage> controller, Loader<BareImage> loader) {
		super(folder, roi, controller, loader);
	}

	@Override
	protected AvgVarStacks<FloatImage> computeAvgAndVar() {
		Loader<BareImage> loader = getLoader();
		
		FloatImage[] avgs = new FloatImage[loader.getNumberOfChannels()];
		FloatImage[] vars = new FloatImage[loader.getNumberOfChannels()];
		int[] stackSizes = new int[loader.getNumberOfChannels()];
		
		double percentile = 75./(loader.getNumberOfChannels()+1);
				
		for(int q=0; q<loader.getNumberOfChannels(); q++) {
			
			loader.openChannel(q);
			
			while(loader.hasNext(q)) {
				
				if(stop) {
					return null;
				}
				
				// first round
				if(avgs[q] == null) {
					stackSizes[q] = 1;
					avgs[q] = new FloatImage(loader.getNext(q));
					vars[q] = new FloatImage(avgs[q]);
					vars[q].square();
				} else {
					// poll the newest image
					BareImage im = loader.getNext(q);
					avgs[q].addPixels(im);
					vars[q].addSquarePixels(im);
					stackSizes[q]++;
				}

				if(stackSizes[q] % 100 == 0) {
					int progress = (int) (percentile * q + percentile * stackSizes[q] / loader.getChannelLength());
					showProgressOnEDT(CalibrationProcessor.PROGRESS, "Stack "+(q+1)+"/"+loader.getNumberOfChannels()+", frame "+String.valueOf(stackSizes[q])+"/"+String.valueOf(loader.getChannelLength()), progress);
				}
			}
			if(avgs[q] != null && vars[q] != null) {
				avgs[q].dividePixels(stackSizes[q]);
				vars[q].toVariance(avgs[q].getImage(), stackSizes[q]);
			}
			
		}
		return new AvgVarStacks<FloatImage>(avgs, vars);
	}
}
