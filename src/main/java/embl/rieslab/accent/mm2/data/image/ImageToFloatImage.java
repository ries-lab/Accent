package main.java.embl.rieslab.accent.mm2.data.image;

import org.micromanager.data.Image;

import ij.process.FloatProcessor;
import main.java.embl.rieslab.accent.common.data.image.FloatImage;

public class ImageToFloatImage {

	private FloatImage im;
	
	public ImageToFloatImage(Image mm2_im, int exposure) {
		
		if(mm2_im.getBytesPerPixel() == 1) {
			im = new FloatImage(im.getWidth(),im.getHeight(), (byte[]) mm2_im.getRawPixels(), exposure);
		} else {
			im = new FloatImage(im.getWidth(),im.getHeight(), (short[]) mm2_im.getRawPixels(), exposure);
		}
	}	
	
	public ImageToFloatImage(ImageToFloatImage imtofloat) {
		im = new FloatImage(imtofloat.getFloatImage());
	}	
	
	public ImageToFloatImage(ImageExposurePair imexp) {
		
		Image mm2_im = imexp.getImage();
		if(mm2_im.getBytesPerPixel() == 1) {
			im = new FloatImage(im.getWidth(),im.getHeight(), (byte[]) mm2_im.getRawPixels(), imexp.getExposure());
		} else {
			im = new FloatImage(im.getWidth(),im.getHeight(), (short[]) mm2_im.getRawPixels(), imexp.getExposure());
		}
	}
	
	public void addPixels(Image image) {
		if(image == null) {
			throw new NullPointerException();
		}

		if(image.getWidth() != im.getWidth() || image.getHeight() != im.getHeight()) {
			throw new IllegalArgumentException();
		}

		if(image.getBytesPerPixel() == 1) {
			im.addPixels((byte[]) image.getRawPixels());
		} else {
			im.addPixels((short[]) image.getRawPixels());
		}
	}
	
	public void addSquarePixels(Image image) {
		if(image == null) {
			throw new NullPointerException();
		}

		if(image.getWidth() != im.getWidth() || image.getHeight() != im.getHeight()) {
			throw new IllegalArgumentException();
		}

		if(image.getBytesPerPixel() == 1) {
			im.addSquarePixels((byte[]) image.getRawPixels());
		} else {
			im.addSquarePixels((short[]) image.getRawPixels());
		}
	}

	public void dividePixels(int i) {
		im.dividePixels(i);
	}

	public void toVariance(ImageToFloatImage image, int i) {
		im.toVariance(image.getFloatImage().getImage(),i);
	}

	public void square() {
		im.square();
	}
	
	public FloatImage getFloatImage() {
		return im;
	}
}
