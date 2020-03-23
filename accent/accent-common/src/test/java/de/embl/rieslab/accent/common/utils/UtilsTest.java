package de.embl.rieslab.accent.common.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class UtilsTest {

	@Test
	public void testIsInteger() {
		assertTrue(utils.isInteger("0"));
		assertTrue(utils.isInteger("-50"));
		assertTrue(utils.isInteger("699"));
		
		// edge cases
		assertFalse(utils.isInteger(null));
		assertFalse(utils.isInteger(""));
		assertFalse(utils.isInteger("."));
		assertFalse(utils.isInteger(","));
		assertFalse(utils.isInteger("55d"));
		assertFalse(utils.isInteger("1.025"));
		assertFalse(utils.isInteger("5,4"));
		assertFalse(utils.isInteger("5.0"));
	}
	
	@Test
	public void testExtractExposureFromString() {
		assertEquals(10,utils.extractExposureMs("sdsfsdf_10ms"), 0.0001);
		assertEquals(9,utils.extractExposureMs("9ms"), 0.0001);
		assertEquals(99,utils.extractExposureMs("158_45-99ms"), 0.0001);
		assertEquals(9.9,utils.extractExposureMs("158ms_4ms5-9.9ms"), 0.0001);
		assertEquals(6,utils.extractExposureMs("158ms_4ms5-9,6ms"), 0.0001);
		assertEquals(99,utils.extractExposureMs("158_45-99ms.tiff"), 0.0001);
		assertEquals(56,utils.extractExposureMs("15ms_dfjhms_51ms56ms.tiff"), 0.0001);
		

		// edge cases
		assertEquals(0,utils.extractExposureMs(null), 0.0001);
		assertEquals(0,utils.extractExposureMs(""), 0.0001);
		assertEquals(0,utils.extractExposureMs("ms"), 0.0001);
		assertEquals(0,utils.extractExposureMs("fjkdsdhb65dfjbfe54"), 0.0001);
		assertEquals(0,utils.extractExposureMs("15564618"), 0.0001);
	}
	
}
