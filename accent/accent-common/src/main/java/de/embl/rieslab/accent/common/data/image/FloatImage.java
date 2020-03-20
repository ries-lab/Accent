package de.embl.rieslab.accent.common.data.image;

import de.embl.rieslab.accent.common.data.image.BareImage.DataType;
import ij.ImagePlus;
import ij.io.FileSaver;
import ij.process.FloatProcessor;

public class FloatImage {

	private final double exposure;
	private FloatProcessor img;

	public FloatImage(BareImage image) {
		this.exposure = image.getExposure();

		img = new FloatProcessor(image.getWidth(), image.getHeight());

		if(image.getDataType() == DataType.BYTE) {
			setPixels(image.getWidth(), image.getHeight(), (byte[]) image.getPixels());
		} else if(image.getDataType() == DataType.SHORT) {
			setPixels(image.getWidth(), image.getHeight(), (short[]) image.getPixels());
		} else {
			setPixels(image.getWidth(), image.getHeight(), (float[]) image.getPixels());
		}
	}

	public FloatImage(int width, int height, byte[] pixels, double exposure) {
		this.exposure = exposure;

		img = new FloatProcessor(width, height);

		setPixels(width, height, pixels);
	}

	public FloatImage(int width, int height, short[] pixels, double exposure) {
		this.exposure = exposure;

		img = new FloatProcessor(width, height);

		setPixels(width, height, pixels);
	}
	
	public FloatImage(int width, int height, double[] pixels, double exposure) {
		this.exposure = exposure;

		img = new FloatProcessor(width, height);

		setPixels(width, height, pixels);
	}	
	
	public FloatImage(int width, int height, float[] pixels, double exposure) {
		this.exposure = exposure;

		img = new FloatProcessor(width, height);

		setPixels(width, height, pixels);
	}

	public FloatImage(FloatImage image) {
		this.exposure = image.getExposure();
		
		img = new FloatProcessor(image.getImage().getFloatArray());
	}

	private void setPixels(int width, int height, byte[] pixels) {
		if(img != null) {
			for(int x=0;x<width;x++) {
				for(int y=0;y<height;y++) {
					img.setf(x, y, Byte.toUnsignedInt(pixels[x+width*y]));
				}
			}
		}
	}

	private void setPixels(int width, int height, double[] pixels) {
		if(img != null) {
			for(int x=0;x<width;x++) {
				for(int y=0;y<height;y++) {
					img.setf(x, y, (float) pixels[x+width*y]);
				}
			}
		}
	}

	private void setPixels(int width, int height, float[] pixels) {
		if(img != null) {
			for(int x=0;x<width;x++) {
				for(int y=0;y<height;y++) {
					img.setf(x, y, pixels[x+width*y]);
				}
			}
		}
	}
	
	private void setPixels(int width, int height, short[] pixels) {
		if(img != null) {
			for(int x=0;x<width;x++) {
				for(int y=0;y<height;y++) {
					img.setf(x, y, (float) Short.toUnsignedInt(pixels[x+width*y]));
				}
			}
		}
	}
	
	public double getExposure() {
		return exposure;
	}
	
	public int getWidth() {
		return img.getWidth();
	}
	
	public int getHeight() {
		return  img.getHeight();
	}
	
	public FloatProcessor getImage() {
		return img;
	}

	public void addPixels(BareImage image) {
		if(image.getDataType() == DataType.BYTE) {
			addPixels((byte[]) image.getPixels());
		} else if(image.getDataType() == DataType.SHORT) {
			addPixels((short[]) image.getPixels());
		} else {
			addPixels((float[]) image.getPixels());
		}
	}
	
	
	private void addPixels(byte[] pixels) {
		if(pixels == null) {
			throw new NullPointerException();
		}
		
		if(pixels.length != getWidth()*getHeight()) {
			throw new IllegalArgumentException();
		}
		
		for(int x=0;x<getWidth();x++) {
			for(int y=0;y<getHeight();y++) {
				img.setf(x, y, img.getf(x,y)+Byte.toUnsignedInt(pixels[x+getWidth()*y]));
			}
		}
	}

	private void addPixels(float[] pixels) {
		if(pixels == null) {
			throw new NullPointerException();
		}
		
		// assumes here a square 2D array
		if(pixels.length != getWidth()*getHeight()) {
			throw new IllegalArgumentException();
		}
		
		for(int x=0;x<getWidth();x++) {
			for(int y=0;y<getHeight();y++) {
				img.setf(x, y, img.getf(x,y)+pixels[x+getWidth()*y]);
			}
		}
	}

	/*
	public void addPixels(float[][] pixels) {
		if(pixels == null) {
			throw new NullPointerException();
		}
		
		// assumes here a square 2D array
		if(pixels.length*pixels[0].length != getWidth()*getHeight()) {
			throw new IllegalArgumentException();
		}
		
		for(int x=0;x<getWidth();x++) {
			for(int y=0;y<getHeight();y++) {
				img.setf(x, y, img.getf(x,y)+pixels[x][y]);
			}
		}
	}*/
	
	private void addPixels(short[] pixels) {
		if(pixels == null) {
			throw new NullPointerException();
		}
		
		if(pixels.length != getWidth()*getHeight()) {
			throw new IllegalArgumentException();
		}
		
		for(int x=0;x<getWidth();x++) {
			for(int y=0;y<getHeight();y++) {
				img.setf(x, y, img.getf(x,y)+ (float) Short.toUnsignedInt(pixels[x+getWidth()*y]));
			}
		}
	}
	
	public void dividePixels(float d) {
		if(Math.abs(d) > 0.01) {
			for(int x=0;x<getWidth();x++) {
				for(int y=0;y<getHeight();y++) {
					img.setf(x, y, img.getf(x,y)/d);
				}
			}
		}
	}
	
	public void addSquarePixels(BareImage image) {
		if(image.getDataType() == DataType.BYTE) {
			addSquarePixels((byte[]) image.getPixels());
		} else if(image.getDataType() == DataType.SHORT) {
			addSquarePixels((short[]) image.getPixels());
		} else {
			addSquarePixels((float[]) image.getPixels());
		}
	}
	
	private void addSquarePixels(byte[] pixels) {
		if(pixels == null) {
			throw new NullPointerException();
		}
		
		if(pixels.length != getWidth()*getHeight()) {
			throw new IllegalArgumentException();
		}
		
		for(int x=0;x<getWidth();x++) {
			for(int y=0;y<getHeight();y++) {
				float pix = (float) Byte.toUnsignedInt(pixels[x+getWidth()*y]);
				img.setf(x, y, img.getf(x,y)+pix*pix);
			}
		}
	}
	
	private void addSquarePixels(short[] pixels) {
		if(pixels == null) {
			throw new NullPointerException();
		}
		
		if(pixels.length != getWidth()*getHeight()) {
			throw new IllegalArgumentException();
		}

		for(int x=0;x<getWidth();x++) {
			for(int y=0;y<getHeight();y++) {
				float pix = (float) Short.toUnsignedInt(pixels[x+getWidth()*y]);
				img.setf(x, y, img.getf(x,y)+pix*pix);
			}
		}
	}
	
	private void addSquarePixels(float[] pixels) {
		if(pixels == null) {
			throw new NullPointerException();
		}
		
		if(pixels.length != getWidth()*getHeight()) {
			throw new IllegalArgumentException();
		}
		
		for(int x=0;x<getWidth();x++) {
			for(int y=0;y<getHeight();y++) {
				img.setf(x, y, img.getf(x,y)+pixels[x+getWidth()*y]*pixels[x+getWidth()*y]);
			}
		}
	}
	
	/*
	public void addSquarePixels(float[][] pixels) {
		if(pixels == null) {
			throw new NullPointerException();
		}
		
		if(pixels.length*pixels[0].length != getWidth()*getHeight()) {
			throw new IllegalArgumentException();
		}
		
		for(int x=0;x<getWidth();x++) {
			for(int y=0;y<getHeight();y++) {
				img.setf(x, y, img.getf(x,y)+pixels[x][y]*pixels[x][y]);
			}
		}
	}*/
	
	public void square() {
		for(int x=0;x<getWidth();x++) {
			for(int y=0;y<getHeight();y++) {
				img.setf(x, y, img.getf(x,y)*img.getf(x,y));
			}
		}
	}
	
	public void toVariance(FloatProcessor imp, float size) {
		if(imp == null) {
			throw new NullPointerException();
		}
		
		if(imp.getWidth() != getWidth() || imp.getHeight() != getHeight()) {
			throw new IllegalArgumentException();
		}
		
		for(int x=0;x<getWidth();x++) {
			for(int y=0;y<getHeight();y++) {
				img.setf(x, y, img.getf(x,y)/size-imp.getf(x, y)*imp.getf(x, y));
			}
		}
	}
	
	public void saveAsTiff(String path) {
		FileSaver fs = new FileSaver(new ImagePlus("", img)); 
		fs.saveAsTiff(path);
	}
	
	public float getPixelValue(int x, int y) { 
		return img.getf(x, y);
	}
}
