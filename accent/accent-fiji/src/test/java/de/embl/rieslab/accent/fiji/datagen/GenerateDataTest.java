package de.embl.rieslab.accent.fiji.datagen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.junit.Test;

import de.embl.rieslab.accent.common.utils.AccentUtils;
import de.embl.rieslab.accent.fiji.utils.AccentFijiUtils;
import io.scif.services.DatasetIOService;
import net.imagej.Dataset;
import net.imagej.ImageJ;
import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.integer.UnsignedIntType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Intervals;
import net.imglib2.view.IntervalView;
import net.imglib2.view.Views;

public class GenerateDataTest {
	
	/**
	 * Tests if the generated UnsignedByte images have the expected average and variance per pixel (within 1% and 6% tolerance respectively).
	 */
	@Test
	public void testByteAverageVariance() {
		int width = 10;
		int height = 20;
		int numFrames = 15000;
		double[] exps = {0.1, 1, 5, 10};
		
		for(double exposure: exps) {
			Img<UnsignedByteType> img = GenerateData.generateUnsignedByteType(width, height, numFrames, exposure);
			Img<FloatType> avg = (new ArrayImgFactory<FloatType>(new FloatType())).create(new int[] {width, height, 1});
			Img<FloatType> var = (new ArrayImgFactory<FloatType>(new FloatType())).create(new int[] {width, height, 1});
			
			// average
			Cursor<FloatType> avg_curs = avg.localizingCursor();
			RandomAccess<UnsignedByteType> r_img = img.randomAccess();
			RandomAccess<FloatType> r_var = var.randomAccess();
			while(avg_curs.hasNext()) {
				FloatType t = avg_curs.next();
				
				r_var.setPosition(avg_curs);
				r_img.setPosition(avg_curs);
				FloatType u = r_var.get();

				
				float f = 0, g = 0;
				for(int z=0;z<numFrames;z++) {
					r_img.setPosition(z,2);
					f += r_img.get().get()/((double) numFrames);
					g += (r_img.get().get() * r_img.get().get()) / ((double) numFrames);
				}		
				g -= f*f;
				
				t.set(f);
				u.set(g);
				
			}
			
			// arbitrary tests to see if the average and variance are somewhat close to expected 
			float lowpix_avg = (float) GenerateData.getLowPixAverage(exposure);
			float dimpix_avg = (float) GenerateData.getDimPixAverage(exposure);
			float lowpix_var = (float) GenerateData.getLowPixVariance(exposure);
			float dimpix_var = (float) GenerateData.getDimPixVariance(exposure);
			
			Cursor<FloatType> curs = avg.localizingCursor();
			RandomAccess<FloatType> r_var2 = var.randomAccess();
			while(curs.hasNext()) {
				FloatType t = curs.next();
				r_var2.setPosition(curs);
				FloatType u = r_var2.get();
				
				if(curs.getIntPosition(0) % 10 == 0 && curs.getIntPosition(1) % 20 == 0) {
					assertEquals(lowpix_avg, t.get(), 0.01*lowpix_avg);
					assertEquals(lowpix_var, u.get(), 0.06*lowpix_var); 
				} else {
					assertEquals(dimpix_avg, t.get(), 0.01*dimpix_avg);
					assertEquals(dimpix_var, u.get(), 0.06*dimpix_var);
				}
			}
		}
	}	
	
	/**
	 * Tests if the generated Float images have the expected average and variance per pixel (within 1% and 6% tolerance respectively).
	 */
	@Test
	public void testFloatAverageVariance() {
		int width = 10;
		int height = 20;
		int numFrames = 15000;
		double[] exps = {20, 50, 300};
		
		for(double exposure: exps) {
			Img<FloatType> img = GenerateData.generateFloatType(width, height, numFrames, exposure);
			Img<FloatType> avg = (new ArrayImgFactory<FloatType>(new FloatType())).create(new int[] {width, height, 1});
			Img<FloatType> var = (new ArrayImgFactory<FloatType>(new FloatType())).create(new int[] {width, height, 1});
			
			// compute average and variance
			Cursor<FloatType> avg_curs = avg.localizingCursor();
			RandomAccess<FloatType> r_img = img.randomAccess();
			RandomAccess<FloatType> r_var = var.randomAccess();
			while(avg_curs.hasNext()) {
				FloatType t = avg_curs.next();
				
				r_var.setPosition(avg_curs);
				r_img.setPosition(avg_curs);
				FloatType u = r_var.get();

				
				float f = 0, g = 0;
				for(int z=0;z<numFrames;z++) {
					r_img.setPosition(z,2);
					f += r_img.get().get()/((double) numFrames);
					g += (r_img.get().get() * r_img.get().get()) / ((double) numFrames);
				}		
				g -= f*f;
				
				t.set(f);
				u.set(g);
				
			}
			
			// arbitrary tests to see if the average and variance are somewhat close to expected 
			float lowpix_avg = (float) GenerateData.getLowPixAverage(exposure);
			float hotpix_avg = (float) GenerateData.getHotPixAverage(exposure);
			float lowpix_var = (float) GenerateData.getLowPixVariance(exposure);
			float hotpix_var = (float) GenerateData.getHotPixVariance(exposure);
			
			Cursor<FloatType> curs = avg.localizingCursor();
			RandomAccess<FloatType> r_var2 = var.randomAccess();
			while(curs.hasNext()) {
				FloatType t = curs.next();
				r_var2.setPosition(curs);
				FloatType u = r_var2.get();
				
				if(curs.getIntPosition(0) % 10 == 0 && curs.getIntPosition(1) % 20 == 0) {
					assertEquals(hotpix_avg, t.get(), 0.01*hotpix_avg);
					assertEquals(hotpix_var, u.get(), 0.06*hotpix_var); // larger tolerance on the variance
				} else {
					assertEquals(lowpix_avg, t.get(), 0.01*lowpix_avg);
					assertEquals(lowpix_var, u.get(), 0.06*lowpix_var); // larger tolerance on the variance
				}
			}
		}
	}
	
	/**
	 * Tests if the generated UnsignedShort images have the expected average and variance per pixel (within 1% and 6% tolerance respectively).
	 */
	@Test
	public void testShortAverageVariance() {
		int width = 10;
		int height = 20;
		int numFrames = 15000;
		double[] exps = {5, 10, 200};
		
		for(double exposure: exps) {
			Img<UnsignedShortType> img = GenerateData.generateUnsignedShortType(width, height, numFrames, exposure);
			Img<FloatType> avg = (new ArrayImgFactory<FloatType>(new FloatType())).create(new int[] {width, height, 1});
			Img<FloatType> var = (new ArrayImgFactory<FloatType>(new FloatType())).create(new int[] {width, height, 1});
			
			// average
			Cursor<FloatType> avg_curs = avg.localizingCursor();
			RandomAccess<UnsignedShortType> r_img = img.randomAccess();
			RandomAccess<FloatType> r_var = var.randomAccess();
			while(avg_curs.hasNext()) {
				FloatType t = avg_curs.next();
				
				r_var.setPosition(avg_curs);
				r_img.setPosition(avg_curs);
				FloatType u = r_var.get();
	
				float f = 0, g = 0;
				for(int z=0;z<numFrames;z++) {
					r_img.setPosition(z,2);
					f += r_img.get().get()/((double) numFrames);
					g += (r_img.get().get() * r_img.get().get()) / ((double) numFrames);
				}		
				g -= f*f;
				
				t.set(f);
				u.set(g);
				
			}
			
			// tests
			float lowpix_avg = (float) GenerateData.getLowPixAverage(exposure);
			float hotpix_avg = (float) GenerateData.getHotPixAverage(exposure);
			float lowpix_var = (float) GenerateData.getLowPixVariance(exposure);
			float hotpix_var = (float) GenerateData.getHotPixVariance(exposure);
			
			Cursor<FloatType> curs = avg.localizingCursor();
			RandomAccess<FloatType> r_var2 = var.randomAccess();
			while(curs.hasNext()) {
				FloatType t = curs.next();
				r_var2.setPosition(curs);
				FloatType u = r_var2.get();
				
				if(curs.getIntPosition(0) % 10 == 0 && curs.getIntPosition(1) % 20 == 0) {
					assertEquals(hotpix_avg, t.get(), 0.01*hotpix_avg);
					assertEquals(hotpix_var, u.get(), 0.06*hotpix_var); 
				} else {
					assertEquals(lowpix_avg, t.get(), 0.01*lowpix_avg);
					assertEquals(lowpix_var, u.get(), 0.06*lowpix_var);
				}
			}
		}
	}
	
	/**
	 * Tests if the generated UnsignedInt images have the expected average and variance per pixel (within 1% and 6% tolerance respectively).
	 */
	@Test
	public void testIntAverageVariance() {
		int width = 10;
		int height = 20;
		int numFrames = 15000;
		double[] exps = {5, 10, 200};
		
		for(double exposure: exps) {
			Img<UnsignedIntType> img = GenerateData.generateUnsignedIntType(width, height, numFrames, exposure);
			Img<FloatType> avg = (new ArrayImgFactory<FloatType>(new FloatType())).create(new int[] {width, height, 1});
			Img<FloatType> var = (new ArrayImgFactory<FloatType>(new FloatType())).create(new int[] {width, height, 1});
			
			// average
			Cursor<FloatType> avg_curs = avg.localizingCursor();
			RandomAccess<UnsignedIntType> r_img = img.randomAccess();
			RandomAccess<FloatType> r_var = var.randomAccess();
			while(avg_curs.hasNext()) {
				FloatType t = avg_curs.next();
				
				r_var.setPosition(avg_curs);
				r_img.setPosition(avg_curs);
				FloatType u = r_var.get();
	
				float f = 0, g = 0;
				for(int z=0;z<numFrames;z++) {
					r_img.setPosition(z,2);
					f += r_img.get().get()/((double) numFrames);
					g += (r_img.get().get() * r_img.get().get()) / ((double) numFrames);
				}		
				g -= f*f;
				
				t.set(f);
				u.set(g);
				
			}
			
			// tests
			float lowpix_avg = (float) GenerateData.getLowPixAverage(exposure);
			float hotpix_avg = (float) GenerateData.getHotPixAverage(exposure);
			float lowpix_var = (float) GenerateData.getLowPixVariance(exposure);
			float hotpix_var = (float) GenerateData.getHotPixVariance(exposure);
			
			Cursor<FloatType> curs = avg.localizingCursor();
			RandomAccess<FloatType> r_var2 = var.randomAccess();
			while(curs.hasNext()) {
				FloatType t = curs.next();
				r_var2.setPosition(curs);
				FloatType u = r_var2.get();
				
				if(curs.getIntPosition(0) % 10 == 0 && curs.getIntPosition(1) % 20 == 0) {
					assertEquals(hotpix_avg, t.get(), 0.01*hotpix_avg);
					assertEquals(hotpix_var, u.get(), 0.06*hotpix_var); 
				} else {
					assertEquals(lowpix_avg, t.get(), 0.01*lowpix_avg);
					assertEquals(lowpix_var, u.get(), 0.06*lowpix_var);
				}
			}
		}
	}
	
	// takes too long, kept for reference
	//@Test
	public void testShortAverageVarianceImageJOp() {
		final ImageJ ij = new ImageJ();
		int width = 100;
		int height = 50;
		int numFrames = 100;
		double[] exps = {0.1, 1, 10};
		
		for(double exposure: exps) {
			Img<UnsignedShortType> img = GenerateData.generateUnsignedShortType(width, height, numFrames, exposure);
			Img<DoubleType> avg = (new ArrayImgFactory<DoubleType>(new DoubleType())).create(new int[] {width, height, 1});
			Img<DoubleType> var = (new ArrayImgFactory<DoubleType>(new DoubleType())).create(new int[] {width, height, 1});
			
			// average
			Cursor<DoubleType> avg_curs = avg.localizingCursor();
			Cursor<DoubleType> var_curs = var.localizingCursor();
			while(avg_curs.hasNext()) {
				DoubleType t = avg_curs.next();
				DoubleType d = var_curs.next();
				
				IntervalView<UnsignedShortType> interval = Views.interval(img, Intervals.createMinSize(avg_curs.getIntPosition(0),avg_curs.getIntPosition(1),0,1,1,numFrames));
				IterableInterval<UnsignedShortType> it = Views.flatIterable(interval);
				t.set(ij.op().stats().mean(it));
				d.set(ij.op().stats().variance(it));
			}
			
			float lowpix_avg = (float) GenerateData.getLowPixAverage(exposure);;
			float hotpix_avg = (float) GenerateData.getHotPixAverage(exposure);;
			Cursor<DoubleType> curs2 = avg.localizingCursor();
			while(curs2.hasNext()) {
				DoubleType t = curs2.next();
				if(curs2.getIntPosition(0) % 10 == 0 && curs2.getIntPosition(1) % 20 == 0) {
					assertEquals(hotpix_avg, t.get(), 0.05*hotpix_avg);
				} else {
					assertEquals(lowpix_avg, t.get(), 0.05*lowpix_avg);
				}
			}
						
			float lowpix_var = (float) GenerateData.getLowPixVariance(exposure);
			float hotpix_var = (float) GenerateData.getHotPixVariance(exposure);
			Cursor<DoubleType> curs3 = var.localizingCursor();
			while (curs3.hasNext()) {
				DoubleType t = curs3.next();
				if (curs3.getIntPosition(0) % 10 == 0 && curs3.getIntPosition(1) % 20 == 0) {
					assertEquals(hotpix_var, t.get(), 0.1*hotpix_var); // on the variance, higher bar cause very approximate generation
				} else {
					assertEquals(lowpix_var, t.get(), 0.1*lowpix_var);
				}
			}
		}
	}
	
	public void testTiming() {
		int n = 10;
		long start = System.currentTimeMillis();
		for(int i=0;i<n;i++) {
			testShortAverageVariance();
		}
		long end = System.currentTimeMillis();
		double res = (end-start)/n/1000.;
		System.out.println(res);
		
		start = System.currentTimeMillis();
		for(int i=0;i<n;i++) {
			testShortAverageVarianceImageJOp();
		}
		end = System.currentTimeMillis();
		res = (end-start)/n/1000.;
		System.out.println(res);
	}
		
	@Test
	public void testGaussian() {
		double[] d = GenerateData.generateGaussianDistributedValues(3.5,10,200000);
		
		double mean = 0, var = 0;
		for(int i=0;i<d.length;i++) {
			mean += d[i]/d.length;
			var += d[i]*d[i]/d.length;
		}
		var -= mean*mean;

		assertEquals(3.5, mean, 0.01*3.5);
		assertEquals(10, var, 0.01*10);
	}
	
	@Test
	public void testWritingToDisk() {
		String dir = "AccentTemp";		
		File f_dir = new File(dir);
		if(!f_dir.exists()) {
			f_dir.mkdir();
		}

		int width = 10;
		int height = 20;
		int numFrames = 100;
		double[] exps = {0.1, 1, 10};
		
		
		// stacks
		GenerateData.generateAndWriteToDisk(dir, width, height, numFrames, exps, true, new UnsignedShortType());
		Map<Double, String> m = AccentFijiUtils.getExposures(dir, true);

		assertEquals(exps.length, m.size());
		assertEquals(exps.length, AccentFijiUtils.getNumberTifsContainMs(dir));
		
		// deletes all
		for(Entry<Double, String> e: m.entrySet()) {
			File f = new File(e.getValue());
			if(f.exists())
				assertTrue(f.delete());
		}
		
		// single images
		GenerateData.generateAndWriteToDisk(dir, width, height, numFrames, exps, false, new UnsignedShortType());
		
		m = AccentFijiUtils.getExposures(dir, false);
		assertEquals(exps.length, m.size());
		assertEquals(exps.length, AccentFijiUtils.getNumberDirectoriesContainMs(dir));
		
		for(Entry<Double, String> el: m.entrySet()) {
			assertEquals(numFrames, AccentFijiUtils.getNumberTifs(el.getValue()));
			
			try {
				Files.list(Paths.get(el.getValue()))
					.forEach(e -> (new File(e.toString())).delete());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			// deletes all
			File f = new File(el.getValue());
			if(f.exists())
				assertTrue(f.delete());
		}
		
		// attempts to delete folder
		f_dir.delete();
	}
	
	// for reference
	public void testSingleDataOpening() {
		int width = 3;
		int height = 4;
		int numFrames = 15;
		double[] exps = {0.1};
		
		final ImageJ ij = new ImageJ();
		DatasetIOService serv = ij.scifio().datasetIO();
		
		// main folder
		String dir = "AccentTemp-do";		
		AccentUtils.createFolder(dir);
		
		// unsigned byte
		String dir_b = dir+"\\b_singles"; 
		AccentUtils.createFolder(dir_b);
		GenerateData.generateAndWriteToDisk(dir_b, width, height, numFrames, exps, false, new UnsignedByteType());
		List<String> list_b = listFiles(dir_b+"\\0.1ms_unbyte");
		try {
			Dataset img = serv.open(list_b.get(0));
			System.out.println(img.dimension(2)); // prints numFrames
			//assertEquals(1, img.dimension(2));
		} catch (IOException e) {
			e.printStackTrace();
		}

		// unsigned short
		String dir_s = dir+"\\s_singles"; 
		AccentUtils.createFolder(dir_s);
		GenerateData.generateAndWriteToDisk(dir_s, width, height, numFrames, exps, false, new UnsignedShortType());
		List<String> list_s = listFiles(dir_s+"\\0.1ms_unshort");
		try {
			Dataset img = serv.open(list_s.get(0));
			System.out.println(img.dimension(2)); // prints 1
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// unsigned int
		String dir_i = dir+"\\i_singles"; 
		AccentUtils.createFolder(dir_i);
		GenerateData.generateAndWriteToDisk(dir_i, width, height, numFrames, exps, false, new UnsignedIntType());
		List<String> list_i = listFiles(dir_i+"\\0.1ms_unint");
		try {
			Dataset img = serv.open(list_i.get(0));
			System.out.println(img.dimension(2)); // prints 1
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private List<String> listFiles(String path){
		List<String> list = null;
		try {
			list = Files.list(Paths.get(path)).map(Path::toString).collect(Collectors.toList());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}
}
