package de.embl.rieslab.accent.fiji.loader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;

import de.embl.rieslab.accent.fiji.data.image.ImgCalibrationImage;
import de.embl.rieslab.accent.fiji.datagen.GenerateData;
import de.embl.rieslab.accent.fiji.utils.AccentFijiUtils;
import net.imagej.Dataset;
import net.imagej.ImageJ;
import net.imglib2.type.numeric.integer.UnsignedShortType;

public class LoadSingleImagesTest {

	int width = 10;
	int height = 20;
	int numFrames = 100;
	double[] exps = {0.1};

	@Test
	public void testUnsignedShortLoader() {
		final ImageJ ij = new ImageJ();
		String dir = "AccentTemp-s";		
		File f_dir = new File(dir);
		if(!f_dir.exists()) {
			f_dir.mkdir();
		}
		
		// generates images
	//	GenerateData.generateAndWriteToDisk(dir, width, height, numFrames, exps, false, new UnsignedShortType());
	//	assertEquals(exps.length, AccentFijiUtils.getNumberDirectoriesContainMs(dir));
		
		// extracts exposures
		Map<Double, String> m = AccentFijiUtils.getExposures(dir);
		m.remove(0.); // removes unknown exposures
	
		String s = dir+"\\0.1ms_unshort";
		
		try {
			Dataset datasets = ij.scifio().datasetIO().open(s);
			System.out.println();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	//		assertEquals(exps.length, loader.getNumberOfChannels());
		
		// loads each file 
	/*	for(int i=0; i<loader.getNumberOfChannels(); i++) {
			assertTrue(loader.openChannel(i));
			assertTrue(loader.hasNext(i));
			
			ImgCalibrationImage img = loader.getNext(i);
			boolean foundExposure = false;
			for(Double e: exps) {
				if(Double.compare(e, img.getExposure()) == 0) {
					foundExposure = true;
				}
			}
			assertTrue(foundExposure);

			assertEquals(3, img.getImage().numDimensions());
			assertEquals(width, img.getImage().dimension(0));
			assertEquals(height, img.getImage().dimension(1));
			assertEquals(numFrames, img.getImage().dimension(2));
		}
		*/
		// deletes all
	/*	for(Entry<Double, String> e: m.entrySet()) {
			String path = e.getValue();

			File f = new File(path);
			if(f.exists())
				assertTrue(f.delete());
		}
		
		f_dir.delete();*/
	}
}
