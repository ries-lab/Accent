package de.embl.rieslab.accent.fiji.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Map;

import org.junit.Test;

import de.embl.rieslab.accent.fiji.datagen.GenerateData;
import net.imglib2.type.numeric.integer.UnsignedShortType;

public class AccentFijiUtilsTest {

	// TODO test the "Avg" and "Var" filtering
	@Test
	public void test() {
		String dir = "AccentTemp-u";		
		File f_dir = new File(dir);
		if(!f_dir.exists()) {
			f_dir.mkdir();
		}

		// images
		int width = 10;
		int height = 20;
		int numFrames = 100;
		double[] exps = {0.1, 1, 10};
	
		GenerateData.generateAndWriteToDisk(dir, width, height, numFrames, exps, true, new UnsignedShortType());
		int nIm = AccentFijiUtils.getNumberTifsContainMs(dir);
		assertEquals(exps.length, nIm);
		assertEquals(exps.length, AccentFijiUtils.getNumberTifs(dir));
		
		// folders
		File[] fs = new File[exps.length+2];
		for(int i=0;i<exps.length+2;i++) {
			String s;
			if(i<2) {
				s = f_dir.getAbsolutePath()+"\\temp"+i+"\\";
			} else {
				s = f_dir.getAbsolutePath()+"\\temp"+exps[i-2]+"ms_hj2i86u\\";
			}
			
			fs[i] = new File(s);
			if(!fs[i].exists()) {
				fs[i].mkdir();
			}

			assertTrue(fs[i].isDirectory());
		}
		assertEquals(exps.length+2, AccentFijiUtils.getNumberDirectories(dir));
		int nF = AccentFijiUtils.getNumberDirectoriesContainMs(dir);
		assertEquals(exps.length, nF);
		
		// count them all
		Map<Double, String> mIm = AccentFijiUtils.getExposures(dir, true);
		Map<Double, String> mF = AccentFijiUtils.getExposures(dir, false);
		assertEquals(exps.length, mF.size());
		assertEquals(exps.length, mIm.size());

		for(double e: exps) {
			assertTrue(mIm.containsKey(e));
			assertTrue(mF.containsKey(e));
			
			File f = new File(mIm.get(e));
			if(f.exists())
				assertTrue(f.delete());
		}
				
		// deletes all		
		for(File f: fs)
			assertTrue(f.delete());
		
		assertTrue(f_dir.delete());
	}
}
