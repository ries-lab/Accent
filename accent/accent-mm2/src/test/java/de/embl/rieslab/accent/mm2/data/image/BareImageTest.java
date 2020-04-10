package de.embl.rieslab.accent.mm2.data.image;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import de.embl.rieslab.accent.mm2.data.image.BareImage;

public class BareImageTest {

	@Test 
	public void testBareImage() {
		int width = 2;
		int height = 3;
		double exposure = 465.16;

		Object bytes = new byte[width*height];
		Object shorts = new short[width*height];
		Object floats = new float[width*height];
		for(int i=0;i<width*height;i++) {
			((byte[]) bytes)[i] = new Integer(i).byteValue();
			((short[]) shorts)[i] = new Integer(i).shortValue();
			((float[]) floats)[i] = (float) (i*1.125);
		}
		
		BareImage b1 = new BareImage(1, bytes, width, height, exposure);
		assertEquals(width, b1.getWidth());
		assertEquals(height, b1.getHeight());
		assertEquals(exposure, b1.getExposure(),0.0001);
		assertEquals(bytes, b1.getImage());

		BareImage s1 = new BareImage(2, shorts, width, height, exposure);
		assertEquals(width, s1.getWidth());
		assertEquals(height, s1.getHeight());
		assertEquals(exposure, s1.getExposure(),0.0001);
		assertEquals(shorts, s1.getImage());
		
		BareImage f1 = new BareImage(3, floats, width, height, exposure);
		assertEquals(width, f1.getWidth());
		assertEquals(height, f1.getHeight());
		assertEquals(exposure, f1.getExposure(),0.0001);
		assertEquals(floats, f1.getImage());
		
		BareImage f2 = new BareImage(5, floats, width, height, exposure);
		assertEquals(width, f2.getWidth());
		assertEquals(height, f2.getHeight());
		assertEquals(exposure, f2.getExposure(),0.0001);
		assertEquals(floats, f2.getImage());
	}
	
	// didn't manage to get the configuration to run JUnit5, so had to go with JUnit4
	@Test
	public void testBytesBareImageExceptions() {
		int width = 2;
		int height = 3;
		double exposure = 465.16;

		Object bytes = new byte[width*height];
		Object shorts = new short[width*height];
		Object floats = new float[width*height];
		for(int i=0;i<width*height;i++) {
			((byte[]) bytes)[i] = new Integer(i).byteValue();
			((short[]) shorts)[i] = new Integer(i).shortValue();
			((float[]) floats)[i] = (float) (i*1.125);
		}

		try {
			new BareImage(1, shorts, width, height, exposure);
			fail("Expected an IllegalArgumentException to be thrown");
		} catch (IllegalArgumentException e) {
			assertEquals("pixels is not a byte array.", e.getMessage());
		}
		

		try {
			new BareImage(1, shorts, width, height, exposure);
			fail("Expected an IllegalArgumentException to be thrown");
		} catch (IllegalArgumentException e) {
			assertEquals("pixels is not a byte array.", e.getMessage());
		}
		try {
			new BareImage(1, floats, width, height, exposure);
			fail("Expected an IllegalArgumentException to be thrown");
		} catch (IllegalArgumentException e) {
			assertEquals("pixels is not a byte array.", e.getMessage());
		}
		try {
			new BareImage(2, floats, width, height, exposure);
			fail("Expected an IllegalArgumentException to be thrown");
		} catch (IllegalArgumentException e) {
			assertEquals("pixels is not a short array.", e.getMessage());
		}
		try {
			new BareImage(2, bytes, width, height, exposure);
			fail("Expected an IllegalArgumentException to be thrown");
		} catch (IllegalArgumentException e) {
			assertEquals("pixels is not a short array.", e.getMessage());
		}
		try {
			new BareImage(3, bytes, width, height, exposure);
			fail("Expected an IllegalArgumentException to be thrown");
		} catch (IllegalArgumentException e) {
			assertEquals("pixels is not a float array.", e.getMessage());
		}
		try {
			new BareImage(4, shorts, width, height, exposure);
			fail("Expected an IllegalArgumentException to be thrown");
		} catch (IllegalArgumentException e) {
			assertEquals("pixels is not a float array.", e.getMessage());
		}
		
		// wrong sizes
		try {
			new BareImage(1, bytes, width+1, height, exposure);
			fail("Expected an IllegalArgumentException to be thrown");
		} catch (IllegalArgumentException e) {
			assertEquals("Pixel array has the wrong size.", e.getMessage());
		}
		try {
			new BareImage(2, shorts, width+1, height, exposure);
			fail("Expected an IllegalArgumentException to be thrown");
		} catch (IllegalArgumentException e) {
			assertEquals("Pixel array has the wrong size.", e.getMessage());
		}
		try {
			new BareImage(4, floats, width+1, height, exposure);
			fail("Expected an IllegalArgumentException to be thrown");
		} catch (IllegalArgumentException e) {
			assertEquals("Pixel array has the wrong size.", e.getMessage());
		}
	}
}
