package de.embl.rieslab.accent.fiji.data.image;

import ij.ImagePlus;

public class ImagePlusDataset {

	private ImagePlus im;
	private double exposure;
	
	public ImagePlusDataset(ImagePlus im, double exposure) {
		if(im == null) {
			throw new NullPointerException();
		}
		
		this.im = im;
		this.exposure = exposure;
	}
	
	public ImagePlus getImage() {
		return im;
	}
	
	public double getExposure() {
		return exposure;
	}

}
