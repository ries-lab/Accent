package de.embl.rieslab.accent.mm2.data.image;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ArrayToFloatImageTest {
	
	@Test
	public void testArrayToImage() {
		ArrayToFloatImage conv = new ArrayToFloatImage();
		
		int width = 5, height = 12;
		double exposure = 45.4;
		
		// float
		float[] floats = new float[width*height];
		for(int x=0;x<width;x++) {
			for(int y=0;y<height;y++) {
				int p = y*width+x;
				floats[p] = (float) (p*12.45);
			}
		}
		FloatImage f = conv.getImage(floats, width, height, exposure);
		
		assertEquals(exposure, f.getExposure(),0.00001);
		assertEquals(width, f.getWidth());
		assertEquals(height, f.getHeight());
		
		for(int x=0;x<width;x++) {
			for(int y=0;y<height;y++) {
				int p = y*width+x;
				assertEquals(floats[p], f.getPixelValue(x, y), 0.0001);
			}
		}		
	}
}
