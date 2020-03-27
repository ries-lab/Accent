package de.embl.rieslab.accent.common.data.image;

/**
 * Image representation with a type (byte, short or float), pixels object, width, height
 * and exposure (ms) at which it was recorded.
 * 
 * @author Joran Deschamps
 *
 */
public class BareImage {
	
	private DataType type;
	private Object pixels;
	private int width;
	private int height;
	private double exposure;

	/**
	 * Constructor.
	 * @param type DataType (BYTE, SHORT or FLOAT)
	 * @param pixels Linear array of pixels of the corresponding type.
	 * @param width Width of the image
	 * @param height Height of the image
	 * @param exposure Exposure in ms at which the image was recorded
	 */
	// should reconsider using the datatype object, not really useful here. But used in FloatImage to avoid recasting
	public BareImage(DataType type, Object pixels, int width, int height, double exposure) {
		if(pixels == null || type == null) {
			throw new NullPointerException();
		}
		
		if(type == DataType.BYTE) {
			if(!(pixels instanceof byte[])) {
				throw new IllegalArgumentException("pixels is not a byte array.");
			}
			
			if(width*height != ((byte[]) pixels).length) {
				throw new IllegalArgumentException("The array has the wrong size.");
			}
		} else if(type == DataType.SHORT) {
			if(!(pixels instanceof short[])) {
				throw new IllegalArgumentException("pixels is not a short array.");
			}
			if(width*height != ((short[]) pixels).length) {
				throw new IllegalArgumentException("The array has the wrong size.");
			}
		} else {
			if(!(pixels instanceof float[])) {
				throw new IllegalArgumentException("pixels is not a float array.");
			}
			if(width*height != ((float[]) pixels).length) {
				throw new IllegalArgumentException("The array has the wrong size.");
			}
		}
		
		this.type = type;
		this.pixels = pixels;
		this.width = width;
		this.height = height;
		this.exposure = exposure;
	}
	/**
	 * Constructor.
	 * @param type Bytes per pixels (1, 2 or >=3)
	 * @param pixels Linear array of pixels of the corresponding type.
	 * @param width Width of the image
	 * @param height Height of the image
	 * @param exposure Exposure in ms at which the image was recorded
	 */
	public BareImage(int bytesPerPixel, Object pixels, int width, int height, double exposure) {
		if(pixels == null) {
			throw new NullPointerException();
		}
		
		if(bytesPerPixel == 1) {
			if(!(pixels instanceof byte[])) {
				throw new IllegalArgumentException("pixels is not a byte array.");
			}
			if(width*height != ((byte[]) pixels).length) {
				throw new IllegalArgumentException();
			} else {
				type = DataType.BYTE;
			}
		} else if(bytesPerPixel == 2) {
			if(!(pixels instanceof short[])) {
				throw new IllegalArgumentException("pixels is not a short array.");
			}
			if(width*height != ((short[]) pixels).length) {
				throw new IllegalArgumentException();
			} else {
				type = DataType.SHORT;
			}
		} else {
			if(!(pixels instanceof float[])) {
				throw new IllegalArgumentException("pixels is not a float array.");
			}
			if(width*height != ((float[]) pixels).length) {
				throw new IllegalArgumentException();
			} else {
				type = DataType.FLOAT;
			}
		}
		
		this.pixels = pixels;
		this.width = width;
		this.height = height;
		this.exposure = exposure;
	}
	
	/**
	 * Returns the data type.
	 * @return DataType.BYTE, DataType.SHORT or DataType.FLOAT
	 */
	public DataType getDataType() {
		return type;
	}
	
	/**
	 * Pixel array
	 * @return Linear pixel array as Object
	 */
	public Object getPixels() {
		return pixels;
	}
	
	/**
	 * Image width
	 * @return
	 */
	public int getWidth() {
		return width;
	}
	/**
	 * Image height
	 * @return
	 */
	public int getHeight() {
		return height;
	}
	/**
	 * Exposure in ms at which the image was recorded
	 * @return Exposure in ms
	 */
	public double getExposure() {
		return exposure;
	}
	/**
	 * Class used to characterize the pixel array type of a BareImage
	 * 
	 * @author Joran Deschamps
	 *
	 */
	public enum DataType{
		BYTE, SHORT, FLOAT;
	}
}
