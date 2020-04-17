package de.embl.rieslab.accent.fiji.data.image;

import de.embl.rieslab.accent.common.interfaces.data.RawImage;
import net.imagej.Dataset;

public class StackImg implements RawImage{
	
	private Dataset img_;
	private double exposure_;
	
	public StackImg(Dataset img, double exposure) {
		if(img == null)
			throw new NullPointerException("Image cannot be null.");
			
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
	public int getWidth() {
		return (int) img_.getWidth();
	}

	@Override
	public int getHeight() {
		return (int) img_.getHeight();
	}

	@Override
	public int getBytesPerPixel() {
		// could use dataset.gettypelabel(), which returns for instance "8-bit uint", "16-bit uint" or "32-bit uint"
		return img_.getImgPlus().getImg().firstElement().getBitsPerPixel() / 8;
	}

}
