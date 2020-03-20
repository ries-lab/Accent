package de.embl.rieslab.accent.common.data.image;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class FloatImageTest {

	// in Java byte and short are signed. We will deal with unsigned images.
	
	@Test
	public void testByteConstructor() {
		int width = 3;
		int height = 4;
		double exposure = 10.5;
				
		byte[] pixels = new byte[width*height];
		for(int i=0;i<width*height;i++) {
			pixels[i] = (new Integer(i)).byteValue();
			assertEquals(i,(int) pixels[i]);
		}
		
		FloatImage im = new FloatImage(width,height,pixels,exposure);

		assertEquals(width, im.getWidth());
		assertEquals(height, im.getHeight());
		assertEquals(exposure, im.getExposure(), 0.0001);

		for(int y=0;y<height;y++) {
			for(int x=0;x<width;x++) {
				int k = y*width+x;
				assertEquals(k,im.getPixelValue(x, y), 0.001);
			}
		}
	}

	@Test
	public void testByteBareImageConstructor() {
		int width = 3;
		int height = 4;
		double exposure = 10.5;
		
		byte[] pixels = new byte[width*height];
		for(int i=0;i<width*height;i++) {
			pixels[i] = (new Integer(i)).byteValue();
			assertEquals(i,(int) pixels[i]);
		}

		BareImage bim = new BareImage(1,pixels,width,height,exposure);
		FloatImage im = new FloatImage(bim);

		assertEquals(width, im.getWidth());
		assertEquals(height, im.getHeight());
		assertEquals(exposure, im.getExposure(), 0.0001);

		for(int y=0;y<height;y++) {
			for(int x=0;x<width;x++) {
				int k = y*width+x;
				assertEquals(k,im.getPixelValue(x, y), 0.001);
			}
		}
	}

	@Test
	public void testAddByteBareImagePixels() {
		int width = 3;
		int height = 4;
		double exposure = 10.5;
		
		int offset = 100; // max signed byte is 127
		
		byte[] pixels = new byte[width*height];
		for(int i=0;i<width*height;i++) {
			pixels[i] = (new Integer(i+offset)).byteValue();
			assertEquals(i+offset,(int) pixels[i]);
		}

		BareImage bim = new BareImage(1,pixels,width,height,exposure);
		FloatImage im = new FloatImage(bim);
		im.addPixels(bim); // expect values > 127, test if no overflow

		for(int y=0;y<height;y++) {
			for(int x=0;x<width;x++) {
				int k = y*width+x+offset;
				assertEquals(2*k,im.getPixelValue(x, y), 0.001);
			}
		}
	}

	@Test
	public void testAddSquareByteBareImagePixels() {
		int width = 3;
		int height = 4;
		double exposure = 10.5;
		
		int offset = 100; // max signed byte is 127
		
		byte[] pixels = new byte[width*height];
		for(int i=0;i<width*height;i++) {
			pixels[i] = (new Integer(i+offset)).byteValue();
			assertEquals(i+offset,(int) pixels[i]);
		}

		BareImage bim = new BareImage(1,pixels,width,height,exposure);
		FloatImage im = new FloatImage(bim);
		im.addSquarePixels(bim);

		for(int y=0;y<height;y++) {
			for(int x=0;x<width;x++) {
				int k = y*width+x+offset;
				assertEquals(k+k*k,im.getPixelValue(x, y), 0.001);
			}
		}
	}

	@Test
	public void testShortConstructor() {
		int width = 3;
		int height = 4;
		double exposure = 10.5;
		
		short[] pixels = new short[width*height];
		for(int i=0;i<width*height;i++) {
			pixels[i] = new Integer(i).shortValue();
			assertEquals(i,(int) pixels[i]);
		}
		
		FloatImage im = new FloatImage(width,height,pixels,exposure);

		assertEquals(width, im.getWidth());
		assertEquals(height, im.getHeight());
		assertEquals(exposure, im.getExposure(), 0.0001);

		for(int y=0;y<height;y++) {
			for(int x=0;x<width;x++) {
				int k = y*width+x;
				assertEquals(k,im.getPixelValue(x, y), 0.001);
			}
		}
	}
	
	@Test
	public void testShortBareImageConstructor() {
		int width = 3;
		int height = 4;
		double exposure = 10.5;
		
		short[] pixels = new short[width*height];
		for(int i=0;i<width*height;i++) {
			pixels[i] = new Integer(i).shortValue();
			assertEquals(i,(int) pixels[i]);
		}

		BareImage bim = new BareImage(2,pixels,width,height,exposure);
		FloatImage im = new FloatImage(bim);

		assertEquals(width, im.getWidth());
		assertEquals(height, im.getHeight());
		assertEquals(exposure, im.getExposure(), 0.0001);

		for(int y=0;y<height;y++) {
			for(int x=0;x<width;x++) {
				int k = y*width+x;
				assertEquals(k,im.getPixelValue(x, y), 0.001);
			}
		}
	}
	
	@Test
	public void testAddShortBareImagePixels() {
		int width = 3;
		int height = 4;
		double exposure = 10.5;
		
		int offset = 32000; // max short is 32,767
		
		short[] pixels = new short[width*height];
		for(int i=0;i<width*height;i++) {
			pixels[i] = new Integer(i+offset).shortValue();
			assertEquals(i+offset,(int) pixels[i]);
		}

		BareImage bim = new BareImage(2,pixels,width,height,exposure);
		FloatImage im = new FloatImage(bim);
		im.addPixels(bim);

		for(int y=0;y<height;y++) {
			for(int x=0;x<width;x++) {
				int k = y*width+x+offset;
				assertEquals(2*k,im.getPixelValue(x, y), 0.001);
			}
		}
	}

	
	@Test
	public void testAddSquareShortBareImagePixels() {
		int width = 3;
		int height = 4;
		double exposure = 10.5;
		
		int offset = 32000; // max short is 32,767
		
		short[] pixels = new short[width*height];
		for(int i=0;i<width*height;i++) {
			pixels[i] = new Integer(i+offset).shortValue();
			assertEquals(i+offset,(int) pixels[i]);
		}

		BareImage bim = new BareImage(2,pixels,width,height,exposure);
		FloatImage im = new FloatImage(bim);
		im.addSquarePixels(bim);

		for(int y=0;y<height;y++) {
			for(int x=0;x<width;x++) {
				int k = y*width+x+offset;
				assertEquals((float) k+k*k,im.getPixelValue(x, y), 0.001);
			}
		}
	}
	
	@Test
	public void testFloatConstructor() {
		int width = 3;
		int height = 4;
		double exposure = 10.5;
		
		float[] pixels = new float[width*height];
		for(int i=0;i<width*height;i++) {
			pixels[i] = (float) (i*1.125);
			assertEquals(i*1.125,(float) pixels[i],0.0001);
		}
		
		FloatImage im = new FloatImage(width,height,pixels,exposure);

		assertEquals(width, im.getWidth());
		assertEquals(height, im.getHeight());
		assertEquals(exposure, im.getExposure(), 0.0001);

		for(int y=0;y<height;y++) {
			for(int x=0;x<width;x++) {
				float k = (float) ((y*width+x)*1.125);
				assertEquals(k,im.getPixelValue(x, y), 0.001);
			}
		}
	}

	@Test
	public void testFloatBareImageConstructor() {
		int width = 3;
		int height = 4;
		double exposure = 10.5;
		
		float[] pixels = new float[width*height];
		for(int i=0;i<width*height;i++) {
			pixels[i] = (float) (i*1.125);
			assertEquals(i*1.125,(float) pixels[i],0.0001);
		}

		BareImage bim = new BareImage(3,pixels,width,height,exposure);
		FloatImage im = new FloatImage(bim);

		assertEquals(width, im.getWidth());
		assertEquals(height, im.getHeight());
		assertEquals(exposure, im.getExposure(), 0.0001);

		for(int y=0;y<height;y++) {
			for(int x=0;x<width;x++) {
				float k = (float) ((y*width+x)*1.125);
				assertEquals(k,im.getPixelValue(x, y), 0.001);
			}
		}
	}
	
	@Test
	public void testAddFloatBareImagePixels() {
		int width = 3;
		int height = 4;
		double exposure = 10.5;
		
		float[] pixels = new float[width*height];
		for(int i=0;i<width*height;i++) {
			pixels[i] = (float) (i*1.125);
			assertEquals(i*1.125,(float) pixels[i],0.0001);
		}

		BareImage bim = new BareImage(3,pixels,width,height,exposure);
		FloatImage im = new FloatImage(bim);
		im.addPixels(bim);

		for(int y=0;y<height;y++) {
			for(int x=0;x<width;x++) {
				float k = (float) ((y*width+x)*1.125);
				assertEquals(2*k,im.getPixelValue(x, y), 0.001);
			}
		}
	}

	
	@Test
	public void testAddSquareFloatBareImagePixels() {
		int width = 3;
		int height = 4;
		double exposure = 10.5;
		
		float[] pixels = new float[width*height];
		for(int i=0;i<width*height;i++) {
			pixels[i] = (float) (i*1.125);
			assertEquals(i*1.125,(float) pixels[i],0.0001);
		}

		BareImage bim = new BareImage(3,pixels,width,height,exposure);
		FloatImage im = new FloatImage(bim);
		im.addSquarePixels(bim);

		for(int y=0;y<height;y++) {
			for(int x=0;x<width;x++) {
				float k = (float) ((y*width+x)*1.125);
				assertEquals(k+k*k,im.getPixelValue(x, y), 0.001);
			}
		}
	}
		
	@Test
	public void testDoubleConstructor() {
		int width = 3;
		int height = 4;
		double exposure = 10.5;
		
		double[] pixels = new double[width*height];
		for(int i=0;i<width*height;i++) {
			pixels[i] = i*1.125;
			assertEquals(i*1.125, pixels[i],0.0001);
		}
		
		FloatImage im = new FloatImage(width,height,pixels,exposure);

		assertEquals(width, im.getWidth());
		assertEquals(height, im.getHeight());
		assertEquals(exposure, im.getExposure(), 0.0001);

		for(int y=0;y<height;y++) {
			for(int x=0;x<width;x++) {
				double k = (y*width+x)*1.125;
				assertEquals(k,im.getPixelValue(x, y), 0.001);
			}
		}
	}
	

	@Test
	public void testSquareFloatImage() {

	}
	
	@Test
	public void testDividePixelsFloatImage() {

	}
	
	@Test
	public void testSaveAsTiffs() {

	}
}
