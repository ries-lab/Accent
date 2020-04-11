package de.embl.rieslab.accent.mm2.data.image;

import de.embl.rieslab.accent.common.interfaces.data.RawImage;

/**
 * Image representation with a type (byte, short or int), pixels object, width, height
 * and exposure (ms) at which it was recorded. Note MM2 seems to only support byte[], 
 * short[] or int[] arrays.
 * 
 * @author Joran Deschamps
 *
 */
public class BareImage implements RawImage {
	
	private DataType type;
	private Object pixels;
	private int width;
	private int height;
	private double exposure;

	/**
	 * Constructor.
	 * @param type Bytes per pixels (1, 2 or higher)
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
				throw new IllegalArgumentException("Pixel array has the wrong size.");
			} else {
				type = DataType.BYTE;
			}
		} else if(bytesPerPixel == 2) {
			if(!(pixels instanceof short[])) {
				throw new IllegalArgumentException("pixels is not a short array.");
			}
			if(width*height != ((short[]) pixels).length) {
				throw new IllegalArgumentException("Pixel array has the wrong size.");
			} else {
				type = DataType.SHORT;
			}
		} else {
			if(!(pixels instanceof int[])) {
				throw new IllegalArgumentException("pixels is not an int array.");
			}
			if(width*height != ((int[]) pixels).length) {
				throw new IllegalArgumentException("Pixel array has the wrong size.");
			} else {
				type = DataType.INT;
			}
		}
		
		this.pixels = pixels;
		this.width = width;
		this.height = height;
		this.exposure = exposure;
	}
	
	/**
	 * Returns the data type.
	 * @return DataType.BYTE, DataType.SHORT or DataType.INT
	 */
	public DataType getDataType() {
		return type;
	}
	
	/**
	 * Returns the linear pixel array. See getWtidth, getHeight and getType to access the correct pixels.
	 * @return Linear pixel array as Object
	 */
	@Override
	public Object getImage() {
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
	@Override
	public double getExposure() {
		return exposure;
	}
	
	@Override
	public int getBytesPerPixel() {
		if(type.equals(DataType.BYTE)){
			return 1;
		} else if(type.equals(DataType.SHORT)){
			return 2;
		} else {
			return 4;
		}
	}
	
	
	/**
	 * Class used to characterize the pixel array type of a BareImage
	 * 
	 * @author Joran Deschamps
	 *
	 */
	public enum DataType{
		BYTE, SHORT, INT;
	}
}
