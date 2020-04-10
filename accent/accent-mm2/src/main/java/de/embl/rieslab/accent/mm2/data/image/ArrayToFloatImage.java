package de.embl.rieslab.accent.mm2.data.image;

import de.embl.rieslab.accent.common.interfaces.data.ArrayToImage;

public class ArrayToFloatImage implements ArrayToImage<FloatImage>{

	@Override
	public FloatImage getImage(int bytesPerPixels, Object pixels, int width, int height, double exposure) {
		if(bytesPerPixels == 1) {
			return new FloatImage(width, height, (byte[]) pixels, exposure);
		} else if(bytesPerPixels == 2) {
			return new FloatImage(width, height, (short[]) pixels, exposure);
		} else if(bytesPerPixels == 4) {
			return new FloatImage(width, height, (float[]) pixels, exposure);
		} else {
			return new FloatImage(width, height, (double[]) pixels, exposure);
		}
	}
}
