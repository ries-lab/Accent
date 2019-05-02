package main.java.embl.rieslab.accent.processing;

import main.java.embl.rieslab.accent.PipelineController;
import main.java.embl.rieslab.accent.data.FloatImage;
import main.java.embl.rieslab.accent.data.ImageProcessorExposurePair;
import main.java.embl.rieslab.accent.loader.Loader;

public class IJProcessor extends CalibrationProcessor<ImageProcessorExposurePair>{

	public IJProcessor(String folder, PipelineController controller, Loader<ImageProcessorExposurePair> loader) {
		super(folder, controller, loader);
	}

	@Override
	protected void computeAvgAndVar(Loader<ImageProcessorExposurePair> loader, FloatImage[] avgs, FloatImage[] vars, int[] stackSizes) {
		
		
		for(int q=0; q<loader.getSize(); q++) {
			
			loader.openChannel(q);
			
			while(loader.hasNext(q)) {
				
				// first round
				if(avgs[q] == null) {
					stackSizes[q] = 1;
					avgs[q] = new FloatImage(loader.getNext(q));
					vars[q] = new FloatImage(avgs[q]);
					vars[q].square();
				} else {
					// poll the newest image
					ImageProcessorExposurePair im = loader.getNext(q);
					avgs[q].addPixels(im.getImage().getFloatArray());
					vars[q].addSquarePixels(im.getImage().getFloatArray());
					stackSizes[q]++;
				}
				
			}
			
			avgs[q].dividePixels(stackSizes[q]);
			vars[q].toVariance(avgs[q].getImage(), stackSizes[q]);
			
		}
	}

}
