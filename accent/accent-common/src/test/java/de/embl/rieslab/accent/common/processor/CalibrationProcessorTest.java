package de.embl.rieslab.accent.common.processor;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.Test;

import de.embl.rieslab.accent.common.data.calibration.Calibration;
import de.embl.rieslab.accent.common.data.image.FloatImage;
import de.embl.rieslab.accent.common.dummys.DummyController;
import de.embl.rieslab.accent.common.dummys.DummyLoader;
import de.embl.rieslab.accent.common.dummys.DummyProcessor;

public class CalibrationProcessorTest {

	@Test
	public void testConstructor() {
		assertThrows(IllegalArgumentException.class, () -> {
			new DummyProcessor("", new DummyController(), new DummyLoader(2));
		});
		assertThrows(NullPointerException.class, () -> {
			new DummyProcessor(null, new DummyController(), new DummyLoader(2));
		});
		assertThrows(NullPointerException.class, () -> {
			new DummyProcessor("", null, new DummyLoader(2));
		});
		assertThrows(NullPointerException.class, () -> {
			new DummyProcessor("", new DummyController(), null);
		});
	}
	
	@Test
	public void testLinearRegressions() {
		DummyProcessor proc = new DummyProcessor("", new DummyController(), new DummyLoader(3));
				
		int Ne = 3;
		double[] exposure = new double[Ne];
		for(int i =0; i< Ne; i++)
			exposure[i] = 10+500*i;
			
		int width = 1;
		int height = 1;
		
		// ground truth
		double baseline = 3.5;
		double dc_per_sec = 1.8; 
		double r_sq_avg = 1; 
		
		double rn_sq = 2.4; 
		double tn_sq_per_sec = 1.8; 
		double r_sq_var = 1; 
		
		double gain = tn_sq_per_sec/dc_per_sec; 
		double r_sq_gain = 1;

		FloatImage[] avgs = new FloatImage[Ne];
		FloatImage[] vars = new FloatImage[Ne];
		for(int i =0; i< Ne; i++) {
			float[] f_avg = new float[width*height];
			float[] f_var = new float[width*height];
			for(int y = 0; y<height; y++) {
				for(int x = 0; x<width; x++) {
					int p = x+width*y;
					f_avg[p] = (float) (baseline+dc_per_sec*exposure[i]/1000.);
					f_var[p] = (float) (rn_sq+tn_sq_per_sec*exposure[i]/1000.);
				}
			}
			avgs[i] = new FloatImage(width, height, f_avg, exposure[i]);
			vars[i] = new FloatImage(width, height, f_var, exposure[i]);
		}
		
		// perform linear regressions
		Calibration cal = proc.performLinearRegressions(avgs, vars);
		

		for(int y = 0; y<height; y++) {
			for(int x = 0; x<width; x++) {
				int p = x+width*y;
				assertEquals(baseline, cal.getBaseline()[p], 0.0001);
				assertEquals(dc_per_sec, cal.getDcPerSec()[p], 0.0001);
				assertEquals(r_sq_avg, cal.getRSqAvg()[p], 0.0001);
				assertEquals(rn_sq, cal.getRnSq()[p], 0.0001);
				assertEquals(tn_sq_per_sec, cal.getTnSqPerSec()[p], 0.0001);
				assertEquals(r_sq_var, cal.getRSqVar()[p], 0.0001);
				assertEquals(gain, cal.getGain()[p], 0.0001);
				assertEquals(r_sq_gain, cal.getRSqGain()[p], 0.0001);
			}
		}
	}
}
