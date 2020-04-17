package de.embl.rieslab.accent.mm2.data.image;

import de.embl.rieslab.accent.common.interfaces.data.ImageSaver;
import ij.ImagePlus;
import ij.io.FileSaver;


public class FloatImageSaver implements ImageSaver<FloatImage>{

	@Override
	public boolean saveAsTiff(FloatImage image, String filePath) {
		if (!(filePath.endsWith(".tif") || filePath.endsWith(".tiff") || filePath.endsWith(".TIF")
				|| filePath.endsWith(".TIFF"))) {
			filePath = filePath + ".tif";
		}
		
		FileSaver fs = new FileSaver(new ImagePlus("", image.getImage()));
		return fs.saveAsTiff(filePath);
	}

}
