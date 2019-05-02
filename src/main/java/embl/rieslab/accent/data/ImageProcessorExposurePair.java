package main.java.embl.rieslab.accent.data;

import ij.process.ImageProcessor;

public class ImageProcessorExposurePair {

	private ImageProcessor im;
	private int exposure;
	
	public ImageProcessorExposurePair(ImageProcessor im, int exposure) {
		this.im = im;
		this.exposure = exposure;
	}
	
	public ImageProcessor getImage() {
		return im;
	}
	
	public int getExposure() {
		return exposure;
	}
}
