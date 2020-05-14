package de.embl.rieslab.accent.common.interfaces.data;

public interface ArrayToImage<T extends CalibrationImage> {

	public T getImage(float[] pixels, int width, int height, double exposure);
	
}
