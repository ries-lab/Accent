package de.embl.rieslab.accent.common.data.image;

import de.embl.rieslab.accent.common.data.image.BareImage.DataType;
import de.embl.rieslab.accent.common.interfaces.data.CalibrationImage;
import ij.ImagePlus;
import ij.io.FileSaver;
import ij.process.FloatProcessor;

/**
 * Class holding a FloatProcessor and the exposure in ms at which the image was recorded.
 * 
 * @author Joran Deschamps
 *
 */
public class FloatImage implements CalibrationImage {

	private final double exposure;
	private FloatProcessor img;

	/**
	 * Constructor, builds a FloatProcessor by casting a BareImage pixel array.
	 * @param image BareImage
	 */
	public FloatImage(BareImage image) {
		this.exposure = image.getExposure();

		img = new FloatProcessor(image.getWidth(), image.getHeight());

		if(image.getDataType() == DataType.BYTE) {
			setPixels(image.getWidth(), image.getHeight(), (byte[]) image.getImage());
		} else if(image.getDataType() == DataType.SHORT) {
			setPixels(image.getWidth(), image.getHeight(), (short[]) image.getImage());
		} else {
			setPixels(image.getWidth(), image.getHeight(), (float[]) image.getImage());
		}
	}

	/**
	 * Constructs a FloatProcessor from a byte array.
	 * 
	 * @param width Width of the image
	 * @param height Height of the image
	 * @param pixels Pixel array of length width*height
	 * @param exposure Exposure in ms
	 */
	public FloatImage(int width, int height, byte[] pixels, double exposure) {
		if(width*height != pixels.length) {
			throw new IllegalArgumentException("The pixel array has the wrong size.");
		}
		
		this.exposure = exposure;

		img = new FloatProcessor(width, height);

		setPixels(width, height, pixels);
	}

	/**
	 * Constructs a FloatProcessor from a short array.
	 * 
	 * @param width
	 * @param height
	 * @param pixels Pixel array of length width*height
	 * @param exposure Exposure in ms
	 */
	public FloatImage(int width, int height, short[] pixels, double exposure) {
		if(width*height != pixels.length) {
			throw new IllegalArgumentException("The pixel array has the wrong size.");
		}
		
		this.exposure = exposure;

		img = new FloatProcessor(width, height);

		setPixels(width, height, pixels);
	}

	/**
	 * Constructs a FloatProcessor from a double array.
	 * 
	 * @param width
	 * @param height
	 * @param pixels Pixel array of length width*height
	 * @param exposure Exposure in ms
	 */
	public FloatImage(int width, int height, double[] pixels, double exposure) {
		if(width*height != pixels.length) {
			throw new IllegalArgumentException("The pixel array has the wrong size.");
		}
		
		this.exposure = exposure;

		img = new FloatProcessor(width, height);

		setPixels(width, height, pixels);
	}	

	/**
	 * Constructs a FloatProcessor from a float array.
	 * 
	 * @param width
	 * @param height
	 * @param pixels Pixel array of length width*height
	 * @param exposure Exposure in ms
	 */
	public FloatImage(int width, int height, float[] pixels, double exposure) {			
		if (width * height != pixels.length) {
			throw new IllegalArgumentException("The pixel array has the wrong size.");
		}
		this.exposure = exposure;

		img = new FloatProcessor(width, height);

		setPixels(width, height, pixels);
	}
	/**
	 * Copies a FloatImage pixel array.
	 * 
	 * @param image
	 */
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
	
	/**
	 * Returns the exposure in ms.
	 * @return Exposure in ms.
	 */
	@Override
	public double getExposure() {
		return exposure;
	}
	/**
	 * Returns the image width.
	 * @return
	 */
	public int getWidth() {
		return img.getWidth();
	}
	/**
	 * Returns the image height.
	 * @return
	 */
	public int getHeight() {
		return  img.getHeight();
	}
	/**
	 * Returns the image.
	 * @return
	 */
	@Override
	public FloatProcessor getImage() {
		return img;
	}
	/**
	 * Adds the image pixel values pixel by pixel.
	 * @param image
	 */
	public void addPixels(BareImage image) {
		if(image.getDataType() == DataType.BYTE) {
			addPixels((byte[]) image.getImage());
		} else if(image.getDataType() == DataType.SHORT) {
			addPixels((short[]) image.getImage());
		} else {
			addPixels((float[]) image.getImage());
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
	/**
	 * Divides all pixels by d.
	 * @param d
	 */
	public void dividePixels(float d) {
		if(Math.abs(d) > 0.01) {
			for(int x=0;x<getWidth();x++) {
				for(int y=0;y<getHeight();y++) {
					img.setf(x, y, img.getf(x,y)/d);
				}
			}
		}
	}
	/**
	 * Adds the square of the image pixels, pixel by pixel.
	 * @param image
	 */
	public void addSquarePixels(BareImage image) {
		if(image.getDataType() == DataType.BYTE) {
			addSquarePixels((byte[]) image.getImage());
		} else if(image.getDataType() == DataType.SHORT) {
			addSquarePixels((short[]) image.getImage());
		} else {
			addSquarePixels((float[]) image.getImage());
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
	/**
	 * Squares the image pixel values.
	 */
	public void square() {
		for(int x=0;x<getWidth();x++) {
			for(int y=0;y<getHeight();y++) {
				img.setf(x, y, img.getf(x,y)*img.getf(x,y));
			}
		}
	}
	/**
	 * Divides the image pixel values by size and substracts the square of imp pixel values.
	 * @param imp
	 * @param size
	 */
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
	/**
	 * Saves the image as tiff.
	 * @param path
	 */
	@Override
	public boolean saveAsTiff(String path) {
		FileSaver fs = new FileSaver(new ImagePlus("", img)); 
		return fs.saveAsTiff(path);
	}
	/**
	 * Returns the pixel value at (x,y)
	 * @param x
	 * @param y
	 * @return
	 */
	@Override
	public float getPixelValue(int x, int y) { 
		return img.getf(x, y);
	}
}
