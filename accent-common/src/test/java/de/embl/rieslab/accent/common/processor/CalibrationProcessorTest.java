package de.embl.rieslab.accent.common.processor;

import java.io.File;

import de.embl.rieslab.accent.common.data.calibration.Calibration;
import de.embl.rieslab.accent.common.data.calibration.CalibrationIO;
import de.embl.rieslab.accent.common.data.roi.SimpleRoi;
import de.embl.rieslab.accent.common.dummys.DummyController;
import de.embl.rieslab.accent.common.dummys.DummyImage;
import de.embl.rieslab.accent.common.dummys.DummyLoader;
import de.embl.rieslab.accent.common.dummys.DummyProcessor;
import org.junit.Test;

import static org.junit.Assert.*;

public class CalibrationProcessorTest {

	@Test(expected = IllegalArgumentException.class)
	public void testEmptyFolderConstructor() {
		new DummyProcessor("", new SimpleRoi(0, 0, 1, 1), new DummyController(), new DummyLoader(1));
	}

	@Test(expected = NullPointerException.class)
	public void testNullConstructor() {
		new DummyProcessor(null, new SimpleRoi(0, 0, 1, 1), new DummyController(), new DummyLoader(2));

	}

	@Test(expected = NullPointerException.class)
	public void testNulllControllerConstructor() {
		new DummyProcessor("", new SimpleRoi(0, 0, 1, 1), null, new DummyLoader(2));

	}

	@Test(expected = NullPointerException.class)
	public void testNulLoaderConstructor() {
		new DummyProcessor("", new SimpleRoi(0, 0, 1, 1), new DummyController(), null);

	}
	
	@Test
	public void testLinearRegressions() {
		int width = 1;
		int height = 1;
		DummyProcessor proc = new DummyProcessor("", new SimpleRoi(0,0,width,height), new DummyController(), new DummyLoader(3));
				
		int Ne = 3;
		double[] exposure = new double[Ne];
		for(int i =0; i< Ne; i++)
			exposure[i] = 10+500*i;
			
		// ground truth
		double baseline = 3.5;
		double dc_per_sec = 1.8; 
		double r_sq_avg = 1; 
		
		double rn_sq = 2.4; 
		double tn_sq_per_sec = 1.8; 
		double r_sq_var = 1; 
		
		double gain = tn_sq_per_sec/dc_per_sec; 
		double r_sq_gain = 1;

		DummyImage[] avgs = new DummyImage[Ne];
		DummyImage[] vars = new DummyImage[Ne];
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
			avgs[i] = new DummyImage(4, f_avg, width, height, exposure[i]);
			vars[i] = new DummyImage(4, f_var, width, height, exposure[i]);
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
	
	@Test
	public void testProcessorPipeline() {
		String dir = "temp_proc";
		File f_dir = new File(dir);
		if(!f_dir.exists()) {
			f_dir.mkdir();
		}

		DummyLoader load =  new DummyLoader(3);
		DummyController cont =  new DummyController();
		DummyProcessor proc = new DummyProcessor(f_dir.getAbsolutePath(), new SimpleRoi(0, 0, load.width, load.height),
				cont, load);
		
		assertFalse(proc.isRunning());
		proc.startProcess();
		
		while(proc.isAlive()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		// checks saved images
		// delete files
		for(int i=0;i<load.exposures.length;i++) {
			File s_var, s_avg;
			if(i%2==0) {
				s_avg = new File(dir+File.separator+"Avg_"+((int)load.exposures[i])+"ms.tiff");
				s_var = new File(dir+File.separator+"Var_"+((int)load.exposures[i])+"ms.tiff");
			} else {
				s_avg = new File(dir+File.separator+"Avg_"+load.exposures[i]+"ms.tiff");
				s_var = new File(dir+File.separator+"Var_"+load.exposures[i]+"ms.tiff");
			}

			assertTrue(s_avg.exists());
			assertTrue(s_avg.delete());
			
			assertTrue(s_var.exists());
			assertTrue(s_var.delete());
		}
		
		// checks saved calibration
		File calib = new File(dir+File.separator+"results."+CalibrationIO.CALIB_EXT);
		assertTrue(calib.exists());
		assertTrue(calib.delete());
		
		// checks calibration images
		File f = new File(dir+File.separator+"Baseline.tiff");
		assertTrue(f.exists());
		assertTrue(f.delete());
		f = new File(dir+File.separator+"DC_per_sec.tiff");
		assertTrue(f.exists());
		assertTrue(f.delete());
		f = new File(dir+File.separator+"Gain.tiff");
		assertTrue(f.exists());
		assertTrue(f.delete());
		f = new File(dir+File.separator+"RN_sq.tiff");
		assertTrue(f.exists());
		assertTrue(f.delete());
		f = new File(dir+File.separator+"TN_sq_per_sec.tiff");
		assertTrue(f.exists());
		assertTrue(f.delete());
		f = new File(dir+File.separator+"R_sq_avg.tiff");
		assertTrue(f.exists());
		assertTrue(f.delete());
		f = new File(dir+File.separator+"R_sq_var.tiff");
		assertTrue(f.exists());
		assertTrue(f.delete());
		f = new File(dir+File.separator+"R_sq_gain.tiff");
		assertTrue(f.exists());
		assertTrue(f.delete());
		
		// checks updates
		assertEquals("Done.",cont.proc_progress.get(cont.proc_progress.size()-1));
		
		// deletes all files
		assertTrue(f_dir.delete());
		
	}
}
