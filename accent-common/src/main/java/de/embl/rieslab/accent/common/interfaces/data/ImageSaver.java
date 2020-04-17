package de.embl.rieslab.accent.common.interfaces.data;

public interface ImageSaver<T extends CalibrationImage> {

	/**
	 * Saves image as tiff. 
	 * 
	 * @param image Image to save.
	 * @param filePath Filepath.
	 */
	public boolean saveAsTiff(T image, String filePath);
}
