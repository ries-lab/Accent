package main.java.embl.rieslab.accent.data;

import org.micromanager.data.Image;

import ij.ImagePlus;
import ij.io.FileSaver;
import ij.process.FloatProcessor;

public class FloatImage {

	private final int exposure;
	private FloatProcessor img;
	
	public FloatImage(ImageExposurePair impair) {
		this.exposure = impair.getExposure();

		Image im = impair.getImage();
		img = new FloatProcessor(im.getWidth(), im.getHeight());
		
		if(im.getBytesPerPixel() == 1) {
			setPixels(im.getWidth(),im.getHeight(), (byte[]) im.getRawPixels());
		} else {
			setPixels(im.getWidth(),im.getHeight(), (short[]) im.getRawPixels());
		}
	}	
	
	public FloatImage(Image im, int exposure) {
		this.exposure = exposure;

		img = new FloatProcessor(im.getWidth(), im.getHeight());
		
		if(im.getBytesPerPixel() == 1) {
			setPixels(im.getWidth(),im.getHeight(), (byte[]) im.getRawPixels());
		} else {
			setPixels(im.getWidth(),im.getHeight(), (short[]) im.getRawPixels());
		}
	}

	public FloatImage(int width, int height, byte[] pixels, int exposure) {
		this.exposure = exposure;

		img = new FloatProcessor(width, height);

		setPixels(width, height, pixels);
	}

	public FloatImage(int width, int height, short[] pixels, int exposure) {
		this.exposure = exposure;

		img = new FloatProcessor(width, height);

		setPixels(width, height, pixels);
	}
	
	public FloatImage(int width, int height, double[] pixels, int exposure) {
		this.exposure = exposure;

		img = new FloatProcessor(width, height);

		setPixels(width, height, pixels);
	}	
	
	public FloatImage(int width, int height, float[] pixels, int exposure) {
		this.exposure = exposure;

		img = new FloatProcessor(width, height);

		setPixels(width, height, pixels);
	}

	public FloatImage(FloatImage image) {
		this.exposure = image.getExposure();
		
		img = new FloatProcessor(image.getImage().getFloatArray());
	}

	public FloatImage(ImageProcessorExposurePair image) {
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
					img.setf(x, y, Short.toUnsignedInt(pixels[x+width*y]));
				}
			}
		}
	}
	
	public int getExposure() {
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

	public void addPixels(Image image) {
		if(image == null) {
			throw new NullPointerException();
		}

		if(image.getWidth() != getWidth() || image.getHeight() != getHeight()) {
			throw new IllegalArgumentException();
		}

		if(image.getBytesPerPixel() == 1) {
			addPixels((byte[]) image.getRawPixels());
		} else {
			addPixels((short[]) image.getRawPixels());
		}
	}

	public void addPixels(byte[] pixels) {
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

	public void addPixels(float[][] pixels) {
		if(pixels == null) {
			throw new NullPointerException();
		}
		
		if(pixels.length != getWidth()*getHeight()) {
			throw new IllegalArgumentException();
		}
		
		for(int x=0;x<getWidth();x++) {
			for(int y=0;y<getHeight();y++) {
				img.setf(x, y, img.getf(x,y)+pixels[x][y]);
			}
		}
	}
	
	private void addPixels(short[] pixels) {
		if(pixels == null) {
			throw new NullPointerException();
		}
		
		if(pixels.length != getWidth()*getHeight()) {
			throw new IllegalArgumentException();
		}
		
		for(int x=0;x<getWidth();x++) {
			for(int y=0;y<getHeight();y++) {
				img.setf(x, y, img.getf(x,y)+Short.toUnsignedInt(pixels[x+getWidth()*y]));
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
	
	public void addSquarePixels(Image image) {
		if(image == null) {
			throw new NullPointerException();
		}

		if(image.getWidth() != getWidth() || image.getHeight() != getHeight()) {
			throw new IllegalArgumentException();
		}

		if(image.getBytesPerPixel() == 1) {
			addSquarePixels((byte[]) image.getRawPixels());
		} else {
			addSquarePixels((short[]) image.getRawPixels());
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
				int pix = Byte.toUnsignedInt(pixels[x+getWidth()*y]);
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
				int pix = Short.toUnsignedInt(pixels[x+getWidth()*y]);
				img.setf(x, y, img.getf(x,y)+pix*pix);
			}
		}
	}
	
	public void addSquarePixels(float[][] pixels) {
		if(pixels == null) {
			throw new NullPointerException();
		}
		
		if(pixels.length != getWidth()*getHeight()) {
			throw new IllegalArgumentException();
		}
		
		for(int x=0;x<getWidth();x++) {
			for(int y=0;y<getHeight();y++) {
				img.setf(x, y, img.getf(x,y)+pixels[x][y]*pixels[x][y]);
			}
		}
	}
	
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
