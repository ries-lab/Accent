package de.embl.rieslab.accent.common.dummys;

import de.embl.rieslab.accent.common.interfaces.data.CalibrationImage;
import de.embl.rieslab.accent.common.interfaces.data.RawImage;

public class DummyImage implements CalibrationImage, RawImage{

	private float[] pixels;
	private int width, height;
	private double exposure;

	public DummyImage(int width, int height, double exposure) {
		pixels = new float[width*height];
		this.width = width;
		this.height = height;
		this.exposure = exposure;
		
		for(int i=0;i<width*height;i++) {
			pixels[i] = i;
		}
	}
	
	public DummyImage(int bytesPerPixels, Object pixels, int width, int height, double exposure) {
		this.pixels = new float[width*height];
		for(int i=0;i<width*height;i++) {
			if(bytesPerPixels == 1) {
				this.pixels[i] = Byte.toUnsignedInt(((byte[]) pixels)[i]);
			} else if(bytesPerPixels == 2) {
				this.pixels[i] = Short.toUnsignedInt(((short[]) pixels)[i]);
			} else if(bytesPerPixels > 2 && bytesPerPixels <= 4) {
				this.pixels[i] = ((float[]) pixels)[i];
			} else { // a bit stupid
				this.pixels[i] = (float) ((double[]) pixels)[i];
			}
		}

		this.width = width;
		this.height = height;
		this.exposure = exposure;
	}
	
	@Override
	public Object getImage() {
		return pixels;
	}

	@Override
	public double getExposure() {
		return exposure;
	}

	@Override
	public float getPixelValue(int x, int y) {
		return pixels[y*width+x];
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public int getBytesPerPixel() {
		return 3;
	}

}
