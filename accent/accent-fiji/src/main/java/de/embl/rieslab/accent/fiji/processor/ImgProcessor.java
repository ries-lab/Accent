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
			while(loader.hasNext(q)) {
				StackImg newImg = loader.getNext(q);
				if(imgcount == 0) {
					avgs[q] = new PlaneImg(factory.create(new int[] {newImg.getWidth(), newImg.getHeight(), 1}), newImg.getExposure());
					vars[q] = new PlaneImg(factory.create(new int[] {newImg.getWidth(), newImg.getHeight(), 1}), newImg.getExposure());
				}
				
				imgcount += newImg.getImage().dimension(2);
				
				// computes cumulative sum and square sum
				RandomAccess<RealType<?>> r = newImg.getImage().randomAccess();
				Cursor<FloatType> cursor = avgs[q].getImage().localizingCursor();
				RandomAccess<FloatType> r_var = vars[q].getImage().randomAccess();
				while(cursor.hasNext()) {
					FloatType t_avg = cursor.next();
					r.setPosition(cursor);
					r_var.setPosition(cursor);
					FloatType t_var = r_var.get();

					float f=t_avg.get(), g=t_var.get();
					for(int z=0;z<newImg.getImage().dimension(2);z++) {
						r.setPosition(z, 2);
						
						f += r.get().getRealFloat();
						g += r.get().getRealFloat()*r.get().getRealFloat();
					}
					t_avg.set(f);
					t_var.set(g);
				}
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
