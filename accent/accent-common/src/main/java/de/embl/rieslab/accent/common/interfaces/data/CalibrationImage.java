package de.embl.rieslab.accent.common.interfaces.data;


public interface CalibrationImage {

	/**
	 * Returns the image.
	 * @return Image
	 */
	public Object getImage();
	
	/**
	 * Returns the exposure at which the image was taken.
	 * @return Exposure in ms
	 */
	public double getExposure();
	
	/**
	 * Returns the value of the pixel at (x,y).
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
	
	/**
	 * Saves image as tiff under the specific fileName (must contain extension).
	 * @param path File name with extension.
	 * @return True if successful, false otherwise.
	 */
	public boolean saveAsTiff(String fileName);
}
