package main.java.embl.rieslab.accent.mm2.processor;

import main.java.embl.rieslab.accent.PipelineController;
import main.java.embl.rieslab.accent.common.data.image.FloatImage;
import main.java.embl.rieslab.accent.common.interfaces.Loader;
import main.java.embl.rieslab.accent.common.processor.CalibrationProcessor;
import main.java.embl.rieslab.accent.mm2.data.image.ImageExposurePair;
import main.java.embl.rieslab.accent.mm2.data.image.ImageToFloatImage;
import main.java.embl.rieslab.accent.mm2.loader.MMStacksLoader;

public class MMStacksProcessor extends CalibrationProcessor<ImageExposurePair>{

	public MMStacksProcessor(String folder, PipelineController controller, MMStacksLoader loader) {
		super(folder, controller, loader);
	}

	@Override
	protected void computeAvgAndVar(Loader<ImageExposurePair> loader, FloatImage[] avgs,
			FloatImage[] vars, int[] stackSizes) {
		
		double percentile = 75./(loader.getSize()+1);
		
		ImageToFloatImage[] avgs_wrap = new ImageToFloatImage[loader.getSize()];
		ImageToFloatImage[] vars_wrap = new ImageToFloatImage[loader.getSize()];
		for(int q=0; q<loader.getSize(); q++) {
			
			loader.openChannel(q);
			
			while(loader.hasNext(q)) {
				
				// first round
				if(avgs_wrap[q] == null) {
					stackSizes[q] = 1;
					avgs_wrap[q] = new ImageToFloatImage(loader.getNext(q));
					vars_wrap[q] = new ImageToFloatImage(avgs_wrap[q]);
					vars_wrap[q].square();
				} else {
					// poll the newest image
					ImageExposurePair im = loader.getNext(q);
					avgs_wrap[q].addPixels(im.getImage());
					vars_wrap[q].addSquarePixels(im.getImage());
					stackSizes[q]++;
				}

				int progress = (int) (percentile * q + percentile * stackSizes[q] / loader.getChannelLength());
				showProgressOnEDT(CalibrationProcessor.PROGRESS, "Stack "+(q+1)+"/"+loader.getSize()+", frame ", stackSizes[q], loader.getChannelLength(), progress);
			}
			if(avgs_wrap[q] != null && vars[q] != null) {

				avgs_wrap[q].dividePixels(stackSizes[q]);
				vars_wrap[q].toVariance(avgs_wrap[q], stackSizes[q]);

				avgs[q] = avgs_wrap[q].getFloatImage();
				vars[q] = vars_wrap[q].getFloatImage();
			}
			
		}
	}

}
