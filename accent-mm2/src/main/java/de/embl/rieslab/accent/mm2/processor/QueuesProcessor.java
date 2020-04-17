package de.embl.rieslab.accent.mm2.processor;

import de.embl.rieslab.accent.common.data.image.AvgVarStacks;
import de.embl.rieslab.accent.common.interfaces.pipeline.Loader;
import de.embl.rieslab.accent.common.interfaces.pipeline.PipelineController;
import de.embl.rieslab.accent.common.processor.CalibrationProcessor;
import de.embl.rieslab.accent.mm2.data.image.BareImage;
import de.embl.rieslab.accent.mm2.data.image.FloatImage;
import de.embl.rieslab.accent.mm2.interfaces.AcquisitionController;
import de.embl.rieslab.accent.mm2.loader.QueuesLoader;

public class QueuesProcessor extends CalibrationProcessor<BareImage, FloatImage> {
	
	private AcquisitionController acqcontroller;
	
	public QueuesProcessor(String folder, PipelineController<BareImage, FloatImage> controller, QueuesLoader loader, AcquisitionController acqcontroller) {
		super(folder, controller, loader);
		this.acqcontroller = acqcontroller;
	}

	@Override
	protected AvgVarStacks<FloatImage> computeAvgAndVar() {
		Loader<BareImage> loader = getLoader();
		
		FloatImage[] avgs = new FloatImage[loader.getNumberOfChannels()];
		FloatImage[] vars = new FloatImage[loader.getNumberOfChannels()];
		int[] stackSizes = new int[loader.getNumberOfChannels()];
				
		boolean done = false;
		boolean update = false;
		while(!done) {
			boolean allEmpty = true;
			for(int q=0;q<loader.getNumberOfChannels();q++) {
				if(loader.hasNext(q)) {
					
					if(stop) {
						return null;
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
			if(allEmpty && acqcontroller.isAcquisitionDone()) {
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
		
		return new AvgVarStacks<FloatImage>(avgs, vars);
	}

}
