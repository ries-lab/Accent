package de.embl.rieslab.accent.common.processor;

import de.embl.rieslab.accent.common.data.image.AvgVarStacks;
import de.embl.rieslab.accent.common.data.image.BareImage;
import de.embl.rieslab.accent.common.data.image.FloatImage;
import de.embl.rieslab.accent.common.interfaces.pipeline.Loader;
import de.embl.rieslab.accent.common.interfaces.pipeline.PipelineController;

public class StacksProcessor extends CalibrationProcessor<BareImage> {

	public StacksProcessor(String folder, PipelineController<BareImage> controller, Loader<BareImage> loader) {
		super(folder, controller, loader);
	}

	@Override
	protected AvgVarStacks computeAvgAndVar() {
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
					showProgressOnEDT(CalibrationProcessor.PROGRESS, "Stack "+(q+1)+"/"+loader.getNumberOfChannels()+", frame ", stackSizes[q], loader.getChannelLength(), progress);
				}
			}
			if(avgs[q] != null && vars[q] != null) {
				avgs[q].dividePixels(stackSizes[q]);
				vars[q].toVariance(avgs[q].getImage(), stackSizes[q]);
			}
			
		}
		return new AvgVarStacks(avgs, vars);
	}
}
