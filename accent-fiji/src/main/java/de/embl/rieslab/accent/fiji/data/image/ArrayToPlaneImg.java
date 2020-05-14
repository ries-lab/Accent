package de.embl.rieslab.accent.fiji.data.image;

import de.embl.rieslab.accent.common.interfaces.data.ArrayToImage;
import net.imglib2.Cursor;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.numeric.real.FloatType;

public class ArrayToPlaneImg implements ArrayToImage<PlaneImg>{
/*
	@Override
	public PlaneImg getImage(double[] pixels, int width, int height, double exposure) {
		ArrayImgFactory<FloatType> factory = new ArrayImgFactory<FloatType>(new FloatType());
		Img<FloatType> img = factory.create(new int[] {width, height});
		
		Cursor<FloatType> cursor = img.localizingCursor();
		while(cursor.hasNext()) {
			FloatType t = cursor.next();
			
			int p = cursor.getIntPosition(1)*width+cursor.getIntPosition(0);
			t.set((float) pixels[p]);			
		}
		return new PlaneImg(img, exposure);
	}
*/
	@Override
	public PlaneImg getImage(float[] pixels, int width, int height, double exposure) {
		ArrayImgFactory<FloatType> factory = new ArrayImgFactory<FloatType>(new FloatType());
		Img<FloatType> img = factory.create(new int[] {width, height});
		
		Cursor<FloatType> cursor = img.localizingCursor();
		while(cursor.hasNext()) {
			FloatType t = cursor.next();
			
			int p = cursor.getIntPosition(1)*width+cursor.getIntPosition(0);
			t.set(pixels[p]);			
		}
		return new PlaneImg(img, exposure);
	}

}
