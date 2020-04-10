package de.embl.rieslab.accent.common.interfaces.data;

public interface RawImage {
	
	/**
	 * Returns the pixel representation.
	 * @return pixel representation
	 */
	public Object getImage();
	
	/**
	 * Returns the exposure at which the image was taken.
	 * @return Exposure in ms
	 */
	public double getExposure();
	
	/**
	 * Returns width (dimension 0).
	 * @return Width
	 */
	public int getWidth();
	
	/**
	 * Returns height (dimension 1).
	 * @return height
	 */
	public int getHeight();
	
	/**
	 * Returns the number of bytes per pixel.
	 * @return Number of bytes per pixel.
	 */
	public int getBytesPerPixel();
}
