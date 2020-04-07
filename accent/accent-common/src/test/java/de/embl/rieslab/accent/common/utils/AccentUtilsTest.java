package de.embl.rieslab.accent.common.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class AccentUtilsTest {

	@Test
	public void testIsInteger() {
		assertTrue(AccentUtils.isInteger("0"));
		assertTrue(AccentUtils.isInteger("-50"));
		assertTrue(AccentUtils.isInteger("699"));
		
		// edge cases
		assertFalse(AccentUtils.isInteger(null));
		assertFalse(AccentUtils.isInteger(""));
		assertFalse(AccentUtils.isInteger("."));
		assertFalse(AccentUtils.isInteger(","));
		assertFalse(AccentUtils.isInteger("55d"));
		assertFalse(AccentUtils.isInteger("1.025"));
		assertFalse(AccentUtils.isInteger("5,4"));
		assertFalse(AccentUtils.isInteger("5.0"));
	}
	
	@Test
	public void testIsNumeric() {
		assertTrue(AccentUtils.isNumeric("0"));
		assertTrue(AccentUtils.isNumeric("-50"));
		assertTrue(AccentUtils.isNumeric("699"));
		assertTrue(AccentUtils.isNumeric("1.025"));
		assertTrue(AccentUtils.isNumeric("5.0"));
		
		// edge cases
		assertFalse(AccentUtils.isNumeric(null));
		assertFalse(AccentUtils.isNumeric(""));
		assertFalse(AccentUtils.isNumeric("."));
		assertFalse(AccentUtils.isNumeric(","));
		assertFalse(AccentUtils.isNumeric("55d"));
		assertFalse(AccentUtils.isNumeric("-5,4"));
	}
	
	@Test
	public void testExtractExposureFromString() {
		assertEquals(10,AccentUtils.extractExposureMs("sdsfsdf_10ms"), 0.0001);
		assertEquals(9,AccentUtils.extractExposureMs("9ms"), 0.0001);
		assertEquals(99,AccentUtils.extractExposureMs("158_45-99ms"), 0.0001);
		assertEquals(9.9,AccentUtils.extractExposureMs("158ms_4ms5-9.9ms"), 0.0001);
		assertEquals(6,AccentUtils.extractExposureMs("158ms_4ms5-9,6ms"), 0.0001);
		assertEquals(99,AccentUtils.extractExposureMs("158_45-99ms.tiff"), 0.0001);
		assertEquals(56,AccentUtils.extractExposureMs("15ms_dfjhms_51ms56ms.tiff"), 0.0001);
		

		// edge cases
		String n = null;
		assertEquals(0,AccentUtils.extractExposureMs(n), 0.0001);
		assertEquals(0,AccentUtils.extractExposureMs(""), 0.0001);
		assertEquals(0,AccentUtils.extractExposureMs("ms"), 0.0001);
		assertEquals(0,AccentUtils.extractExposureMs("fjkdsdhb65dfjbfe54"), 0.0001);
		assertEquals(0,AccentUtils.extractExposureMs("15564618"), 0.0001);
	}
	
}
