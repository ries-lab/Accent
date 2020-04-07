package de.embl.rieslab.accent.fiji.datagen;

import java.util.concurrent.ThreadLocalRandom;


import io.scif.img.ImgSaver;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.type.numeric.real.FloatType;

/**
 * Generates images according to physical numbers with low and hot pixels. Variance is a very rough approximation.
 * 
 * @author Joran Deschamps
 *
 */
public class GenerateData {
	
	// fixed physical numbers to generate the data
	// very dim
	public final static double DIMPIX_BASELINE = 78.88;
	public final static double DIMPIX_DCPERSEC = 96.262; 
	public final static double DIMPIX_RNSQ = 40.31; 
	public final static double DIMPIX_TNSQPERSEC = 111.503;
	
	// dim pixel
	public final static double LOWPIX_BASELINE = 128.709;
	public final static double LOWPIX_DCPERSEC = 261.262; 
	public final static double LOWPIX_RNSQ = 170.501; 
	public final static double LOWPIX_TNSQPERSEC = 591.503;
	
	// hot pixel
	public final static double HOTPIX_BASELINE = 1228.587;
	public final static double HOTPIX_DCPERSEC = 50709.523; 
	public final static double HOTPIX_RNSQ = 16926.469; 
	public final static double HOTPIX_TNSQPERSEC = 348342.688;
	
	
	public static Img<UnsignedShortType> generateUnsignedShortType(int width, int height, int numFrames, double exposure){
		
		final long[] dim = new long[] { width, height, numFrames };
		final ImgFactory<UnsignedShortType> factory =  new ArrayImgFactory<UnsignedShortType>( new UnsignedShortType() );
		Img<UnsignedShortType> img = factory.create(dim);
		
		double hot_avg = getHotPixAverage(exposure);
		double hot_var = getHotPixVariance(exposure);
		double low_avg = getLowPixAverage(exposure);
		double low_var = getLowPixVariance(exposure);
	
		RandomAccess<UnsignedShortType> r = img.randomAccess();
		for(int x=0;x<width;x++) {
			r.setPosition(x, 0);
			for(int y=0;y<height;y++) {
				r.setPosition(y, 1);
				
				double[] pixs;
				if(x % 10 == 0 && y % 20 == 0) {
					pixs = generateGaussianDistributedValues(hot_avg, hot_var, numFrames);
				} else {
					pixs = generateGaussianDistributedValues(low_avg, low_var, numFrames);
				}
				
				for(int z=0;z<numFrames;z++) {
					r.setPosition(z,2);
					r.get().set((int) pixs[z]);
				}
			}
		}
				
		return img;
	}
	
	// at high exposures, the bytetype will overflow
	public static Img<UnsignedByteType> generateUnsignedByteType(int width, int height, int numFrames, double exposure){
		
		final long[] dim = new long[] { width, height, numFrames };
		final ImgFactory<UnsignedByteType> factory =  new ArrayImgFactory<UnsignedByteType>( new UnsignedByteType() );
		Img<UnsignedByteType> img = factory.create(dim);
		
		double dim_avg = getDimPixAverage(exposure);
		double dim_var = getDimPixVariance(exposure);
		double low_avg = getLowPixAverage(exposure);
		double low_var = getLowPixVariance(exposure);
				
		RandomAccess<UnsignedByteType> r = img.randomAccess();
		for(int x=0;x<width;x++) {
			r.setPosition(x, 0);
			for(int y=0;y<height;y++) {
				r.setPosition(y, 1);
				
				double[] pixs;
				if(x % 10 == 0 && y % 20 == 0) {
					pixs = generateGaussianDistributedValues(low_avg, low_var, numFrames);
				} else {
					pixs = generateGaussianDistributedValues(dim_avg, dim_var, numFrames);
				}
				
				for(int z=0;z<numFrames;z++) {
					r.setPosition(z,2);
					r.get().set((int) pixs[z]);
				}
			}
		}
		
		return img;
	}

	public static Img<FloatType> generateFloatType(int width, int height, int numFrames, double exposure){
		
		final long[] dim = new long[] { width, height, numFrames };
		final ImgFactory<FloatType> factory =  new ArrayImgFactory<FloatType>( new FloatType() );
		Img<FloatType> img = factory.create(dim);
		
		double hot_avg = getHotPixAverage(exposure);
		double hot_var = getHotPixVariance(exposure);
		double low_avg = getLowPixAverage(exposure);
		double low_var = getLowPixVariance(exposure);
				
		RandomAccess<FloatType> r = img.randomAccess();
		for(int x=0;x<width;x++) {
			r.setPosition(x, 0);
			for(int y=0;y<height;y++) {
				r.setPosition(y, 1);
				
				double[] pixs;
				if(x % 10 == 0 && y % 20 == 0) {
					pixs = generateGaussianDistributedValues(hot_avg, hot_var, numFrames);
				} else {
					pixs = generateGaussianDistributedValues(low_avg, low_var, numFrames);
				}
				
				for(int z=0;z<numFrames;z++) {
					r.setPosition(z,2);
					r.get().set((float) pixs[z]);
				}
			}
		}
		
		return img;
	}
	
	public static double[] generateGaussianDistributedValues(double mean, double variance, int numberOfPoints) {
		double[] values = new double[numberOfPoints];
		ThreadLocalRandom generator = ThreadLocalRandom.current();

		double std = Math.sqrt(variance);
		for (int i = 0; i < numberOfPoints; i++) {
			values[i] = mean + generator.nextGaussian() * std;
		}
		return values;
	}

	public static double getDimPixAverage(double exposure) {
		return (DIMPIX_BASELINE+DIMPIX_DCPERSEC*exposure/1000.);
	}
	
	public static double getDimPixVariance(double exposure) {
		return (DIMPIX_RNSQ+DIMPIX_TNSQPERSEC*exposure/1000.);
	}

	public static double getHotPixAverage(double exposure) {
		return (HOTPIX_BASELINE+HOTPIX_DCPERSEC*exposure/1000.);
	}
	
	public static double getHotPixVariance(double exposure) {
		return (HOTPIX_RNSQ+HOTPIX_TNSQPERSEC*exposure/1000.);
	}
	
	public static double getLowPixAverage(double exposure) {
		return (LOWPIX_BASELINE+LOWPIX_DCPERSEC*exposure/1000.);
	}
	
	public static double getLowPixVariance(double exposure) {
		return (LOWPIX_RNSQ+LOWPIX_TNSQPERSEC*exposure/1000.);
	}
	
	public static void generateImagesToDisk(String path, int width, int height, int numFrames, double[] exposure) {
		ImgSaver saver = new ImgSaver();
		
		for (double e : exposure) {
			Img<FloatType> img_f = generateFloatType(width, height, numFrames, e);
			String name_f = path + "/" + e + "ms_float.tif";
			saver.saveImg(name_f, img_f);

			Img<UnsignedShortType> img_s = generateUnsignedShortType(width, height, numFrames, e);
			String name_s = path + "/" + e + "ms_unshort.tif";
			saver.saveImg(name_s, img_s);

			Img<UnsignedByteType> img_b = generateUnsignedByteType(width, height, numFrames, e);
			String name_b = path + "/" + e + "ms_unbyte.tif";
			saver.saveImg(name_b, img_b);
		}
	}
}
