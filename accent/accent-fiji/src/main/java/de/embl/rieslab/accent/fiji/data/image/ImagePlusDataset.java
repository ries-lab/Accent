package de.embl.rieslab.accent.fiji.data.image;

import ij.ImagePlus;

public class ImagePlusDataset {

	private ImagePlus im;
	private int exposure;
	
	public ImagePlusDataset(ImagePlus im, int exposure) {
		if(im == null) {
			throw new NullPointerException();
		}
		
		this.im = im;
		this.exposure = exposure;
	}
	
	public ImagePlus getImage() {
		return im;
	}
	
	public int getExposure() {
		return exposure;
	}

}
