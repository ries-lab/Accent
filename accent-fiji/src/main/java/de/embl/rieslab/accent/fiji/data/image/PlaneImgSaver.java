package de.embl.rieslab.accent.fiji.data.image;

import de.embl.rieslab.accent.common.interfaces.data.ImageSaver;
import io.scif.img.ImgSaver;

public class PlaneImgSaver implements ImageSaver<PlaneImg>{

	@Override
	public boolean saveAsTiff(PlaneImg image, String filePath) {
		ImgSaver saver = new ImgSaver();
		saver.saveImg(filePath, image.getImage());
		return true;
	}

}
