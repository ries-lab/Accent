package de.embl.rieslab.accent.common.data.image;

public class BareImage {
	
	private DataType type;
	private Object pixels;
	private int width;
	private int height;
	private double exposure;

	// should reconsider using the datatype object, not really useful here. But used in FloatImage to avoid recasting
	public BareImage(DataType type, Object pixels, int width, int height, double exposure) {
		if(pixels == null || type == null) {
			throw new NullPointerException();
		}
		
		if(type == DataType.BYTE) {
			if(!(pixels instanceof byte[])) {
				throw new IllegalArgumentException("pixels is not a byte array.");
			}
			
			if(width*height != ((byte[]) pixels).length) {
				throw new IllegalArgumentException("The array has the wrong size.");
			}
		} else if(type == DataType.SHORT) {
			if(!(pixels instanceof short[])) {
				throw new IllegalArgumentException("pixels is not a short array.");
			}
			if(width*height != ((short[]) pixels).length) {
				throw new IllegalArgumentException("The array has the wrong size.");
			}
		} else {
			if(!(pixels instanceof float[])) {
				throw new IllegalArgumentException("pixels is not a float array.");
			}
			if(width*height != ((float[]) pixels).length) {
				throw new IllegalArgumentException("The array has the wrong size.");
			}
		}
		
		this.type = type;
		this.pixels = pixels;
		this.width = width;
		this.height = height;
		this.exposure = exposure;
	}
	
	public BareImage(int bytesPerPixel, Object pixels, int width, int height, double exposure) {
		if(pixels == null) {
			throw new NullPointerException();
		}
		
		if(bytesPerPixel == 1) {
			if(!(pixels instanceof byte[])) {
				throw new IllegalArgumentException("pixels is not a byte array.");
			}
			if(width*height != ((byte[]) pixels).length) {
				throw new IllegalArgumentException();
			} else {
				type = DataType.BYTE;
			}
		} else if(bytesPerPixel == 2) {
			if(!(pixels instanceof short[])) {
				throw new IllegalArgumentException("pixels is not a short array.");
			}
			if(width*height != ((short[]) pixels).length) {
				throw new IllegalArgumentException();
			} else {
				type = DataType.SHORT;
			}
		} else {
			if(!(pixels instanceof float[])) {
				throw new IllegalArgumentException("pixels is not a float array.");
			}
			if(width*height != ((float[]) pixels).length) {
				throw new IllegalArgumentException();
			} else {
				type = DataType.FLOAT;
			}
		}
		
		this.pixels = pixels;
		this.width = width;
		this.height = height;
		this.exposure = exposure;
	}
	
	public DataType getDataType() {
		return type;
	}
	
	public Object getPixels() {
		return pixels;
	}
	
	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
	
	public double getExposure() {
		return exposure;
	}
		
	public enum DataType{
		BYTE, SHORT, FLOAT;
	}
}
