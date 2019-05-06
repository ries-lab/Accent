package main.java.embl.rieslab.accent.data.images;

import org.micromanager.data.Image;

public class ImageExposurePair {

	private Image im;
	private int exposure;
	
	public ImageExposurePair(Image im, int exposure) {
		if(im == null) {
			throw new NullPointerException();
		}
		
		this.im = im;
		this.exposure = exposure;
	}
	
	public Image getImage() {
		return im;
	}
	
	public int getExposure() {
		return exposure;
	}
}
