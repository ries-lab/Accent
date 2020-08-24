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
		float[] baseline = { 1.5f, 2.9f, 3.7f, 4.1f, 1.4f, 5.2f};
		float[] dc_per_sec = { 5.48f, 4.9f, 2.45f, 5.68f, 4.58f, 10.1f };
		
		float[] rn_sq = { 1.45f, 6.26f, 7.5f, 4.5f, 6.57f, 7.321f };
		float[] tn_sq_per_sec = { 6.35f, 7.896f, 100.5f ,5.78f, 4.2f, 86.9f };
		
		float[] r_sq_avg = { 0f, 0f ,0f ,0f ,0f ,0f };
		float[] r_sq_var = { 0f, 0f ,0f ,0f,0f ,0f  };
		float[] gain = { 0f, 0f ,0f ,0f,0f ,0f  };
		float[] r_sq_gain = { 0f, 0f ,0f ,0f,0f ,0f  };

		return new Calibration(1, 1, 2, 3, baseline, dc_per_sec, r_sq_avg, 
				rn_sq, tn_sq_per_sec, r_sq_var, gain, r_sq_gain);
	}
}
