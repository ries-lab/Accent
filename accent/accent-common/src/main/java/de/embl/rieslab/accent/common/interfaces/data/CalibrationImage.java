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
}
