package de.embl.rieslab.accent.mm2.data.image;

import de.embl.rieslab.accent.common.interfaces.data.ArrayToImage;

public class ArrayToFloatImage implements ArrayToImage<FloatImage>{

	@Override
	public FloatImage getImage(double[] pixels, int width, int height, double exposure) {
		return new FloatImage(pixels, width, height, exposure);
	}

	@Override
	public FloatImage getImage(float[] pixels, int width, int height, double exposure) {
		return new FloatImage(pixels, width, height, exposure);
	}
}
