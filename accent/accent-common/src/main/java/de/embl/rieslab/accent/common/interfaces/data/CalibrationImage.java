package de.embl.rieslab.accent.common.interfaces.data;


public interface CalibrationImage {

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
	 * Returns the value of the pixel at (x,y). If the image has a 3rd dimension, then it is always returned at frame 0.
	 * 
	 * @param x Dimension 0
	 * @param y Dimension 1
	 * @return Pixel value
	 */
	public float getPixelValue(int x, int y); 

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
	
}
