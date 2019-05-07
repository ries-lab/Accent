package main.java.embl.rieslab.accent.mm2.processor;

import main.java.embl.rieslab.accent.PipelineController;
import main.java.embl.rieslab.accent.common.data.image.FloatImage;
import main.java.embl.rieslab.accent.common.interfaces.Loader;
import main.java.embl.rieslab.accent.common.processor.CalibrationProcessor;
import main.java.embl.rieslab.accent.mm2.data.image.ImageExposurePair;
import main.java.embl.rieslab.accent.mm2.data.image.ImageToFloatImage;
import main.java.embl.rieslab.accent.mm2.loader.QueuesLoader;

public class QueuesProcessor extends CalibrationProcessor<ImageExposurePair> {
	
	public QueuesProcessor(String folder, PipelineController controller, QueuesLoader loader) {
		super(folder, controller, loader);
	}

	@Override
	protected void computeAvgAndVar(Loader<ImageExposurePair> loader, FloatImage[] avgs,
			FloatImage[] vars, int[] stackSizes) {
		
		
		ImageToFloatImage[] avgs_wrap = new ImageToFloatImage[loader.getSize()];
		ImageToFloatImage[] vars_wrap = new ImageToFloatImage[loader.getSize()];
		
		boolean done = false;
		while(!done) {
			boolean allEmpty = true;
			for(int q=0;q<loader.getSize();q++) {
				if(loader.hasNext(q)) {
					allEmpty = false;
					
					// first round
					if(avgs_wrap[q] == null) {
						stackSizes[q] = 1;
						avgs_wrap[q] = new ImageToFloatImage(loader.getNext(q));
						vars_wrap[q] = new ImageToFloatImage(avgs_wrap[q]); // this is suboptimal as it first copies each pixel and then will square it
						vars_wrap[q].square();
					} else {
						// poll the newest image
						ImageExposurePair im = loader.getNext(q);
						avgs_wrap[q].addPixels(im.getImage());
						vars_wrap[q].addSquarePixels(im.getImage());
						stackSizes[q]++;
					}
					
					showProgressOnEDT(CalibrationProcessor.PROGRESS, "Processing frame: "+stackSizes[q], 0);

				}
			}
			
			// all queues were empty
			if(allEmpty && getController().isAcquisitionDone()) {
				done = true;
			}	
		}
		
		// renormalizes and computes vars
		for(int q=0;q<loader.getSize();q++) {
			if(avgs_wrap[q] != null && vars_wrap[q] != null) {
				avgs_wrap[q].dividePixels(stackSizes[q]);
				vars_wrap[q].toVariance(avgs_wrap[q], stackSizes[q]);

				avgs[q] = avgs_wrap[q].getFloatImage();
				vars[q] = vars_wrap[q].getFloatImage();
			}
		}
	}

}
