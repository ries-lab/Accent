package de.embl.rieslab.accent.mm2.data.image;

import de.embl.rieslab.accent.common.interfaces.data.ArrayToImage;

public class ArrayToFloatImage implements ArrayToImage<FloatImage>{

	@Override
	public FloatImage getImage(int bytesPerPixels, Object pixels, int width, int height, double exposure) {
		return new FloatImage(new BareImage(bytesPerPixels, pixels, width, height, exposure));
	}

}
