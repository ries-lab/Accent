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
		
		double[] gain =  { 30.1, 14.1, 25.1, 34.1 };
		c2.setGain(gain);
		assertFalse(Calibration.areEquals(c1, c2));
	}
	
	@Test
	public void testIO() {
		String dir = "/temp_test_calib/";
		File f_dir = new File(dir);
		if(!f_dir.exists()) {
			f_dir.mkdir();
		}
				
		File cal = new File(dir+"temp_calib"+CalibrationIO.CALIB_EXT);
			
		// write calibration
		Calibration c1 = generateCalibration();
		CalibrationIO.write(cal, c1);
		assertTrue(cal.exists());
		
		// read calibration
		Calibration c2 = CalibrationIO.read(cal);
		assertTrue(Calibration.areEquals(c1, c2));
		assertEquals(2,c2.getHeight());
		
		// delete temp
		assertTrue(cal.delete());
		assertTrue(f_dir.delete());
	}
	
	@Test 
	public void testWrongSizeException() {
		double[] d = { 1, 2, 3 };
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
		double[] baseline = { 0.1, 1.1, 2.1, 3.1 };
		double[] dc_per_sec = { 4.1, 5.1, 6.1, 7.1 };
		double[] r_sq_avg = { 8.1, 9.1, 10.1, 11.1 };
		double[] rn_sq = { 12.1, 13.1, 14.1, 15.1 };
		double[] tn_sq_per_sec = { 10.1, 11.1, 21.1, 31.1 };
		double[] r_sq_var = { 20.1, 12.1, 22.1, 32.1 };

		double[] gain = { 30.1, 14.1, 24.1, 34.1 };
		double[] r_sq_gain = { 50.1, 15.1, 25.1, 35.1 };

		return new Calibration(2, 2, baseline, dc_per_sec, r_sq_avg, 
				rn_sq, tn_sq_per_sec, r_sq_var, gain, r_sq_gain);
	}
}
