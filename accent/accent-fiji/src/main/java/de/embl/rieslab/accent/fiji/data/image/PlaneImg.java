package de.embl.rieslab.accent.fiji.data.image;

import de.embl.rieslab.accent.common.interfaces.data.CalibrationImage;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;

public class PlaneImg  implements CalibrationImage{

	private Img<FloatType> img_;
	private double exposure_;
	
	public PlaneImg(Img<FloatType> img, double exposure) {
		if(img == null)
			throw new NullPointerException("Image cannot be null.");
				
		if(img.numDimensions() != 2)
			throw new IllegalArgumentException("Image must be of dimension 2.");
		
		img_ = img;
		exposure_ = exposure;
	}
	
	@Override
	public Img<FloatType> getImage() {
		return img_;
	}

	@Override
	public double getExposure() {
		return exposure_;
	}

	@Override
	public float getPixelValue(int x, int y) {
		if(x < 0 || x >= getWidth() || y < 0 || y >= getHeight())
			throw new IllegalArgumentException("Coordinates outside bounds.");
		
		RandomAccess<? extends RealType<?>> r = img_.randomAccess();
		r.setPosition(new int[] {x,y,1});
		
		return r.get().getRealFloat();
	}

	@Override
	public int getWidth() {
		return (int) img_.dimension(0);
	}

	@Override
	public int getHeight() {
		return (int) img_.dimension(1);
	}

}
