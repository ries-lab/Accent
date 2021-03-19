package de.embl.rieslab.accent.fiji.loader;

import java.io.File;
import java.util.Map;
import java.util.Map.Entry;

import de.embl.rieslab.accent.fiji.data.image.StackImg;
import de.embl.rieslab.accent.fiji.datagen.GenerateData;
import de.embl.rieslab.accent.fiji.utils.AccentFijiUtils;
import io.scif.SCIFIO;
import io.scif.services.DatasetIOService;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.integer.UnsignedIntType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.type.numeric.real.FloatType;
import org.junit.Before;
import org.junit.Test;
import org.scijava.log.LogService;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class StackLoaderTest {

	int width = 10;
	int height = 20;
	int numFrames = 100;
	double[] exps = {0.1, 2.0};

	private DatasetIOService ioService;
	private LogService logService;

	@Before
	public void init(){
		SCIFIO scifio = new SCIFIO();
		ioService = scifio.datasetIO();
		logService = scifio.log();
	}

	@Test
	public void testUnsignedShortLoader() {
		String dir = "AccentTemp-stack-s";
		File f_dir = new File(dir);
		if(!f_dir.exists()) {
			f_dir.mkdir();
		}
		
		// generates images
		GenerateData.generateAndWriteToDisk(dir, width, height, numFrames, exps, true, new UnsignedShortType());
		assertEquals(exps.length, AccentFijiUtils.getNumberTifsContainMs(dir));
		
		// extracts exposures
		Map<Double, String> m = AccentFijiUtils.getExposures(dir, true);
		m.remove(0.); // removes unknown exposures
		
		// creates loader
		StackLoader loader = new StackLoader(ioService, logService, m);
		assertEquals(exps.length, loader.getNumberOfChannels());
		
		// loads each file 
		for(int i=0; i<loader.getNumberOfChannels(); i++) {
			assertTrue(loader.openChannel(i));
			assertTrue(loader.hasNext(i));
			
			StackImg img = loader.getNext(i);
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
		
		// deletes all
		for(Entry<Double, String> e: m.entrySet()) {
			String path = e.getValue();

			File f = new File(path);
			if(f.exists())
				assertTrue(f.delete());
		}
		
		f_dir.delete();
	}

	@Test
	public void testUnsignedByteLoader() {
		String dir = "AccentTemp-stack-b";		
		File f_dir = new File(dir);
		if(!f_dir.exists()) {
			f_dir.mkdir();
		}
		
		// generates images
		GenerateData.generateAndWriteToDisk(dir, width, height, numFrames, exps, true, new UnsignedByteType());
		assertEquals(exps.length, AccentFijiUtils.getNumberTifsContainMs(dir));
		
		// extracts exposures
		Map<Double, String> m = AccentFijiUtils.getExposures(dir,true);
		m.remove(0.); // removes unknown exposures
		
		// creates loader
		StackLoader loader = new StackLoader(ioService, logService, m);
		assertEquals(exps.length, loader.getNumberOfChannels());
		
		// loads each file 
		for(int i=0; i<loader.getNumberOfChannels(); i++) {
			assertTrue(loader.openChannel(i));
			assertTrue(loader.hasNext(i));
			
			StackImg img = loader.getNext(i);
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
		
		// deletes all
		for(Entry<Double, String> e: m.entrySet()) {
			String path = e.getValue();

			File f = new File(path);
			if(f.exists())
				assertTrue(f.delete());
		}
		
		f_dir.delete();
	}


	@Test
	public void testUnsignedIntLoader() {
		String dir = "AccentTemp-stack-i";
		File f_dir = new File(dir);
		if(!f_dir.exists()) {
			f_dir.mkdir();
		}
		
		// generates images
		GenerateData.generateAndWriteToDisk(dir, width, height, numFrames, exps, true, new UnsignedIntType());
		assertEquals(exps.length, AccentFijiUtils.getNumberTifsContainMs(dir));
		
		// extracts exposures
		Map<Double, String> m = AccentFijiUtils.getExposures(dir,true);
		m.remove(0.); // removes unknown exposures
		
		// creates loader
		StackLoader loader = new StackLoader(ioService, logService, m);
		assertEquals(exps.length, loader.getNumberOfChannels());
		
		// loads each file 
		for(int i=0; i<loader.getNumberOfChannels(); i++) {
			assertTrue(loader.openChannel(i));
			assertTrue(loader.hasNext(i));
			
			StackImg img = loader.getNext(i);
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
		
		// deletes all
		for(Entry<Double, String> e: m.entrySet()) {
			String path = e.getValue();

			File f = new File(path);
			if(f.exists())
				assertTrue(f.delete());
		}
		
		f_dir.delete();
	}

	@Test
	public void testFloatLoader() {
		String dir = "AccentTemp-stack-f";		
		File f_dir = new File(dir);
		if(!f_dir.exists()) {
			f_dir.mkdir();
		}
		
		// generates images
		GenerateData.generateAndWriteToDisk(dir, width, height, numFrames, exps, true, new FloatType());
		assertEquals(exps.length, AccentFijiUtils.getNumberTifsContainMs(dir));
		
		// extracts exposures
		Map<Double, String> m = AccentFijiUtils.getExposures(dir,true);
		m.remove(0.); // removes unknown exposures
		
		// creates loader
		StackLoader loader = new StackLoader(ioService, logService, m);
		assertEquals(exps.length, loader.getNumberOfChannels());
		
		// loads each file 
		for(int i=0; i<loader.getNumberOfChannels(); i++) {
			assertTrue(loader.openChannel(i));
			assertTrue(loader.hasNext(i));
			
			StackImg img = loader.getNext(i);
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
		
		// deletes all
		for(Entry<Double, String> e: m.entrySet()) {
			String path = e.getValue();

			File f = new File(path);
			if(f.exists())
				assertTrue(f.delete());
		}
		
		f_dir.delete();
	}
}
