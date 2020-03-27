package de.embl.rieslab.accent.common.interfaces.ui;

/**
 * Interface for a generator UI panel..
 * @author Joran Deschamps
 *
 */
public interface GeneratePanelInterface {
	/**
	 * Shows the path to the calibration file.
	 * @param path
	 */
	public void setCalibrationPath(String path);
	/**
	 * Updates the progress status of the generation.
	 * @param progress String describing the current status
	 */
	public void setProgress(String progress);
	/**
	 * Returns the array of exposure to be used for generating the maps.
	 * @return Array of exposure in ms.
	 */
	public double[] getExposures();
}
