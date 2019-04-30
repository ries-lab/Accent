package main.java.embl.rieslab.accent.data;

import org.micromanager.data.Image;

import io.scif.img.ImgSaver;
import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.numeric.real.FloatType;

public class FloatImage {

	private final int exposure;
	private Img<FloatType> img;
	
	public FloatImage(ImageExposurePair impair) {
		this.exposure = impair.getExposure();

		final ImgFactory< FloatType > imgFactory = new ArrayImgFactory<FloatType>();
		long[] dims = {impair.getImage().getWidth(),impair.getImage().getHeight()};
		img = imgFactory.create(dims, new FloatType());
		
		if(impair.getImage().getBytesPerPixel() == 1) {
			setPixels(impair.getImage().getWidth(),impair.getImage().getHeight(), (byte[]) impair.getImage().getRawPixels());
		} else {
			setPixels(impair.getImage().getWidth(),impair.getImage().getHeight(), (short[]) impair.getImage().getRawPixels());
		}
	}	
	
	public FloatImage(Image im, int exposure) {
		this.exposure = exposure;

		final ImgFactory< FloatType > imgFactory = new ArrayImgFactory<FloatType>();
		long[] dims = {im.getWidth(),im.getHeight()};
		img = imgFactory.create(dims, new FloatType());
		
		if(im.getBytesPerPixel() == 1) {
			setPixels(im.getWidth(),im.getHeight(), (byte[]) im.getRawPixels());
		} else {
			setPixels(im.getWidth(),im.getHeight(), (short[]) im.getRawPixels());
		}
	}

	public FloatImage(int width, int height, byte[] pixels, int exposure) {
		this.exposure = exposure;
		
		final ImgFactory< FloatType > imgFactory = new ArrayImgFactory<FloatType>();
		long[] dims = {width,height};
		img = imgFactory.create(dims, new FloatType());

		setPixels(width, height, pixels);
	}

	public FloatImage(int width, int height, short[] pixels, int exposure) {
		this.exposure = exposure;
		
		final ImgFactory< FloatType > imgFactory = new ArrayImgFactory<FloatType>();
		long[] dims = {width,height};
		img = imgFactory.create(dims, new FloatType());

		setPixels(width, height, pixels);
	}
	
	public FloatImage(int width, int height, double[] pixels, int exposure) {
		this.exposure = exposure;
		
		final ImgFactory< FloatType > imgFactory = new ArrayImgFactory<FloatType>();
		long[] dims = {width,height};
		img = imgFactory.create(dims, new FloatType());

		setPixels(width, height, pixels);
	}
	
	public FloatImage(FloatImage image) {
		this.exposure = image.getExposure();
		
		img = image.getImage().factory().create(image.getImage(), new FloatType());
		
		// create a cursor for both images
		Cursor<FloatType> cursorInput = image.getImage().cursor();
		Cursor<FloatType> cursorOutput = img.cursor();

		while (cursorInput.hasNext()) {
			cursorInput.fwd();
			cursorOutput.fwd();
			
			cursorOutput.get().set(cursorInput.get());
		}
	}

	private void setPixels(int width, int height, byte[] pixels) {
		Cursor<FloatType> cu = img.localizingCursor();
		int x,y;
		while(cu.hasNext()) {
			cu.fwd();
			x = cu.getIntPosition(0);
			y = cu.getIntPosition(1);
			
			cu.get().set(Byte.toUnsignedInt(pixels[x+y*width]));
		}
	}

	private void setPixels(int width, int height, double[] pixels) {
		Cursor<FloatType> cu = img.localizingCursor();
		int x,y;
		while(cu.hasNext()) {
			cu.fwd();
			x = cu.getIntPosition(0);
			y = cu.getIntPosition(1);
			
			cu.get().set((float) pixels[x+y*width]);
		}
	}
	
	private void setPixels(int width, int height, short[] pixels) {
		Cursor<FloatType> cu = img.localizingCursor();
		int x,y;
		while(cu.hasNext()) {
			cu.fwd();
			x = cu.getIntPosition(0);
			y = cu.getIntPosition(1);
			
			cu.get().set(Short.toUnsignedInt(pixels[x+y*width]));
		}
	}
	
	
	public int getExposure() {
		return exposure;
	}
	
	public long getWidth() {
		return img.dimension(0);
	}
	
	public long getHeight() {
		return  img.dimension(1);
	}
	
	public Img<FloatType> getImage() {
		return img;
	}

	public void addPixels(Image image) {
		if(image == null) {
			throw new NullPointerException();
		}

		if(image.getWidth() != getWidth() || image.getHeight() != getHeight()) {
			throw new IllegalArgumentException();
		}

		if(image.getBytesPerPixel() == 1) {
			addPixels(image.getWidth(), image.getHeight(), (byte[]) image.getRawPixels());
		} else {
			addPixels(image.getWidth(), image.getHeight(), (short[]) image.getRawPixels());
		}

	}

	public void addPixels(int width, int height, byte[] pixels) {
		Cursor<FloatType> cu = img.localizingCursor();
		int x,y;
		while(cu.hasNext()) {
			cu.fwd();
			x = cu.getIntPosition(0);
			y = cu.getIntPosition(1);
			
			cu.get().set(cu.get().get()+Byte.toUnsignedInt(pixels[x+y*width]));
		}
		
	}
	
	private void addPixels(int width, int height, short[] pixels) {
		Cursor<FloatType> cu = img.localizingCursor();
		int x,y;
		while(cu.hasNext()) {
			cu.fwd();
			x = cu.getIntPosition(0);
			y = cu.getIntPosition(1);
			
			cu.get().set(cu.get().get()+Short.toUnsignedInt(pixels[x+y*width]));
		}
		
	}
	
	public void dividePixels(float d) {
		if(Math.abs(d) > 0.01) {
			Cursor<FloatType> curs = img.cursor();
			
			while(curs.hasNext()) {
				curs.fwd();
				curs.get().set(curs.get().get() / d);
			}
		}
	}
	
	public void addSquarePixels(Image image) {
		if(image == null) {
			throw new NullPointerException();
		}

		if(image.getWidth() != getWidth() || image.getHeight() != getHeight()) {
			throw new IllegalArgumentException();
		}

		if(image.getBytesPerPixel() == 1) {
			addSqPixels(image.getWidth(), image.getHeight(), (byte[]) image.getRawPixels());
		} else {
			addSqPixels(image.getWidth(), image.getHeight(), (short[]) image.getRawPixels());
		}
	}

	private void addSqPixels(int width, int height, byte[] pixels) {
		Cursor<FloatType> cu = img.localizingCursor();
		int x,y;
		while(cu.hasNext()) {
			cu.fwd();
			x = cu.getIntPosition(0);
			y = cu.getIntPosition(1);
			
			int val = Byte.toUnsignedInt(pixels[x+y*width]);
			cu.get().set(cu.get().get()+val*val);
		}
	}
	
	private void addSqPixels(int width, int height, short[] pixels) {
		Cursor<FloatType> cu = img.localizingCursor();
		int x,y;
		while(cu.hasNext()) {
			cu.fwd();
			x = cu.getIntPosition(0);
			y = cu.getIntPosition(1);
			
			int val = Short.toUnsignedInt(pixels[x+y*width]);
			cu.get().set(cu.get().get()+val*val);
		}
	}
	
	public void square() {
		Cursor<FloatType> curs = img.cursor();
		
		while(curs.hasNext()) {
			curs.fwd();
			curs.get().set(curs.get().get() * curs.get().get());
		}
	}
	
	public void toVariance(Img<FloatType> meanImg, float size) {
		if(meanImg == null) {
			throw new NullPointerException();
		}
		
		if(meanImg.dimension(0) != getWidth() || meanImg.dimension(1) != getHeight()) {
			throw new IllegalArgumentException();
		}

		Cursor<FloatType> curs = img.cursor();
		RandomAccess<FloatType> randomAccess = meanImg.randomAccess();

		while (curs.hasNext()) {
			curs.fwd();
			randomAccess.setPosition(curs);
			float mean = randomAccess.get().get();
			curs.get().set(curs.get().get() / size - mean*mean);
		}
	}
	
	public void saveAsTiff(String path) {
		ImgSaver saver = new ImgSaver();
		try {
			saver.saveImg(path, img);
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}
	
	public float getPixelValue(int x, int y) { // what are the performances of such access?
		RandomAccess<FloatType> ra = img.randomAccess();
		ra.setPosition(x, 0);
		ra.setPosition(y, 1);
		return ra.get().get();
	}
}
