package de.embl.rieslab.accent.mm2.data.image;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

import de.embl.rieslab.accent.mm2.data.image.BareImage;
import de.embl.rieslab.accent.mm2.data.image.FloatImage;

public class FloatImageTest {

	// in Java byte and short are signed. We will deal with unsigned images.
	
	@Test
	public void testByteConstructor() {
		int width = 3;
		int height = 4;
		double exposure = 10.5;
			
		// to get almost the full dynamical range
		int offset = -128;
		int slope = 20;
		
		byte[] pixels = new byte[width*height];
		for(int i=0;i<width*height;i++) {
			byte val = (byte) (i*slope+offset);
			pixels[i] = val;
			assertEquals(val,(int) pixels[i]);
		}
		
		FloatImage im = new FloatImage(width,height,pixels,exposure);

		assertEquals(width, im.getWidth());
		assertEquals(height, im.getHeight());
		assertEquals(exposure, im.getExposure(), 0.0001);

		for(int y=0;y<height;y++) {
			for(int x=0;x<width;x++) {
				byte k = (byte) (slope*(y*width+x)+offset);
				float val = (float) Byte.toUnsignedInt(k);
				assertEquals(val,im.getPixelValue(x, y), 0.001);
			}
		}
	}

	@Test
	public void testByteBareImageConstructor() {
		int width = 3;
		int height = 4;
		double exposure = 10.5;

		// to get almost the full dynamical range
		int offset = -128;
		int slope = 20;
		
		byte[] pixels = new byte[width*height];
		for(int i=0;i<width*height;i++) {
			byte val = (byte) (i*slope+offset);
			pixels[i] = val;
			assertEquals(val,(int) pixels[i]);
		}

		BareImage bim = new BareImage(1,pixels,width,height,exposure);
		FloatImage im = new FloatImage(bim);

		assertEquals(width, im.getWidth());
		assertEquals(height, im.getHeight());
		assertEquals(exposure, im.getExposure(), 0.0001);

		for(int y=0;y<height;y++) {
			for(int x=0;x<width;x++) {
				byte k = (byte) (slope*(y*width+x)+offset);
				float val = (float) Byte.toUnsignedInt(k);
				assertEquals(val,im.getPixelValue(x, y), 0.001);
			}
		}
	}

	@Test
	public void testAddByteBareImagePixels() {
		int width = 3;
		int height = 4;
		double exposure = 10.5;

		// to get almost the full dynamical range
		int offset = -128;
		int slope = 20;
		
		byte[] pixels = new byte[width*height];
		for(int i=0;i<width*height;i++) {
			byte val = (byte) (i*slope+offset);
			pixels[i] = val;
			assertEquals(val,(int) pixels[i]);
		}

		BareImage bim = new BareImage(1,pixels,width,height,exposure);
		FloatImage im = new FloatImage(bim);
		im.addPixels(bim); // expect values > 127, test if no overflow

		for(int y=0;y<height;y++) {
			for(int x=0;x<width;x++) {
				byte k = (byte) (slope*(y*width+x)+offset);
				float val = (float) Byte.toUnsignedInt(k);
				assertEquals(2*val,im.getPixelValue(x, y), 0.001);
			}
		}
	}

	@Test
	public void testAddSquareByteBareImagePixels() {
		int width = 3;
		int height = 4;
		double exposure = 10.5;

		// to get almost the full dynamical range
		int offset = -128;
		int slope = 20;
		
		byte[] pixels = new byte[width*height];
		for(int i=0;i<width*height;i++) {
			byte val = (byte) (i*slope+offset);
			pixels[i] = val;
			assertEquals(val,(int) pixels[i]);
		}

		BareImage bim = new BareImage(1,pixels,width,height,exposure);
		FloatImage im = new FloatImage(bim);
		im.addSquarePixels(bim);

		for(int y=0;y<height;y++) {
			for(int x=0;x<width;x++) {
				byte k = (byte) (slope*(y*width+x)+offset);
				float val = (float) Byte.toUnsignedInt(k);
				assertEquals(val+val*val,im.getPixelValue(x, y), 0.001);
			}
		}
	}

	@Test
	public void testShortConstructor() {
		int width = 3;
		int height = 4;
		double exposure = 10.5;

		// this should cover almost the entire range of short
		int offset = -32000;
		int slope = 5300;
		
		short[] pixels = new short[width*height];
		for(int i=0;i<width*height;i++) {
			short val = (short) (offset+slope*i);
			pixels[i] = val;
			assertEquals(val,(int) pixels[i]);
		}
		
		FloatImage im = new FloatImage(width,height,pixels,exposure);

		assertEquals(width, im.getWidth());
		assertEquals(height, im.getHeight());
		assertEquals(exposure, im.getExposure(), 0.0001);

		for(int y=0;y<height;y++) {
			for(int x=0;x<width;x++) {
				Short k = (short) (slope*(y*width+x)+offset);
				float val = (float) Short.toUnsignedInt(k);
				assertEquals(val,im.getPixelValue(x, y), 0.001);
			}
		}
	}
	
	@Test
	public void testShortBareImageConstructor() {
		int width = 3;
		int height = 4;
		double exposure = 10.5;
		
		// this should cover almost the entire range of short
		int offset = -32000;
		int slope = 5300;
		
		short[] pixels = new short[width*height];
		for(int i=0;i<width*height;i++) {			
			short val = (short) (offset+slope*i);
			pixels[i] = val;
			assertEquals(val, pixels[i]);
		}

		BareImage bim = new BareImage(2,pixels,width,height,exposure);
		FloatImage im = new FloatImage(bim);

		assertEquals(width, im.getWidth());
		assertEquals(height, im.getHeight());
		assertEquals(exposure, im.getExposure(), 0.0001);

		for(int y=0;y<height;y++) {
			for(int x=0;x<width;x++) {
				Short k = (short) (slope*(y*width+x)+offset);
				float val = (float) Short.toUnsignedInt(k);
				assertEquals(val,im.getPixelValue(x, y), 0.001);
			}
		}
	}
	
	@Test
	public void testAddShortBareImagePixels() {
		int width = 3;
		int height = 4;
		double exposure = 10.5;

		// this should cover almost the entire range of short
		int offset = -32000;
		int slope = 5300;
		
		short[] pixels = new short[width*height];
		for(int i=0;i<width*height;i++) {		
			short val = (short) (offset+slope*i);
			pixels[i] = val;
			assertEquals(val, pixels[i]);
		}

		BareImage bim = new BareImage(2,pixels,width,height,exposure);
		FloatImage im = new FloatImage(bim);
		im.addPixels(bim);

		for(int y=0;y<height;y++) {
			for(int x=0;x<width;x++) {
				Short k = (short) (slope*(y*width+x)+offset);
				float val = (float) Short.toUnsignedInt(k);
				assertEquals(2*val,im.getPixelValue(x, y), 0.001);
			}
		}
	}

	
	@Test
	public void testAddSquareShortBareImagePixels() {
		int width = 3;
		int height = 4;
		double exposure = 10.5;

		// this should cover almost the entire range of short
		int offset = -32000;
		int slope = 5300;
		
		short[] pixels = new short[width*height];
		for(int i=0;i<width*height;i++) {	
			short val = (short) (offset+slope*i);
			pixels[i] = val;
			assertEquals(val, pixels[i]);
		}

		BareImage bim = new BareImage(2,pixels,width,height,exposure);
		FloatImage im = new FloatImage(bim);
		im.addSquarePixels(bim);

		for(int y=0;y<height;y++) {
			for(int x=0;x<width;x++) {
				Short k = (short) (slope*(y*width+x)+offset);
				float val = (float) Short.toUnsignedInt(k);
				assertEquals(val+val*val,im.getPixelValue(x, y), 0.001);
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
	public void testFloatImageConstructor() {
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
		FloatImage im2 = new FloatImage(im);
		
		im.addPixels(bim);

		for(int y=0;y<height;y++) {
			for(int x=0;x<width;x++) {
				if(!(x==0 && y==0))
					assertNotEquals(im2.getPixelValue(x, y),im.getPixelValue(x, y), 0.001);
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
		int width = 3;
		int height = 4;
		double exposure = 10.5;
		
		int offset = 32000; // max short is 32,767		
		
		short[] pixels = new short[width*height];
		for(int i=0;i<width*height;i++) {
			pixels[i] = new Integer(i+offset).shortValue();
			assertEquals(i+offset,(int) pixels[i]);
		}
		
		FloatImage im = new FloatImage(width,height,pixels,exposure);
		im.square();

		for(int y=0;y<height;y++) {
			for(int x=0;x<width;x++) {
				float k = y*width+x+offset;
				assertEquals(k*k,im.getPixelValue(x, y), 0.0001);
			}
		}
	}
	
	@Test
	public void testDividePixelsFloatImage() {
		int width = 3;
		int height = 4;
		double exposure = 10.5;
		
		int offset = 32000; // max short is 32,767		
		
		short[] pixels = new short[width*height];
		for(int i=0;i<width*height;i++) {
			pixels[i] = new Integer(i+offset).shortValue();
			assertEquals(i+offset,(int) pixels[i]);
		}
		
		FloatImage im = new FloatImage(width,height,pixels,exposure);
		float d = (float) 10.568;
		im.dividePixels(d);

		for(int y=0;y<height;y++) {
			for(int x=0;x<width;x++) {
				float k = y*width+x+offset;
				assertEquals(k/d,im.getPixelValue(x, y), 0.0001);
			}
		}
	}
}
