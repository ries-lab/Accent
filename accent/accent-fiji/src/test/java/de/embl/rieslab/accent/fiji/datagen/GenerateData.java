package de.embl.rieslab.accent.fiji.datagen;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import io.scif.img.ImgSaver;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.type.numeric.real.FloatType;

/**
 * Generates images according to physical numbers with low and hot pixels.
 * 
 * To refactor:
 * - make use of the generics to reduce code load, all methods can probably be combined with generics and lambdas
 * - there must be a way to save Img stacks as individual planes without copying the data
 * - generateAndWrite method should be refactored as the same code is copied multiple times in the method
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

	
	public static Img<UnsignedShortType> generateUnsignedShortType(int width, int height, int numFrames,
			double exposure) {

		final long[] dim = new long[] { width, height, numFrames };
		final ImgFactory<UnsignedShortType> factory = new ArrayImgFactory<UnsignedShortType>(new UnsignedShortType());
		Img<UnsignedShortType> img = factory.create(dim);

		double hot_avg = getHotPixAverage(exposure);
		double hot_var = getHotPixVariance(exposure);
		double low_avg = getLowPixAverage(exposure);
		double low_var = getLowPixVariance(exposure);

		RandomAccess<UnsignedShortType> r = img.randomAccess();
		for (int x = 0; x < width; x++) {
			r.setPosition(x, 0);
			for (int y = 0; y < height; y++) {
				r.setPosition(y, 1);

				double[] pixs;
				if (x % 10 == 0 && y % 20 == 0) {
					pixs = generateGaussianDistributedValues(hot_avg, hot_var, numFrames);
				} else {
					pixs = generateGaussianDistributedValues(low_avg, low_var, numFrames);
				}

				for (int z = 0; z < numFrames; z++) {
					r.setPosition(z, 2);
					r.get().set((int) pixs[z]);
				}
			}
		}

		return img;
	}
	
	public static ArrayList<Img<UnsignedShortType>> generateUnsignedShortTypeSingles(int width, int height, int numFrames,
			double exposure) {

		final long[] dim = new long[] { width, height, 1 };
		final ImgFactory<UnsignedShortType> factory = new ArrayImgFactory<UnsignedShortType>(new UnsignedShortType());
		ArrayList<Img<UnsignedShortType>> img = new ArrayList<Img<UnsignedShortType>>();
		ArrayList<RandomAccess<UnsignedShortType>> r_arr = new ArrayList<RandomAccess<UnsignedShortType>>();

		double hot_avg = getHotPixAverage(exposure);
		double hot_var = getHotPixVariance(exposure);
		double low_avg = getLowPixAverage(exposure);
		double low_var = getLowPixVariance(exposure);

		for(int f=0;f<numFrames;f++) {
			img.add(factory.create(dim));
			r_arr.add(img.get(f).randomAccess());
		}
		
		for (int x = 0; x < width; x++) {
			for(int f=0;f<numFrames;f++) {
				r_arr.get(f).setPosition(x, 0);
			}
			for (int y = 0; y < height; y++) {
				for(int f=0;f<numFrames;f++) {
					r_arr.get(f).setPosition(y, 1);
				}

				double[] pixs;
				if (x % 10 == 0 && y % 20 == 0) {
					pixs = generateGaussianDistributedValues(hot_avg, hot_var, numFrames);
				} else {
					pixs = generateGaussianDistributedValues(low_avg, low_var, numFrames);
				}

				for(int f=0;f<numFrames;f++) {
					r_arr.get(f).get().set((int) pixs[f]); 
				}
			}
		}

		return img;
	}

	// at high exposures, the bytetype will overflow
	public static Img<UnsignedByteType> generateUnsignedByteType(int width, int height, int numFrames,
			double exposure) {

		final long[] dim = new long[] { width, height, numFrames };
		final ImgFactory<UnsignedByteType> factory = new ArrayImgFactory<UnsignedByteType>(new UnsignedByteType());
		Img<UnsignedByteType> img = factory.create(dim);

		double dim_avg = getDimPixAverage(exposure);
		double dim_var = getDimPixVariance(exposure);
		double low_avg = getLowPixAverage(exposure);
		double low_var = getLowPixVariance(exposure);

		RandomAccess<UnsignedByteType> r = img.randomAccess();
		for (int x = 0; x < width; x++) {
			r.setPosition(x, 0);
			for (int y = 0; y < height; y++) {
				r.setPosition(y, 1);

				double[] pixs;
				if (x % 10 == 0 && y % 20 == 0) {
					pixs = generateGaussianDistributedValues(low_avg, low_var, numFrames);
				} else {
					pixs = generateGaussianDistributedValues(dim_avg, dim_var, numFrames);
				}

				for (int z = 0; z < numFrames; z++) {
					r.setPosition(z, 2);
					r.get().set((int) pixs[z]);
				}
			}
		}

		return img;
	}

	// at high exposures, the bytetype will overflow
	public static ArrayList<Img<UnsignedByteType>> generateUnsignedByteTypeSingles(int width, int height, int numFrames,
			double exposure) {

		final long[] dim = new long[] { width, height, numFrames };
		final ImgFactory<UnsignedByteType> factory = new ArrayImgFactory<UnsignedByteType>(new UnsignedByteType());
		ArrayList<Img<UnsignedByteType>> img = new ArrayList<Img<UnsignedByteType>>();
		ArrayList<RandomAccess<UnsignedByteType>> r_arr = new ArrayList<RandomAccess<UnsignedByteType>>();

		double dim_avg = getDimPixAverage(exposure);
		double dim_var = getDimPixVariance(exposure);
		double low_avg = getLowPixAverage(exposure);
		double low_var = getLowPixVariance(exposure);

		for(int f=0;f<numFrames;f++) {
			img.add(factory.create(dim));
			r_arr.add(img.get(f).randomAccess());
		}
		
		for (int x = 0; x < width; x++) {
			for(int f=0;f<numFrames;f++) {
				r_arr.get(f).setPosition(x, 0);
			}
			for (int y = 0; y < height; y++) {
				for(int f=0;f<numFrames;f++) {
					r_arr.get(f).setPosition(y, 1);
				}

				double[] pixs;
				if (x % 10 == 0 && y % 20 == 0) {
					pixs = generateGaussianDistributedValues(low_avg, low_var, numFrames);
				} else {
					pixs = generateGaussianDistributedValues(dim_avg, dim_var, numFrames);
				}

				for(int f=0;f<numFrames;f++) {
					r_arr.get(f).get().set((int) pixs[f]); 
				}
			}
		}
		return img;
	}

	public static Img<FloatType> generateFloatType(int width, int height, int numFrames, double exposure) {

		final long[] dim = new long[] { width, height, numFrames };
		final ImgFactory<FloatType> factory = new ArrayImgFactory<FloatType>(new FloatType());
		Img<FloatType> img = factory.create(dim);

		double hot_avg = getHotPixAverage(exposure);
		double hot_var = getHotPixVariance(exposure);
		double low_avg = getLowPixAverage(exposure);
		double low_var = getLowPixVariance(exposure);

		RandomAccess<FloatType> r = img.randomAccess();
		for (int x = 0; x < width; x++) {
			r.setPosition(x, 0);
			for (int y = 0; y < height; y++) {
				r.setPosition(y, 1);

				double[] pixs;
				if (x % 10 == 0 && y % 20 == 0) {
					pixs = generateGaussianDistributedValues(hot_avg, hot_var, numFrames);
				} else {
					pixs = generateGaussianDistributedValues(low_avg, low_var, numFrames);
				}

				for (int z = 0; z < numFrames; z++) {
					r.setPosition(z, 2);
					r.get().set((float) pixs[z]);
				}
			}
		}
		return img;
	}

	public static ArrayList<Img<FloatType>> generateFloatTypeSingles(int width, int height, int numFrames,
			double exposure) {

		final long[] dim = new long[] { width, height, numFrames };
		final ImgFactory<FloatType> factory = new ArrayImgFactory<FloatType>(new FloatType());
		ArrayList<Img<FloatType>> img = new ArrayList<Img<FloatType>>(numFrames);
		ArrayList<RandomAccess<FloatType>> r_arr = new ArrayList<RandomAccess<FloatType>>();

		double hot_avg = getHotPixAverage(exposure);
		double hot_var = getHotPixVariance(exposure);
		double low_avg = getLowPixAverage(exposure);
		double low_var = getLowPixVariance(exposure);

		for(int f=0;f<numFrames;f++) {
			img.add(factory.create(dim));
			r_arr.add(img.get(f).randomAccess());
		}
		
		for (int x = 0; x < width; x++) {
			for(int f=0;f<numFrames;f++) {
				r_arr.get(f).setPosition(x, 0);
			}
			for (int y = 0; y < height; y++) {
				for(int f=0;f<numFrames;f++) {
					r_arr.get(f).setPosition(y, 1);
				}

				double[] pixs;
				if (x % 10 == 0 && y % 20 == 0) {
					pixs = generateGaussianDistributedValues(hot_avg, hot_var, numFrames);
				} else {
					pixs = generateGaussianDistributedValues(low_avg, low_var, numFrames);
				}

				for(int f=0;f<numFrames;f++) {
					r_arr.get(f).get().set((int) pixs[f]); 
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
	
	// TODO refactor
	// there must be a way to save the individual planes of the img without generating them with another method...
	public static void generateAndWriteToDisk(String path, int width, int height, int numFrames, double[] exposure, boolean stack, RealType<?> type) {
		ImgSaver saver = new ImgSaver();
		
		for (double e : exposure) {
			
			if(type.getBitsPerPixel() > 16) {
				if(stack) {
					Img<FloatType> img_f = generateFloatType(width, height, numFrames, e);
					String name_f = path + "\\" + e + "ms_float.tif";
					saver.saveImg(name_f, img_f);
				} else {
					ArrayList<Img<FloatType>> img_f = generateFloatTypeSingles(width, height, numFrames, e);	
					String name_f = path + "\\" + e + "ms_float";
					File f = new File(name_f);
					if(!f.exists()) {
						f.mkdir();
					}

					String name_file = name_f + "\\";
					int numZeros = String.valueOf(numFrames).length();
					for(int i=0;i<img_f.size();i++) {
						int num = String.valueOf(i).length();
						String s = name_file+"single_float_";
						for(int k=0; k<numZeros-num;k++) {
							s=s+"0";
						}
						s = s+i+".tif";
						
						saver.saveImg(s, img_f.get(i));
					}
				}
				
			} else if(type.getBitsPerPixel() == 16) {
				if(stack) {
					Img<UnsignedShortType> img_s = generateUnsignedShortType(width, height, numFrames, e);
					String name_s = path + "\\" + e + "ms_unshort.tif";
					saver.saveImg(name_s, img_s);
				} else {
					ArrayList<Img<UnsignedShortType>> img_f = generateUnsignedShortTypeSingles(width, height, numFrames, e);	
					String name_f = path + "\\" + e + "ms_unshort";
					File f = new File(name_f);
					if(!f.exists()) {
						f.mkdir();
					}

					String name_file = name_f + "\\";
					int numZeros = String.valueOf(numFrames).length();
					for(int i=0;i<img_f.size();i++) {
						int num = String.valueOf(i).length();
						String s = name_file+"single_short_";
						for(int k=0; k<numZeros-num;k++) {
							s=s+"0";
						}
						s = s+i+".tif";

						saver.saveImg(s, img_f.get(i));
					}
				}
			} else if(type.getBitsPerPixel() == 8) {
				if(stack) {
					Img<UnsignedByteType> img_b = generateUnsignedByteType(width, height, numFrames, e);
					String name_b = path + "\\" + e + "ms_unbyte.tif";
					saver.saveImg(name_b, img_b);
				} else {
					ArrayList<Img<UnsignedByteType>> img_f = generateUnsignedByteTypeSingles(width, height, numFrames, e);	
					String name_f = path + "\\" + e + "ms_unbyte";
					File f = new File(name_f);
					if(!f.exists()) {
						f.mkdir();
					}

					String name_file = name_f + "\\";
					int numZeros = String.valueOf(numFrames).length();
					for(int i=0;i<img_f.size();i++) {
						int num = String.valueOf(i).length();
						String s = name_file+"single_byte_";
						for(int k=0; k<numZeros-num;k++) {
							s=s+"0";
						}
						s = s+i+".tif";
						
						saver.saveImg(s, img_f.get(i));
					}
				}
			}
			
		}
	}
}
