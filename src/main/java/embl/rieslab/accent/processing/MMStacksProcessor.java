package main.java.embl.rieslab.accent.processing;

import main.java.embl.rieslab.accent.PipelineController;
import main.java.embl.rieslab.accent.data.FloatImage;
import main.java.embl.rieslab.accent.data.ImageExposurePair;
import main.java.embl.rieslab.accent.loader.Loader;
import main.java.embl.rieslab.accent.loader.MMStacksLoader;

public class MMStacksProcessor extends CalibrationProcessor<ImageExposurePair>{

	public MMStacksProcessor(String folder, PipelineController controller, MMStacksLoader loader) {
		super(folder, controller, loader);
	}

	@Override
	protected void computeAvgAndVar(Loader<ImageExposurePair> loader, FloatImage[] avgs,
			FloatImage[] vars, int[] stackSizes) {
		
		double percentile = 75./(loader.getSize()+1);
		
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
					ImageExposurePair im = loader.getNext(q);
					avgs[q].addPixels(im.getImage());
					vars[q].addSquarePixels(im.getImage());
					stackSizes[q]++;
				}

				int progress = (int) (percentile * loader.getSize() + percentile * stackSizes[q] / loader.getChannelLength());
				showProgressOnEDT(CalibrationProcessor.PROGRESS, "Stack "+q+"/"+loader.getSize()+" ", stackSizes[q], loader.getChannelLength(), progress);
			}
			
			avgs[q].dividePixels(stackSizes[q]);
			vars[q].toVariance(avgs[q].getImage(), stackSizes[q]);
			
		}
	}

}
