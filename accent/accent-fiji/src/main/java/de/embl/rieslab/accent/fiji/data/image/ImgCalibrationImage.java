package de.embl.rieslab.accent.fiji.data.image;

import de.embl.rieslab.accent.common.interfaces.data.CalibrationImage;
import net.imagej.Dataset;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.RealType;

public class ImgCalibrationImage implements CalibrationImage{

	private Dataset img_;
	private double exposure_;
	
	public ImgCalibrationImage(Dataset img, double exposure) {
		img_ = img;
		exposure_ = exposure;
	}
	
	@Override
	public Img<RealType<?>> getImage() {
		return img_;
	}

	@Override
	public double getExposure() {
		return exposure_;
	}

}
