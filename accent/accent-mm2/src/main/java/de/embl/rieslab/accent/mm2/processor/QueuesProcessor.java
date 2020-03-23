package de.embl.rieslab.accent.mm2.processor;

import de.embl.rieslab.accent.common.data.image.BareImage;
import de.embl.rieslab.accent.common.data.image.FloatImage;
import de.embl.rieslab.accent.common.interfaces.Loader;
import de.embl.rieslab.accent.common.interfaces.PipelineController;
import de.embl.rieslab.accent.common.processor.CalibrationProcessor;
import de.embl.rieslab.accent.mm2.loader.QueuesLoader;

public class QueuesProcessor extends CalibrationProcessor {
	
	public QueuesProcessor(String folder, PipelineController controller, QueuesLoader loader) {
		super(folder, controller, loader);
	}

	@Override
	protected void computeAvgAndVar(Loader loader, FloatImage[] avgs,
			FloatImage[] vars, int[] stackSizes) {
				
		boolean done = false;
		boolean update = false;
		while(!done) {
			boolean allEmpty = true;
			for(int q=0;q<loader.getNumberOfChannels();q++) {
				if(loader.hasNext(q)) {
					
					if(stop) {
						return;
					}
					
					allEmpty = false;
					
					// first round
					if(avgs[q] == null) {
						stackSizes[q] = 1;
						avgs[q] = new FloatImage(loader.getNext(q));
						vars[q] = new FloatImage(avgs[q]); // this is suboptimal as it first copies each pixel and then will square it
						vars[q].square();
					} else {
						// poll the newest image
						BareImage im = loader.getNext(q);
						avgs[q].addPixels(im);
						vars[q].addSquarePixels(im);
						stackSizes[q]++;
					}
					
					update = true;
				}
			}
			
			if(update) {
				showProgressOnEDT(CalibrationProcessor.PROGRESS, "Processing frame: "+(stackSizes[0]-1), 0);
				update = false;
			}
			
			// all queues were empty
			if(allEmpty && getController().isAcquisitionDone()) {
				done = true;
			}	
		}
		
		// renormalizes and computes vars
		for(int q=0;q<loader.getNumberOfChannels();q++) {
			if(avgs[q] != null && vars[q] != null) {
				avgs[q].dividePixels(stackSizes[q]);
				vars[q].toVariance(avgs[q].getImage(), stackSizes[q]);
			}
		}
	}

}
