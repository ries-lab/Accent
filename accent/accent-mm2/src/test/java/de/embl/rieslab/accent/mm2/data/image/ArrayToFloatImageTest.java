package de.embl.rieslab.accent.mm2.data.image;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ArrayToFloatImageTest {
	
	@Test
	public void testArrayToImage() {
		ArrayToFloatImage conv = new ArrayToFloatImage();
		
		int width = 5, height = 12;
		double exposure = 45.4;
		
		// bytes
		byte[] bytes = new byte[width*height];
		for(int x=0;x<width;x++) {
			for(int y=0;y<height;y++) {
				int p = y*width+x;
				bytes[p] = (byte) p;
			}
		}
		FloatImage f = conv.getImage(1, bytes, width, height, exposure);
		
		assertEquals(exposure, f.getExposure(),0.00001);
		assertEquals(width, f.getWidth());
		assertEquals(height, f.getHeight());
		
		for(int x=0;x<width;x++) {
			for(int y=0;y<height;y++) {
				int p = y*width+x;
				assertEquals(bytes[p], f.getPixelValue(x, y), 0.0001);
			}
		}

		// short
		short[] shorts = new short[width*height];
		for(int x=0;x<width;x++) {
			for(int y=0;y<height;y++) {
				int p = y*width+x;
				shorts[p] = (short) (p*100);
			}
		}
		f = conv.getImage(2, shorts, width, height, exposure);
		
		assertEquals(exposure, f.getExposure(),0.00001);
		assertEquals(width, f.getWidth());
		assertEquals(height, f.getHeight());
		
		for(int x=0;x<width;x++) {
			for(int y=0;y<height;y++) {
				int p = y*width+x;
				assertEquals(shorts[p], f.getPixelValue(x, y), 0.0001);
			}
		}

		// float
		float[] floats = new float[width*height];
		for(int x=0;x<width;x++) {
			for(int y=0;y<height;y++) {
				int p = y*width+x;
				floats[p] = (float) (p*12.45);
			}
		}
		f = conv.getImage(4, floats, width, height, exposure);
		
		assertEquals(exposure, f.getExposure(),0.00001);
		assertEquals(width, f.getWidth());
		assertEquals(height, f.getHeight());
		
		for(int x=0;x<width;x++) {
			for(int y=0;y<height;y++) {
				int p = y*width+x;
				assertEquals(floats[p], f.getPixelValue(x, y), 0.0001);
			}
		}

		// double
		double[] doubles = new double[width*height];
		for(int x=0;x<width;x++) {
			for(int y=0;y<height;y++) {
				int p = y*width+x;
				doubles[p] = p*12.45;
			}
		}
		f = conv.getImage(8, doubles, width, height, exposure);
		
		assertEquals(exposure, f.getExposure(),0.00001);
		assertEquals(width, f.getWidth());
		assertEquals(height, f.getHeight());
		
		for(int x=0;x<width;x++) {
			for(int y=0;y<height;y++) {
				int p = y*width+x;
				assertEquals(doubles[p], f.getPixelValue(x, y), 0.0001);
			}
		}
		
		
	}
}
