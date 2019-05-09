package de.embl.rieslab.accent.common.data.image;

public class BareImage {
	
	private DataType type;
	private Object pixels;
	private int width;
	private int height;
	private int exposure;

	public BareImage(DataType type, Object pixels, int width, int height, int exposure) {
		if(pixels == null || type == null) {
			throw new NullPointerException();
		}
		
		if(type == DataType.BYTE) {
			if(width*height != ((byte[]) pixels).length) {
				throw new IllegalArgumentException();
			}
		} else if(type == DataType.SHORT) {
			if(width*height != ((short[]) pixels).length) {
				throw new IllegalArgumentException();
			}
		} else {
			if(width*height != ((float[]) pixels).length) {
				throw new IllegalArgumentException();
			}
		}
		
		
		this.type = type;
		this.pixels = pixels;
		this.width = width;
		this.height = height;
		this.exposure = exposure;
	}
	
	public BareImage(int bytesPerPixel, Object pixels, int width, int height, int exposure) {
		if(pixels == null) {
			throw new NullPointerException();
		}
		
		if(bytesPerPixel == 1) {
			if(width*height != ((byte[]) pixels).length) {
				throw new IllegalArgumentException();
			} else {
				type = DataType.BYTE;
			}
		} else if(bytesPerPixel == 2) {
			if(width*height != ((short[]) pixels).length) {
				throw new IllegalArgumentException();
			} else {
				type = DataType.BYTE;
			}
		} else {
			if(width*height != ((float[]) pixels).length) {
				throw new IllegalArgumentException();
			} else {
				type = DataType.BYTE;
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
	
	public int getExposure() {
		return exposure;
	}
		
	public enum DataType{
		BYTE, SHORT, FLOAT;
	}
}
