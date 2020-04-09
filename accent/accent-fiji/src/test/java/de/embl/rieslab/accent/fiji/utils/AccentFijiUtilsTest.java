package de.embl.rieslab.accent.fiji.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Map;

import org.junit.Test;

import de.embl.rieslab.accent.fiji.datagen.GenerateData;
import net.imglib2.type.numeric.integer.UnsignedShortType;

public class AccentFijiUtilsTest {

	@Test
	public void test() {
		String dir = "AccentTemp";		
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

		assertEquals(exps.length, AccentFijiUtils.getNumberTifsContainMs(dir));
		assertEquals(exps.length, AccentFijiUtils.getNumberTifs(dir));
		
		Map<Double, String> m = AccentFijiUtils.getExposures(dir);
		for(double e: exps) {
			assertTrue(m.containsKey(e));
			
			File f = new File(m.get(e));
			if(f.exists())
				assertTrue(f.delete());
		}
		
		// folders
		int k = 7;
		File[] fs = new File[k];
		for(int i=0;i<k;i++) {
			String s;
			if(i<2) {
				s = f_dir.getAbsolutePath()+"\\temp"+i+"\\";
			} else {
				s = f_dir.getAbsolutePath()+"\\temp"+i+"ms_hj2i86u\\";
			}
			
			fs[i] = new File(s);
			if(!fs[i].exists()) {
				fs[i].mkdir();
			}

			assertTrue(fs[i].isDirectory());
		}
		assertEquals(k, AccentFijiUtils.getNumberDirectories(dir));
		assertEquals(k-2, AccentFijiUtils.getNumberDirectoriesContainMs(dir));
		
		// deletes all		
		for(File f: fs)
			assertTrue(f.delete());
		
		assertTrue(f_dir.delete());
	}
}
