package main.java.embl.rieslab.photonfreecamcalib.data;

import ij.process.FloatProcessor;

public class FloatImage {

	private final FloatProcessor proc;
	private final int exposure;
	
	public FloatImage(int width, int height, float[][] pixels, int exposure) {
		this.exposure = exposure;
		this.proc = new FloatProcessor(width, height);
		this.proc.setFloatArray(pixels);
	}
	
	public int getExposure() {
		return exposure;
	}
	
	public int getWidth() {
		return proc.getWidth();
	}
	
	public int getHeight() {
		return proc.getHeight();
	}
	
	public FloatProcessor getImage() {
		return proc;
	}
	
	public float[] getPixels() {
		return (float[]) proc.getPixels();
	}

	public void addPixels(float[] pixs) {
		if(pixs == null) {
			throw new NullPointerException();
		}
		
		if(pixs.length != proc.getWidth()*proc.getHeight()) {
			throw new IllegalArgumentException();
		}
		
		for(int x=0;x<proc.getWidth();x++) {
			for(int y=0;y<proc.getHeight();y++) {
				proc.setf(x, y, proc.getf(x,y)+pixs[x+proc.getWidth()*y]);
			}
		}
	}
	
	public void dividePixels(float d) {
		if(Math.abs(d) > 0.01) {
			for(int x=0;x<proc.getWidth();x++) {
				for(int y=0;y<proc.getHeight();y++) {
					proc.setf(x, y, proc.getf(x,y)/d);
				}
			}
		}
	}
	
	public void addSquarePixels(float[] pixs) {
		if(pixs == null) {
			throw new NullPointerException();
		}
		
		if(pixs.length != proc.getWidth()*proc.getHeight()) {
			throw new IllegalArgumentException();
		}
		
		for(int x=0;x<proc.getWidth();x++) {
			for(int y=0;y<proc.getHeight();y++) {
				proc.setf(x, y, proc.getf(x,y)+pixs[x+proc.getWidth()*y]*pixs[x+proc.getWidth()*y]);
			}
		}
	}
	
	public void square() {
		for(int x=0;x<proc.getWidth();x++) {
			for(int y=0;y<proc.getHeight();y++) {
				proc.setf(x, y, proc.getf(x,y)*proc.getf(x,y));
			}
		}
	}
	
	public void toVariance(float[] meanPixs, float size) {
		if(meanPixs == null) {
			throw new NullPointerException();
		}
		
		if(meanPixs.length != proc.getWidth()*proc.getHeight()) {
			throw new IllegalArgumentException();
		}
		
		for(int x=0;x<proc.getWidth();x++) {
			for(int y=0;y<proc.getHeight();y++) {
				proc.setf(x, y, proc.getf(x,y)/size-meanPixs[x+proc.getWidth()*y]*meanPixs[x+proc.getWidth()*y]);
			}
		}
	}

	public FloatImage copy() {
		return new FloatImage(getWidth(), getHeight(), proc.getFloatArray(), exposure);
	}
	
	public FloatProcessor getProcessor() {
		return proc;
	}
	
}
