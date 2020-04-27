package de.embl.rieslab.accent.fiji.data.image;

import de.embl.rieslab.accent.common.interfaces.data.RawImage;
import net.imagej.Dataset;

public class StackImg implements RawImage{
	
	private Dataset img_;
	private double exposure_;
	private int thirdDimIndex_;
	
	public StackImg(Dataset img, double exposure) {
		if(img == null)
			throw new NullPointerException("Image cannot be null.");
			
		img_ = img;
		// identify third dimension
		// we assume only 3 dimensions with depth > 1 and we look for the third dimension value
		int j = 2;
		if(img.numDimensions() > 2) { // identify the dimension with the largest depth, ignoring X,Y
			long max = 1;
			for(int i=2;i<img.numDimensions();i++) {
				if(img.dimension(i) > max) {
					max = img.dimension(i);
					j = i;
				}
			}
		}
		thirdDimIndex_ = j;
		
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

	public int getThirdDimensionIndex() {
		return thirdDimIndex_;
	}
}
