package de.embl.rieslab.accent.common.processor;

import de.embl.rieslab.accent.common.data.image.BareImage;
import de.embl.rieslab.accent.common.data.image.FloatImage;
import de.embl.rieslab.accent.common.interfaces.Loader;
import de.embl.rieslab.accent.common.interfaces.PipelineController;

public class StacksProcessor extends CalibrationProcessor {

	public StacksProcessor(String folder, PipelineController controller, Loader loader) {
		super(folder, controller, loader);
	}

	@Override
	protected void computeAvgAndVar(Loader loader, FloatImage[] avgs,
			FloatImage[] vars, int[] stackSizes) {
		
		double percentile = 75./(loader.getSize()+1);
		
		for(int q=0; q<loader.getSize(); q++) {
			
			loader.openChannel(q);
			
			while(loader.hasNext(q)) {
				
				if(stop) {
					return;
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

				int progress = (int) (percentile * q + percentile * stackSizes[q] / loader.getChannelLength());
				showProgressOnEDT(CalibrationProcessor.PROGRESS, "Stack "+(q+1)+"/"+loader.getSize()+", frame ", stackSizes[q], loader.getChannelLength(), progress);
			}
			if(avgs[q] != null && vars[q] != null) {
				avgs[q].dividePixels(stackSizes[q]);
				vars[q].toVariance(avgs[q].getImage(), stackSizes[q]);
			}
			
		}
	}

}
