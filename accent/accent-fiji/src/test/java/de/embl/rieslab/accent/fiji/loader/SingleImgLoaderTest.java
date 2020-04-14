package de.embl.rieslab.accent.fiji.loader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;

import de.embl.rieslab.accent.fiji.data.image.StackImg;
import de.embl.rieslab.accent.fiji.datagen.GenerateData;
import de.embl.rieslab.accent.fiji.utils.AccentFijiUtils;
import net.imagej.ImageJ;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.integer.UnsignedIntType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.type.numeric.real.FloatType;

public class SingleImgLoaderTest {

	int width = 4;
	int height = 5;
	int numFrames = 10;
	double[] exps = {0.1, 2.0, 10.5};

	@Test
	public void testMultiStackUnsignedShortLoader() {
		final ImageJ ij = new ImageJ();
		String dir = "AccentTemp-mstacks-s";		
		File f_dir = new File(dir);
		if(!f_dir.exists()) {
			f_dir.mkdir();
		}
		
		// generates images
		GenerateData.generateAndWriteToDisk(dir, width, height, numFrames, exps, true, new UnsignedShortType());
		assertEquals(exps.length, AccentFijiUtils.getNumberTifs(dir));
		
		// extracts exposures
		Map<Double, String> m = new HashMap<Double, String>();
		m.put(2., f_dir.getPath());
		
		// creates loader
		SingleImgLoader loader = new SingleImgLoader(ij.scifio().datasetIO(), m);
		assertEquals(1, loader.getNumberOfChannels());
		
		assertTrue(loader.openChannel(0));
		assertEquals(exps.length, loader.getChannelLength());

		while(loader.hasNext(0)) {
			StackImg img = loader.getNext(0);
			assertEquals(numFrames, img.getImage().dimension(2));
		}

		// deletes all
		try {
			Files.list(Paths.get(dir)).forEach(e -> (new File(e.toString())).delete());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
			
		f_dir.delete();
	}

	@Test
	public void testUnsignedShortLoader() {
		final ImageJ ij = new ImageJ();
		String dir = "AccentTemp-single-s";		
		File f_dir = new File(dir);
		if(!f_dir.exists()) {
			f_dir.mkdir();
		}
		
		// generates images
		GenerateData.generateAndWriteToDisk(dir, width, height, numFrames, exps, false, new UnsignedShortType());
		assertEquals(exps.length, AccentFijiUtils.getNumberDirectoriesContainMs(dir));
		
		// extracts exposures
		Map<Double, String> m = AccentFijiUtils.getExposures(dir, false);
		
		// creates loader
		SingleImgLoader loader = new SingleImgLoader(ij.scifio().datasetIO(), m);
		assertEquals(exps.length, loader.getNumberOfChannels());
		
		// loads each file 
		for(int i=0; i<loader.getNumberOfChannels(); i++) {
			assertTrue(loader.openChannel(i));

			int nFrames = 0;
			while(loader.hasNext(i)) {
				StackImg img = loader.getNext(i);
				boolean foundExposure = false;
				for(Double e: exps) {
					if(Double.compare(e, img.getExposure()) == 0) {
						foundExposure = true;
					}
				}
				assertTrue(foundExposure);
	
				assertEquals(width, img.getImage().dimension(0));
				assertEquals(height, img.getImage().dimension(1));
				
				nFrames += img.getImage().dimension(2);
			}
			assertEquals(numFrames, nFrames);
		}
		
		// deletes all
		for(Entry<Double, String> el: m.entrySet()) {
			try {
				Files.list(Paths.get(el.getValue()))
					.forEach(e -> (new File(e.toString())).delete());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			File f = new File(el.getValue());
			if(f.exists())
				assertTrue(f.delete());
		}
		
		f_dir.delete();
	}

	@Test
	public void testUnsignedIntLoader() {
		final ImageJ ij = new ImageJ();
		String dir = "AccentTemp-single-i";		
		File f_dir = new File(dir);
		if(!f_dir.exists()) {
			f_dir.mkdir();
		}
		
		// generates images
		GenerateData.generateAndWriteToDisk(dir, width, height, numFrames, exps, false, new UnsignedIntType());
		assertEquals(exps.length, AccentFijiUtils.getNumberDirectoriesContainMs(dir));
		
		// extracts exposures
		Map<Double, String> m = AccentFijiUtils.getExposures(dir, false);
		
		// creates loader
		SingleImgLoader loader = new SingleImgLoader(ij.scifio().datasetIO(), m);
		assertEquals(exps.length, loader.getNumberOfChannels());
		
		// loads each file 
		for(int i=0; i<loader.getNumberOfChannels(); i++) {
			assertTrue(loader.openChannel(i));

			int nFrames = 0;
			while(loader.hasNext(i)) {
				StackImg img = loader.getNext(i);
				boolean foundExposure = false;
				for(Double e: exps) {
					if(Double.compare(e, img.getExposure()) == 0) {
						foundExposure = true;
					}
				}
				assertTrue(foundExposure);
	
				assertEquals(width, img.getImage().dimension(0));
				assertEquals(height, img.getImage().dimension(1));
				
				nFrames += img.getImage().dimension(2);
			}
			assertEquals(numFrames, nFrames);
		}
		
		// deletes all
		for(Entry<Double, String> el: m.entrySet()) {
			try {
				Files.list(Paths.get(el.getValue()))
					.forEach(e -> (new File(e.toString())).delete());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			File f = new File(el.getValue());
			if(f.exists())
				assertTrue(f.delete());
		}
		
		f_dir.delete();
	}

	@Test
	public void testFloatLoader() {
		final ImageJ ij = new ImageJ();
		String dir = "AccentTemp-single-f";		
		File f_dir = new File(dir);
		if(!f_dir.exists()) {
			f_dir.mkdir();
		}
		
		// generates images
		GenerateData.generateAndWriteToDisk(dir, width, height, numFrames, exps, false, new FloatType());
		assertEquals(exps.length, AccentFijiUtils.getNumberDirectoriesContainMs(dir));
		
		// extracts exposures
		Map<Double, String> m = AccentFijiUtils.getExposures(dir, false);
		
		// creates loader
		SingleImgLoader loader = new SingleImgLoader(ij.scifio().datasetIO(), m);
		assertEquals(exps.length, loader.getNumberOfChannels());
		
		// loads each file 
		for(int i=0; i<loader.getNumberOfChannels(); i++) {
			assertTrue(loader.openChannel(i));

			int nFrames = 0;
			while(loader.hasNext(i)) {
				StackImg img = loader.getNext(i);
				boolean foundExposure = false;
				for(Double e: exps) {
					if(Double.compare(e, img.getExposure()) == 0) {
						foundExposure = true;
					}
				}
				assertTrue(foundExposure);
	
				assertEquals(width, img.getImage().dimension(0));
				assertEquals(height, img.getImage().dimension(1));
				
				nFrames += img.getImage().dimension(2);
			}
			assertEquals(numFrames, nFrames);
		}
		
		// deletes all
		for(Entry<Double, String> el: m.entrySet()) {
			try {
				Files.list(Paths.get(el.getValue()))
					.forEach(e -> (new File(e.toString())).delete());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			File f = new File(el.getValue());
			if(f.exists())
				assertTrue(f.delete());
		}
		
		f_dir.delete();
	}
	
	@Test
	public void testUnsignedByteLoader() {
		final ImageJ ij = new ImageJ();
		String dir = "AccentTemp-single-b";		
		File f_dir = new File(dir);
		if(!f_dir.exists()) {
			f_dir.mkdir();
		}
		
		// generates images
		GenerateData.generateAndWriteToDisk(dir, width, height, numFrames, exps, false, new UnsignedByteType());
		assertEquals(exps.length, AccentFijiUtils.getNumberDirectoriesContainMs(dir));
		
		// extracts exposures
		Map<Double, String> m = AccentFijiUtils.getExposures(dir, false);
		
		// creates loader
		SingleImgLoader loader = new SingleImgLoader(ij.scifio().datasetIO(), m);
		assertEquals(exps.length, loader.getNumberOfChannels());
		
		// loads each file 
		for(int i=0; i<loader.getNumberOfChannels(); i++) {
			assertTrue(loader.openChannel(i));

			int nFrames = 0;
			int counter = 0;
			while(loader.hasNext(i)) {
				StackImg img = loader.getNext(i);
				boolean foundExposure = false;
				for(Double e: exps) {
					if(Double.compare(e, img.getExposure()) == 0) {
						foundExposure = true;
					}
				}
				assertTrue(foundExposure);
	
				// right x-y dimensions
				assertEquals(width, img.getImage().dimension(0));
				assertEquals(height, img.getImage().dimension(1));

				nFrames += img.getImage().dimension(2);
				counter++;
			}
			
			// it loaded the right number of frames
			assertEquals(numFrames, nFrames);
			
			// it loaded everything in the first iteration
			assertEquals(1, counter);
		}
		
		// deletes all
		for(Entry<Double, String> el: m.entrySet()) {
			try {
				Files.list(Paths.get(el.getValue()))
					.forEach(e -> (new File(e.toString())).delete());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			File f = new File(el.getValue());
			if(f.exists())
				assertTrue(f.delete());
		}
		
		f_dir.delete();
	}
}

