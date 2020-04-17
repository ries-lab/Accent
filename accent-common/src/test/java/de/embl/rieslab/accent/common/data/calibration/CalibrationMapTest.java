package de.embl.rieslab.accent.common.data.calibration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class CalibrationMapTest {

	@Test
	public void testGenerateCalibrationMap() {
		Calibration cal = generateCalibration();
		
		for(int i=0;i<1;i++) {
			double exp = 5+i*10.5; 

			float[] avg = CalibrationMap.generateAvgMap(cal, exp);
			for(int p=0; p< cal.getHeight()*cal.getWidth(); p++) {
				float pix = (float) (cal.getBaseline()[p]+cal.getDcPerSec()[p]*exp/1000.0);
				assertEquals(pix, avg[p], 0.0001);
			}
			
			float[] var = CalibrationMap.generateVarMap(cal, exp);
			for(int p=0; p< cal.getHeight()*cal.getWidth(); p++) {
				float pix = (float) (cal.getRnSq()[p]+cal.getTnSqPerSec()[p]*exp/1000.0);
				assertEquals(pix, var[p], 0.0001);
			}
		}
		
	}

	private static Calibration generateCalibration() {
		// Creates calibration object
		double[] baseline = { 1.5, 2.9, 3.7, 4.1, 1.4, 5.2};
		double[] dc_per_sec = { 5.48, 4.9, 2.45, 5.68, 4.58, 10.1 };
		
		double[] rn_sq = { 1.45, 6.26, 7.5, 4.5, 6.57, 7.321 };
		double[] tn_sq_per_sec = { 6.35, 7.896, 100.5 ,5.78, 4.2, 86.9 };
		
		double[] r_sq_avg = { 0, 0 ,0 ,0 ,0 ,0 };
		double[] r_sq_var = { 0, 0 ,0 ,0,0 ,0  };
		double[] gain = { 0, 0 ,0 ,0,0 ,0  };
		double[] r_sq_gain = { 0, 0 ,0 ,0,0 ,0  };

		return new Calibration(2, 3, baseline, dc_per_sec, r_sq_avg, 
				rn_sq, tn_sq_per_sec, r_sq_var, gain, r_sq_gain);
	}
}
