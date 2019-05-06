package main.java.embl.rieslab.accent.processing;

import main.java.embl.rieslab.accent.PipelineController;
import main.java.embl.rieslab.accent.data.images.FloatImage;
import main.java.embl.rieslab.accent.loader.Loader;

public class FloatImageProcessor extends CalibrationProcessor<FloatImage>{

	public FloatImageProcessor(String folder, PipelineController controller, Loader<FloatImage> loader) {
		super(folder, controller, loader);
	}

	@Override
	protected void computeAvgAndVar(Loader<FloatImage> loader, FloatImage[] avgs, FloatImage[] vars, int[] stackSizes) {
		
		double percentile = 75./(loader.getSize()+1);
		
		for(int q=0; q<loader.getSize(); q++) {
			
			boolean b = loader.openChannel(q);
			if(b) {
				while(loader.hasNext(q)) {
					
					// first round
					if(avgs[q] == null) {
						stackSizes[q] = 1;
						avgs[q] = new FloatImage(loader.getNext(q));
						vars[q] = new FloatImage(avgs[q]);
						vars[q].square();
					} else {
						// poll the newest image
						FloatImage im = loader.getNext(q);
						avgs[q].addPixels(im.getImage().getFloatArray());
						vars[q].addSquarePixels(im.getImage().getFloatArray());
						stackSizes[q]++;
					}
					
					int progress = (int) (percentile * loader.getSize() + percentile * stackSizes[q] / loader.getChannelLength());
					showProgressOnEDT(CalibrationProcessor.PROGRESS, "Stack "+q+"/"+loader.getSize()+" ", stackSizes[q], loader.getChannelLength(), progress);
					
				}
				
				if(avgs[q] != null && vars[q] != null) {
					avgs[q].dividePixels(stackSizes[q]);
					vars[q].toVariance(avgs[q].getImage(), stackSizes[q]);
				}
			}
		}
	}

}