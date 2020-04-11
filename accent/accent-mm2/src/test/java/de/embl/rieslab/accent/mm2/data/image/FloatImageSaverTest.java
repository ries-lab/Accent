package de.embl.rieslab.accent.mm2.data.image;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

public class FloatImageSaverTest {ss
	//TODO read the tif and see if the pixels are the same
	@Test
	public void testFlotImageSaver() {
		String dir = "AccentTemp";		
		File f_dir = new File(dir);
		if(!f_dir.exists()) {
			f_dir.mkdir();
		}
		
		// creates float
		int width = 100;
		int height = 200;
		double exposure = 12.5;
		float[] pixs = new float[width*height];
		for(int i=0;i<width*height;i++) {
			pixs[i] = (float) (i*10.42);
		}
				
		// creates FloatImage
		FloatImage f = new FloatImage(width, height, pixs, exposure);

		// attempts saving
		FloatImageSaver ims = new FloatImageSaver();
		
		String s = dir+"\\file1";
		ims.saveAsTiff(f, s);
		
		File file1 = new File(s+".tif"); 
		assertTrue(file1.exists());
		assertTrue(file1.delete());

		s = dir+"\\file2.tif";
		ims.saveAsTiff(f, s);
		
		file1 = new File(s); 
		assertTrue(file1.exists());
		assertTrue(file1.delete());
		
		// attempts to delete folder
		f_dir.delete();
	}
	
}
