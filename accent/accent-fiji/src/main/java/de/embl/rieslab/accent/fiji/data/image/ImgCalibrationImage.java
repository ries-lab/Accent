package de.embl.rieslab.accent.fiji.data.image;

import de.embl.rieslab.accent.common.interfaces.data.CalibrationImage;
import io.scif.img.ImgSaver;
import net.imagej.Dataset;
import net.imglib2.RandomAccess;
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

	@Override
	public float getPixelValue(int x, int y) {
		if(x>=0 && x<img_.getWidth() && y>=0 && y<img_.getHeight()) {
			RandomAccess<? extends RealType<?>> r = img_.getImgPlus().randomAccess();
			r.localize(new int[] {x,y,0});
			return r.get().getRealFloat();
		}
		return 0;
	}

	@Override
	public int getWidth() {
		return (int) img_.getWidth();
	}

	@Override
	public int getHeight() {
		return (int) img_.getHeight();
	}

	@Override
	public boolean saveAsTiff(String fileName) {
		ImgSaver saver = new ImgSaver();
		saver.saveImg(fileName, img_.getImgPlus().getImg());
		return true;
	}

}
