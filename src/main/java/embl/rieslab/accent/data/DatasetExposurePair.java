package main.java.embl.rieslab.accent.data;

import net.imagej.Dataset;

public class DatasetExposurePair {

	private Dataset im;
	private int exposure;
	
	public DatasetExposurePair(Dataset im, int exposure) {
		if(im == null) {
			throw new NullPointerException();
		}
		
		this.im = im;
		this.exposure = exposure;
	}
	
	public Dataset getImage() {
		return im;
	}
	
	public int getExposure() {
		return exposure;
	}
}
