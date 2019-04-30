package main.java.embl.rieslab.accent.processing;

import main.java.embl.rieslab.accent.PipelineController;
import main.java.embl.rieslab.accent.data.FloatImage;
import main.java.embl.rieslab.accent.data.ImageExposurePair;
import main.java.embl.rieslab.accent.loader.Loader;
import main.java.embl.rieslab.accent.loader.QueuesLoader;

public class QueuesProcessor extends CalibrationProcessor<ImageExposurePair> {
	
	public QueuesProcessor(String folder, PipelineController controller, QueuesLoader loader) {
		super(folder, controller, loader);
	}

	@Override
	protected void computeAvgAndVar(Loader<ImageExposurePair> loader, FloatImage[] avgs,
			FloatImage[] vars, int[] stackSizes) {
		
		boolean done = false;
		while(!done) {
			boolean allEmpty = true;
			for(int q=0;q<loader.getSize();q++) {
				if(loader.hasNext(q)) {
					allEmpty = false;
					
					// first round
					if(avgs[q] == null) {
						stackSizes[q] = 1;
						avgs[q] = new FloatImage(loader.getNext(q));
						vars[q] = new FloatImage(avgs[q]); // this is suboptimal as it first copies each pixel and then will square it
						vars[q].square();
					} else {
						// poll the newest image
						ImageExposurePair im = loader.getNext(q);
						avgs[q].addPixels(im.getImage());
						vars[q].addSquarePixels(im.getImage());
						stackSizes[q]++;
					}
				}
			}
			
			// all queues were empty
			if(allEmpty && getController().isAcquisitionDone()) {
				done = true;
			}	
		}
		
		// renormalizes and computes vars
		for(int q=0;q<loader.getSize();q++) {
			avgs[q].dividePixels(stackSizes[q]);
			vars[q].toVariance(avgs[q].getImage(), stackSizes[q]);
		}
	}

}
