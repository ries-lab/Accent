package de.embl.rieslab.accent.fiji.data.image;

import de.embl.rieslab.accent.common.interfaces.data.CalibrationImage;
import net.imagej.Dataset;
import net.imglib2.RandomAccess;
import net.imglib2.type.numeric.RealType;

public class PlaneImg  implements CalibrationImage{

	private Dataset img_;
	private double exposure_;
	
	public PlaneImg(Dataset img, double exposure) {
		if(img == null)
			throw new NullPointerException("Image cannot be null.");
			
		// should check that that the dimensions correspond to x and y actually...
		if(img.numDimensions() != 2)
			throw new IllegalArgumentException("PlaneImg must be of dimension 2.");
			
		img_ = img;
		exposure_ = exposure;
	}
	
	@Override
	public Dataset getImage() {
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
		
		RandomAccess<? extends RealType<?>> r = img_.getImgPlus().getImg().randomAccess();
		r.setPosition(new int[] {x,y});
		
		return r.get().getRealFloat();
	}

	@Override
	public int getWidth() {
		return (int) img_.getWidth();
	}

	@Override
	public int getHeight() {
		return (int) img_.getHeight();
	}

}
