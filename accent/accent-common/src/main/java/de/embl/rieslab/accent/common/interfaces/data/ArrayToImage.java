package de.embl.rieslab.accent.common.interfaces.data;

public interface ArrayToImage<T extends CalibrationImage> {

	public T getImage(int bytesPerPixels, Object pixels, int width, int height, double exposure);
	
}
