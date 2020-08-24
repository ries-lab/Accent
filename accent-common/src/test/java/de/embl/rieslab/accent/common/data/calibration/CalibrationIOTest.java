package de.embl.rieslab.accent.common.data.calibration;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.junit.jupiter.api.Test;

public class CalibrationIOTest {

	
	@Test
	public void testEquals() {
		Calibration c1 = generateCalibration();
		Calibration c2 = generateCalibration();
		assertTrue(Calibration.areEquals(c1, c2));
		
		float[] gain =  { 30.1f, 14.1f, 25.1f, 34.1f };
		c2.setGain(gain);
		assertFalse(Calibration.areEquals(c1, c2));
	}
	
	@Test
	public void testIO() {
		File cal = new File("temp_calib."+CalibrationIO.CALIB_EXT);
			
		// write calibration
		Calibration c1 = generateCalibration();
		assertTrue(CalibrationIO.write(cal, c1));
		assertTrue(cal.exists());
		
		// read calibration
		Calibration c2 = CalibrationIO.read(cal);
		assertTrue(Calibration.areEquals(c1, c2));
		assertEquals(2,c2.getHeight());
		
		// delete temp
		assertTrue(cal.delete());
	}
	
	@Test 
	public void testWrongSizeException() {
		float[] d = { 1, 2, 3 };
		Calibration c = generateCalibration();
		
		assertThrows(IllegalArgumentException.class, () -> {
			c.setBaseline(d);
		});
		
		assertThrows(IllegalArgumentException.class, () -> {
			c.setDcPerSec(d);
		});
		
		assertThrows(IllegalArgumentException.class, () -> {
			c.setGain(d);
		});
		
		assertThrows(IllegalArgumentException.class, () -> {
			c.setRnSq(d);
		});
		
		assertThrows(IllegalArgumentException.class, () -> {
			c.setRSqAvg(d);
		});
		
		assertThrows(IllegalArgumentException.class, () -> {
			c.setRSqVar(d);
		});
		
		assertThrows(IllegalArgumentException.class, () -> {
			c.setRSqGain(d);
		});
		
		assertThrows(IllegalArgumentException.class, () -> {
			c.setTnSqPerSec(d);
		});
		
	}
	
	public static Calibration generateCalibration() {
		// Creates calibration object
		float[] baseline = { 0.1f, 1.1f, 2.1f, 3.1f };
		float[] dc_per_sec = { 4.1f, 5.1f, 6.1f, 7.1f };
		float[] r_sq_avg = { 8.1f, 9.1f, 10.1f, 11.1f };
		float[] rn_sq = { 12.1f, 13.1f, 14.1f, 15.1f };
		float[] tn_sq_per_sec = { 10.1f, 11.1f, 21.1f, 31.1f };
		float[] r_sq_var = { 20.1f, 12.1f, 22.1f, 32.1f };

		float[] gain = { 30.1f, 14.1f, 24.1f, 34.1f };
		float[] r_sq_gain = { 50.1f, 15.1f, 25.1f, 35.1f };

		return new Calibration(1, 1, 2, 2, baseline, dc_per_sec, r_sq_avg, 
				rn_sq, tn_sq_per_sec, r_sq_var, gain, r_sq_gain);
	}
}
