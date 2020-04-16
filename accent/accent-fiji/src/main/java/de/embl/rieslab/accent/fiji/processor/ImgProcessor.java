package de.embl.rieslab.accent.fiji.processor;

import de.embl.rieslab.accent.common.data.image.AvgVarStacks;
import de.embl.rieslab.accent.common.interfaces.pipeline.Loader;
import de.embl.rieslab.accent.common.interfaces.pipeline.PipelineController;
import de.embl.rieslab.accent.common.processor.CalibrationProcessor;
import de.embl.rieslab.accent.fiji.data.image.PlaneImg;
import de.embl.rieslab.accent.fiji.data.image.StackImg;
import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;

public class ImgProcessor extends CalibrationProcessor<StackImg, PlaneImg>{

	public ImgProcessor(String folder, PipelineController<StackImg, PlaneImg> controller, Loader<StackImg> loader) {
		super(folder, controller, loader);
	}

	@Override
	protected AvgVarStacks<PlaneImg> computeAvgAndVar() {
		Loader<StackImg> loader = getLoader();

		PlaneImg[] avgs = new PlaneImg[loader.getNumberOfChannels()];
		PlaneImg[] vars = new PlaneImg[loader.getNumberOfChannels()];
		
		ArrayImgFactory<FloatType> factory = new ArrayImgFactory<FloatType>(new FloatType());
		
		for(int q=0; q<loader.getNumberOfChannels(); q++) {
			loader.openChannel(q);

			int imgcount = 0;
			int filecount = 0;
			while(loader.hasNext(q)) {
				StackImg newImg = loader.getNext(q);
				if(imgcount == 0) {
					avgs[q] = new PlaneImg(factory.create(new int[] {newImg.getWidth(), newImg.getHeight()}), newImg.getExposure());
					vars[q] = new PlaneImg(factory.create(new int[] {newImg.getWidth(), newImg.getHeight()}), newImg.getExposure());
				}
				
				boolean t_axis = false;
				if(newImg.getImage().dimension(2) > 1) {
					t_axis = true;
				}
				
				imgcount += newImg.getImage().dimension(2);
				
				// computes cumulative sum and square sum
				int pixcount = 0;
				RandomAccess<RealType<?>> r = newImg.getImage().randomAccess();
				Cursor<FloatType> cursor = avgs[q].getImage().localizingCursor();
				RandomAccess<FloatType> r_var = vars[q].getImage().randomAccess();
				while(cursor.hasNext()) {
					FloatType t_avg = cursor.next();
					
					r.setPosition(cursor.getIntPosition(0),0);
					r.setPosition(cursor.getIntPosition(1),1);
					r_var.setPosition(cursor);
					FloatType t_var = r_var.get();

					float f=t_avg.get(), g=t_var.get();
					for(int z=0;z<newImg.getImage().dimension(2);z++) {
						// moves only if has 3rd dimension 
						if(t_axis) {
							r.setPosition(z, 2);
						}
						
						f += r.get().getRealFloat();
						g += r.get().getRealFloat()*r.get().getRealFloat();
					}
					t_avg.set(f);
					t_var.set(g);
					
					// shows progress for single stack
					if(newImg.getImage().dimension(2) > 1 && loader.getChannelLength() == imgcount 
							&& ((pixcount+1)%100 == 0  || (pixcount+1)==newImg.getHeight()*newImg.getWidth())) { // single stack
						
						double large_step = 80.*((double) q)/((double) loader.getNumberOfChannels()); // % of files
						double small_step = 80.*((double) pixcount)/((double) newImg.getHeight()*newImg.getWidth())/((double) loader.getNumberOfChannels()); // % pixels 
						
						int progress = (int) (large_step + small_step);
						
						showProgressOnEDT(CalibrationProcessor.PROGRESS, "Stack "+(q+1)+"/"+loader.getNumberOfChannels()+", "+
								"pixels "+(pixcount+1)+"/"+(newImg.getHeight()*newImg.getWidth()), progress);
						
					} else if(newImg.getImage().dimension(2) > 1 
							&& ((pixcount+1)%100 == 0 || (pixcount+1)==newImg.getHeight()*newImg.getWidth())) { // multistacks
						
						double large_step = 80.*((double) q)/((double) loader.getNumberOfChannels()); // % of files
						double intermediate_step = 80.*((double) filecount)/((double) loader.getChannelLength())/((double) loader.getNumberOfChannels()); // % of images
						double small_step = 80.*((double) pixcount)/((double) newImg.getHeight()*newImg.getWidth())/((double) loader.getChannelLength())/((double) loader.getNumberOfChannels()); // % pixels
						
						int progress = (int) (large_step+intermediate_step+small_step);
						
						showProgressOnEDT(CalibrationProcessor.PROGRESS, "Stack "+(q+1)+"/"+loader.getNumberOfChannels()+", "+
								"image "+(filecount+1)+"/"+loader.getChannelLength()
								+", pixels "+(pixcount+1)+"/"+(newImg.getHeight()*newImg.getWidth()), progress);
						
					}	

					pixcount++;
				}
				
				// shows progress for single images
				if(newImg.getImage().dimension(2) == 1 
						&& ((imgcount+1)%100 == 0 || (imgcount+1)==loader.getChannelLength())) { // single images
					double large_step = 80.*((double) q)/((double) loader.getNumberOfChannels()); // % of files
					double small_step = 80.*((double) imgcount)/((double) loader.getChannelLength())/((double) loader.getNumberOfChannels());  // % frames 
					int progress = (int) (large_step + small_step);
					showProgressOnEDT(CalibrationProcessor.PROGRESS, "Stack "+(q+1)+"/"+loader.getNumberOfChannels()+", "+
							"frame "+(imgcount+1)+"/"+loader.getChannelLength(), progress);
				}	

				filecount++;
			}
			
			// computes mean and variance
			Cursor<FloatType> cursor = avgs[q].getImage().localizingCursor();
			RandomAccess<FloatType> r_var = vars[q].getImage().randomAccess();
			while(cursor.hasNext()) {
				FloatType t_avg = cursor.next();
				r_var.setPosition(cursor);
				FloatType t_var = r_var.get();
				
				float f = t_avg.get(), g = t_var.get();
			
				// divides by image count
				f /= imgcount;
				g = g/imgcount - f*f;
				
				// sets pixels
				t_avg.set(f);
				t_var.set(g);
			}
		}
		return  new AvgVarStacks<PlaneImg>(avgs, vars);
	}

}
