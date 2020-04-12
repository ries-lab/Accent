package de.embl.rieslab.accent.fiji.processor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.embl.rieslab.accent.common.data.image.AvgVarStacks;
import de.embl.rieslab.accent.fiji.data.image.PlaneImg;
import de.embl.rieslab.accent.fiji.datagen.GenerateData;
import de.embl.rieslab.accent.fiji.dummys.DummyController;
import de.embl.rieslab.accent.fiji.dummys.DummyStackLoader;
import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.integer.UnsignedIntType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.type.numeric.real.FloatType;

public class ImgProcessorTest {
	
	int width = 11;
	int height = 21;
	int numFrames = 15000;
	double[] exps = {0.1, 2.0, 3.5};
	
	@Test
	public void testProcUnsignedByteStackLoader() {
		String dir = "AccentTemp-proc-s";	
		
		// creates loader
		DummyStackLoader loader = new DummyStackLoader(width, height, numFrames, exps, new UnsignedByteType());
		assertEquals(exps.length, loader.getNumberOfChannels());
		
		// creates processor
		DummyController cont = new DummyController();
		ImgProcessor proc = new ImgProcessor(dir, cont, loader);
		
		// performs avg and var calculation
		AvgVarStacks<PlaneImg> avgs_vars = proc.computeAvgAndVar();
		
		// checks results
		for(int q=0;q<avgs_vars.getAvgs().length;q++) {
			int k = 0;
			boolean b = false;
			for(int j=0;j<exps.length;j++) {
				if(Double.compare(exps[j], avgs_vars.getAvgs()[q].getExposure()) == 0) {
					k = j;
					b = true;
				}
			}
			assertTrue(b);
			assertEquals(exps[k], avgs_vars.getAvgs()[q].getExposure(), 0.0001);

			// gets average and variance maps at the current exposure
			Img<FloatType> avg = avgs_vars.getAvgs()[q].getImage();
			Img<FloatType> var = avgs_vars.getVars()[q].getImage();

			// dimensions
			assertEquals(width, avg.dimension(0));
			assertEquals(width, var.dimension(0));
			assertEquals(height, avg.dimension(1));
			assertEquals(height, var.dimension(1));
			assertEquals(1, avg.dimension(2));
			assertEquals(1, var.dimension(2));
			
			// checks values
			float lowpix_avg = (float) GenerateData.getLowPixAverage(exps[k]);
			float dimpix_avg = (float) GenerateData.getDimPixAverage(exps[k]);
			float lowpix_var = (float) GenerateData.getLowPixVariance(exps[k]);
			float dimpix_var = (float) GenerateData.getDimPixVariance(exps[k]);
			
			Cursor<FloatType> cursor = avg.localizingCursor();
			RandomAccess<FloatType> r_var = var.randomAccess();
			while(cursor.hasNext()) {
				FloatType t = cursor.next();
				r_var.setPosition(cursor);
				FloatType u = r_var.get();

				// checks against expected values (with a certain tolerance)
				if(cursor.getIntPosition(0) % 10 == 0 && cursor.getIntPosition(1) % 20 == 0) {
					assertEquals(lowpix_avg, t.get(), 0.01*lowpix_avg);
					assertEquals(lowpix_var, u.get(), 0.06*lowpix_var);
				} else {
					assertEquals(dimpix_avg, t.get(), 0.01*dimpix_avg);
					assertEquals(dimpix_var, u.get(), 0.06*dimpix_var);
				}
			}
		}
	}
	
	@Test
	public void testProcUnsignedShortStackLoader() {
		String dir = "AccentTemp-proc-s";	
		
		// creates loader
		DummyStackLoader loader = new DummyStackLoader(width, height, numFrames, exps, new UnsignedShortType());
		assertEquals(exps.length, loader.getNumberOfChannels());
		
		// creates processor
		DummyController cont = new DummyController();
		ImgProcessor proc = new ImgProcessor(dir, cont, loader);
		
		// performs avg and var calculation
		AvgVarStacks<PlaneImg> avgs_vars = proc.computeAvgAndVar();
		
		// checks results
		for(int q=0;q<avgs_vars.getAvgs().length;q++) {
			int k = 0;
			boolean b = false;
			for(int j=0;j<exps.length;j++) {
				if(Double.compare(exps[j], avgs_vars.getAvgs()[q].getExposure()) == 0) {
					k = j;
					b = true;
				}
			}
			assertTrue(b);
			assertEquals(exps[k], avgs_vars.getAvgs()[q].getExposure(), 0.0001);

			// gets average and variance maps at the current exposure
			Img<FloatType> avg = avgs_vars.getAvgs()[q].getImage();
			Img<FloatType> var = avgs_vars.getVars()[q].getImage();

			// dimensions
			assertEquals(width, avg.dimension(0));
			assertEquals(width, var.dimension(0));
			assertEquals(height, avg.dimension(1));
			assertEquals(height, var.dimension(1));
			assertEquals(1, avg.dimension(2));
			assertEquals(1, var.dimension(2));
			
			// checks values
			float lowpix_avg = (float) GenerateData.getLowPixAverage(exps[k]);
			float hotpix_avg = (float) GenerateData.getHotPixAverage(exps[k]);
			float lowpix_var = (float) GenerateData.getLowPixVariance(exps[k]);
			float hotpix_var = (float) GenerateData.getHotPixVariance(exps[k]);
			
			Cursor<FloatType> cursor = avg.localizingCursor();
			RandomAccess<FloatType> r_var = var.randomAccess();
			while(cursor.hasNext()) {
				FloatType t = cursor.next();
				r_var.setPosition(cursor);
				FloatType u = r_var.get();

				// checks against expected values (with a certain tolerance)
				if(cursor.getIntPosition(0) % 10 == 0 && cursor.getIntPosition(1) % 20 == 0) {
					assertEquals(hotpix_avg, t.get(), 0.01*hotpix_avg);
					assertEquals(hotpix_var, u.get(), 0.06*hotpix_var); 
				} else {
					assertEquals(lowpix_avg, t.get(), 0.01*lowpix_avg);
					assertEquals(lowpix_var, u.get(), 0.06*lowpix_var);
				}
			}
		}
	}
	
	@Test
	public void testProcFloatStackLoader() {
		String dir = "AccentTemp-proc-s";	
		
		// creates loader
		DummyStackLoader loader = new DummyStackLoader(width, height, numFrames, exps, new FloatType());
		assertEquals(exps.length, loader.getNumberOfChannels());
		
		// creates processor
		DummyController cont = new DummyController();
		ImgProcessor proc = new ImgProcessor(dir, cont, loader);
		
		// performs avg and var calculation
		AvgVarStacks<PlaneImg> avgs_vars = proc.computeAvgAndVar();
		
		// checks results
		for(int q=0;q<avgs_vars.getAvgs().length;q++) {
			int k = 0;
			boolean b = false;
			for(int j=0;j<exps.length;j++) {
				if(Double.compare(exps[j], avgs_vars.getAvgs()[q].getExposure()) == 0) {
					k = j;
					b = true;
				}
			}
			assertTrue(b);
			assertEquals(exps[k], avgs_vars.getAvgs()[q].getExposure(), 0.0001);

			// gets average and variance maps at the current exposure
			Img<FloatType> avg = avgs_vars.getAvgs()[q].getImage();
			Img<FloatType> var = avgs_vars.getVars()[q].getImage();

			// dimensions
			assertEquals(width, avg.dimension(0));
			assertEquals(width, var.dimension(0));
			assertEquals(height, avg.dimension(1));
			assertEquals(height, var.dimension(1));
			assertEquals(1, avg.dimension(2));
			assertEquals(1, var.dimension(2));
			
			// checks values
			float lowpix_avg = (float) GenerateData.getLowPixAverage(exps[k]);
			float hotpix_avg = (float) GenerateData.getHotPixAverage(exps[k]);
			float lowpix_var = (float) GenerateData.getLowPixVariance(exps[k]);
			float hotpix_var = (float) GenerateData.getHotPixVariance(exps[k]);
			
			Cursor<FloatType> cursor = avg.localizingCursor();
			RandomAccess<FloatType> r_var = var.randomAccess();
			while(cursor.hasNext()) {
				FloatType t = cursor.next();
				r_var.setPosition(cursor);
				FloatType u = r_var.get();

				// checks against expected values (with a certain tolerance)
				if(cursor.getIntPosition(0) % 10 == 0 && cursor.getIntPosition(1) % 20 == 0) {
					assertEquals(hotpix_avg, t.get(), 0.01*hotpix_avg);
					assertEquals(hotpix_var, u.get(), 0.06*hotpix_var); 
				} else {
					assertEquals(lowpix_avg, t.get(), 0.01*lowpix_avg);
					assertEquals(lowpix_var, u.get(), 0.06*lowpix_var);
				}
			}
		}
	}
	
	@Test
	public void testProcIntStackLoader() {
		String dir = "AccentTemp-proc-s";	
		
		// creates loader
		DummyStackLoader loader = new DummyStackLoader(width, height, numFrames, exps, new UnsignedIntType());
		assertEquals(exps.length, loader.getNumberOfChannels());
		
		// creates processor
		DummyController cont = new DummyController();
		ImgProcessor proc = new ImgProcessor(dir, cont, loader);
		
		// performs avg and var calculation
		AvgVarStacks<PlaneImg> avgs_vars = proc.computeAvgAndVar();
		
		// checks results
		for(int q=0;q<avgs_vars.getAvgs().length;q++) {
			int k = 0;
			boolean b = false;
			for(int j=0;j<exps.length;j++) {
				if(Double.compare(exps[j], avgs_vars.getAvgs()[q].getExposure()) == 0) {
					k = j;
					b = true;
				}
			}
			assertTrue(b);
			assertEquals(exps[k], avgs_vars.getAvgs()[q].getExposure(), 0.0001);

			// gets average and variance maps at the current exposure
			Img<FloatType> avg = avgs_vars.getAvgs()[q].getImage();
			Img<FloatType> var = avgs_vars.getVars()[q].getImage();

			// dimensions
			assertEquals(width, avg.dimension(0));
			assertEquals(width, var.dimension(0));
			assertEquals(height, avg.dimension(1));
			assertEquals(height, var.dimension(1));
			assertEquals(1, avg.dimension(2));
			assertEquals(1, var.dimension(2));
			
			// checks values
			float lowpix_avg = (float) GenerateData.getLowPixAverage(exps[k]);
			float hotpix_avg = (float) GenerateData.getHotPixAverage(exps[k]);
			float lowpix_var = (float) GenerateData.getLowPixVariance(exps[k]);
			float hotpix_var = (float) GenerateData.getHotPixVariance(exps[k]);
			
			Cursor<FloatType> cursor = avg.localizingCursor();
			RandomAccess<FloatType> r_var = var.randomAccess();
			while(cursor.hasNext()) {
				FloatType t = cursor.next();
				r_var.setPosition(cursor);
				FloatType u = r_var.get();

				// checks against expected values (with a certain tolerance)
				if(cursor.getIntPosition(0) % 10 == 0 && cursor.getIntPosition(1) % 20 == 0) {
					assertEquals(hotpix_avg, t.get(), 0.01*hotpix_avg);
					assertEquals(hotpix_var, u.get(), 0.06*hotpix_var); 
				} else {
					assertEquals(lowpix_avg, t.get(), 0.01*lowpix_avg);
					assertEquals(lowpix_var, u.get(), 0.06*lowpix_var);
				}
			}
		}
	}
}
