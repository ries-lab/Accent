package de.embl.rieslab.accent.fiji.processor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import net.imagej.DatasetService;
import org.junit.Before;

import de.embl.rieslab.accent.common.data.image.AvgVarStacks;
import de.embl.rieslab.accent.common.data.roi.SimpleRoi;
import de.embl.rieslab.accent.common.interfaces.pipeline.Loader;
import de.embl.rieslab.accent.fiji.data.image.PlaneImg;
import de.embl.rieslab.accent.fiji.data.image.StackImg;
import de.embl.rieslab.accent.fiji.datagen.GenerateData;
import de.embl.rieslab.accent.fiji.dummys.DummyController;
import de.embl.rieslab.accent.fiji.dummys.DummyStackLoader;
import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.integer.UnsignedIntType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.type.numeric.real.FloatType;
import org.junit.Test;
import org.scijava.Context;

public class ImgProcessorTest {

	// TODO test cases resembling multiple stacks or stacks 
	// TODO test cases where the third dimension is 2,3,4...etc...
	
	int width = 11;
	int height = 21;
	int numFrames = 15000;
	double[] exps = {0.1, 2.0, 3.5};

	private DatasetService service;

	@Before
	public void init(){
		Context ctx = new Context(DatasetService.class);
		service = ctx.service(DatasetService.class);
	}

	@Test
	public void testProcUnsignedByteStackLoader() {
		String dir = "AccentTemp-proc-s";
		
		// creates loader
		DummyStackLoader loader = new DummyStackLoader(service, width, height, numFrames, exps, new UnsignedByteType());
		assertEquals(exps.length, loader.getNumberOfChannels());
		
		// creates processor
		DummyController cont = new DummyController();
		ImgProcessor proc = new ImgProcessor(dir, new SimpleRoi(0,0,width,height), cont, loader);
		
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
		DummyStackLoader loader = new DummyStackLoader(service, width, height, numFrames, exps, new UnsignedShortType());
		assertEquals(exps.length, loader.getNumberOfChannels());
		
		// creates processor
		DummyController cont = new DummyController();
		ImgProcessor proc = new ImgProcessor(dir, new SimpleRoi(0,0,width,height), cont, loader);
		
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
		DummyStackLoader loader = new DummyStackLoader(service, width, height, numFrames, exps, new FloatType());
		assertEquals(exps.length, loader.getNumberOfChannels());
		
		// creates processor
		DummyController cont = new DummyController();
		ImgProcessor proc = new ImgProcessor(dir, new SimpleRoi(0,0,width,height), cont, loader);
		
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
		DummyStackLoader loader = new DummyStackLoader(service, width, height, numFrames, exps, new UnsignedIntType());
		assertEquals(exps.length, loader.getNumberOfChannels());
		
		// creates processor
		DummyController cont = new DummyController();
		ImgProcessor proc = new ImgProcessor(dir, new SimpleRoi(0,0,width,height), cont, loader);
		
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
	public void testProcessor() {
		String dir = "AccentTemp-proc-s";	
		
		// creates loader
		final int nChannels = 2;
		final double[] exps = {0.1, 1000}; 
		final long width = 1;
		final long height = 1;
		final long zDepth = 1;
		final long cDepth = 1;
		final long tDepth = 10;
		
		Loader<StackImg> loader = new Loader<StackImg>() {
			int currentChannel = 0;
			int currentFrame = 0;

			private StackImg getImg(int t, int channel) {
				Img<UnsignedShortType> img  = ArrayImgs.unsignedShorts(width, height, zDepth, cDepth, 1);
				img.randomAccess().get().set(new UnsignedShortType(t));
				return new StackImg(service.create(img), exps[channel]);
			}
			
			@Override
			public StackImg getNext(int channel) {
				return getImg(currentFrame++, channel);
			}

			@Override
			public boolean hasNext(int channel) {
				return currentFrame < tDepth;
			}

			@Override
			public int getNumberOfChannels() {
				return nChannels;
			}

			@Override
			public boolean openChannel(int channel) {
				currentChannel = channel;
				currentFrame = 0;
				
				return true;
			}

			@Override
			public int getChannelLength() {
				return (int) tDepth;
			}};
		
		// creates processor
		DummyController cont = new DummyController();
		ImgProcessor proc = new ImgProcessor(dir, new SimpleRoi(0,0,(int)width,(int)height), cont, loader);
		
		// performs avg and var calculation
		AvgVarStacks<PlaneImg> avgs_vars = proc.computeAvgAndVar();
		
		// grund truth
		double realAvg = 0, realVar=0;
		for(int i=0;i<tDepth;i++) {
			realAvg += i;
			realVar += i*i;
		}
		realAvg = realAvg/tDepth;
		realVar = realVar/tDepth - realAvg*realAvg;
		
		// checks results
		for(int i=0;i<exps.length;i++) {
			Img<FloatType> av = avgs_vars.getAvgs()[i].getImage();
			Img<FloatType> var = avgs_vars.getVars()[i].getImage();

			assertEquals(realAvg, av.randomAccess().get().get(), 0.0010);
			assertEquals(realVar, var.randomAccess().get().get(), 0.0001);
		}
	}
	
}
