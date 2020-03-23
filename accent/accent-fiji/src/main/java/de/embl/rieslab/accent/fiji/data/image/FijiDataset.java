package de.embl.rieslab.accent.fiji.data.image;

import net.imagej.Dataset;

public class FijiDataset {

	private Dataset im;
	private double exposure;
	
	public FijiDataset(Dataset im, double exposure) {
		if(im == null) {
			throw new NullPointerException();
		}
		
		this.im = im;
		this.exposure = exposure;
	}
	
	public Dataset getImage() {
		return im;
	}
	
	public double getExposure() {
		return exposure;
	}
	
	public String getType() {
		return im.getTypeLabelShort();
	}
}
