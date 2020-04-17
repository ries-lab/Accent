package de.embl.rieslab.accent.common.generator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;

import de.embl.rieslab.accent.common.data.calibration.Calibration;
import de.embl.rieslab.accent.common.data.calibration.CalibrationIOTest;
import de.embl.rieslab.accent.common.dummys.DummyController;
import de.embl.rieslab.accent.common.dummys.DummyImage;

public class AvgVarMapsGeneratorTest {

	@Test
	public void testGeneration() {
		DummyController cont = new DummyController();
		Calibration cal = CalibrationIOTest.generateCalibration();
		AvgVarMapsGenerator<DummyImage, DummyImage> gen = new AvgVarMapsGenerator<DummyImage, DummyImage>(cont);

		double[] expo = new double[10];
		for(int i=0;i<expo.length;i++)
			expo[i] = i*10.5;
		
		// creates temp folder
		String dir = "temp_test";
		File f_dir = new File(dir);
		if(!f_dir.exists()) {
			f_dir.mkdir();
		}

		assertFalse(gen.isRunning());
		gen.generate(f_dir.getAbsolutePath(), cal, expo);
		assertTrue(gen.isRunning());
		
		while(gen.isRunning()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		for(int i=0;i<cont.gen_progress.size()-1;i++) {
			String s = "Exposure: "+i+"/"+expo.length;
			assertEquals(s, cont.gen_progress.get(i));
		}
		assertEquals("Done.", cont.gen_progress.get(cont.gen_progress.size()-1));
		
		// delete files
		for(int i=0;i<expo.length;i++) {
			File s_var, s_avg;
			if(i%2==0) {
				s_avg = new File(dir+"\\generated_Avg_"+((int)expo[i])+"ms.tiff");
				s_var = new File(dir+"\\generated_Var_"+((int)expo[i])+"ms.tiff");
			} else {
				s_avg = new File(dir+"\\generated_Avg_"+expo[i]+"ms.tiff");
				s_var = new File(dir+"\\generated_Var_"+expo[i]+"ms.tiff");
			}
			
			assertTrue(s_avg.exists());
			assertTrue(s_avg.delete());
			
			assertTrue(s_var.exists());
			assertTrue(s_var.delete());
		}
		assertTrue(f_dir.delete());
	}
	
	@Test
	public void testIllegalArgumentsGenerations() {
		DummyController cont = new DummyController();
		Calibration cal = CalibrationIOTest.generateCalibration();
		AvgVarMapsGenerator<DummyImage, DummyImage> gen = new AvgVarMapsGenerator<DummyImage, DummyImage>(cont);

		double[] expo = new double[0];
		
		// creates temp folder
		String dir = "temp_test";
		File f_dir = new File(dir);
		if(!f_dir.exists()) {
			f_dir.mkdir();
		}

		assertThrows(IllegalArgumentException.class, () -> {
			gen.generate(f_dir.getAbsolutePath(), cal, expo);
		});
		
		assertTrue(f_dir.delete());
	}

	@Test
	public void testNullGenerations() {
		DummyController cont = new DummyController();
		Calibration cal = CalibrationIOTest.generateCalibration();
		AvgVarMapsGenerator<DummyImage, DummyImage> gen = new AvgVarMapsGenerator<DummyImage, DummyImage>(cont);

		double[] expo = new double[10];
		for(int i=0;i<expo.length;i++)
			expo[i] = i*10;
		
		// creates temp folder
		String dir = "temp_test";
		File f_dir = new File(dir);
		if(!f_dir.exists()) {
			f_dir.mkdir();
		}
				
		// tests null generation
		assertThrows(NullPointerException.class, () -> {
			gen.generate(null, cal, expo);
		});
		assertThrows(NullPointerException.class, () -> {
			gen.generate(f_dir.getAbsolutePath(), null, expo);
		});
		assertThrows(NullPointerException.class, () -> {
			gen.generate(f_dir.getAbsolutePath(), cal, null);
		});
		assertTrue(f_dir.delete());
	}

	@Test
	public void testPathsGeneration() {

	}
	
	@Test
	public void testNullConstructor() {
		assertThrows(NullPointerException.class, () -> {
			new AvgVarMapsGenerator<DummyImage, DummyImage>(null);
		});
	}
}
