package de.embl.rieslab.accent.fiji.datagen;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import ij.IJ;
import ij.ImagePlus;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.integer.UnsignedIntType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.type.numeric.real.FloatType;

/**
 * Generates images according to physical numbers with low and hot pixels. Note that these methods can
 * rapidly fill the entire heap, they are just meant to provide simple datasets got the unit tests. Saving
 * the images is very slow using ImgSaver. 
 * 
 * needs to be refactored
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
	//public final static double HOTPIX_BASELINE = 1228.587;
	//public final static double HOTPIX_DCPERSEC = 50709.523; 
	//public final static double HOTPIX_RNSQ = 16926.469; 
	//public final static double HOTPIX_TNSQPERSEC = 348342.688;

	// made it smaller to avoid overflows
	public final static double HOTPIX_BASELINE = 1228.587;
	public final static double HOTPIX_DCPERSEC = 10709.523; 
	public final static double HOTPIX_RNSQ = 1626.469; 
	public final static double HOTPIX_TNSQPERSEC = 11342.688;
	
	
/*
	public static void generateGroundTruth(String path, int width, int height, boolean hotpix) {

		final long[] dim = new long[] { width, height};
		final ImgFactory<DoubleType> factory = new ArrayImgFactory<DoubleType>(new DoubleType());

		Img<DoubleType> baseline = factory.create(dim);
		Img<DoubleType> dcpersec = factory.create(dim);
		Img<DoubleType> rnsq = factory.create(dim);
		Img<DoubleType> tnsqpersec = factory.create(dim);
		Img<DoubleType> gain = factory.create(dim);

		Cursor<DoubleType> c_baseline = baseline.localizingCursor();
		RandomAccess<DoubleType> r_dcpersec = dcpersec.randomAccess();
		RandomAccess<DoubleType> r_rnsq = rnsq.randomAccess();
		RandomAccess<DoubleType> r_tnsqpersec = tnsqpersec.randomAccess();
		RandomAccess<DoubleType> r_gain = gain.randomAccess();

		while(c_baseline.hasNext()) {
			DoubleType t = c_baseline.next();
			r_dcpersec.setPosition(c_baseline);
			r_rnsq.setPosition(c_baseline);
			r_tnsqpersec.setPosition(c_baseline);
			r_gain.setPosition(c_baseline);
			
			long x = c_baseline.getIntPosition(0), y = c_baseline.getIntPosition(1);
			if(hotpix) {
				if (x % 10 == 0 && y % 20 == 0) {
					t.set(HOTPIX_BASELINE);
					r_dcpersec.get().set(HOTPIX_DCPERSEC);
					r_rnsq.get().set(HOTPIX_RNSQ);
					r_tnsqpersec.get().set(HOTPIX_TNSQPERSEC);
					r_gain.get().set(HOTPIX_TNSQPERSEC/HOTPIX_DCPERSEC);
				} else {
					t.set(LOWPIX_BASELINE);
					r_dcpersec.get().set(LOWPIX_DCPERSEC);
					r_rnsq.get().set(LOWPIX_RNSQ);
					r_tnsqpersec.get().set(LOWPIX_TNSQPERSEC);
					r_gain.get().set(LOWPIX_TNSQPERSEC/LOWPIX_DCPERSEC);
				}
			} else {
				if (x % 10 == 0 && y % 20 == 0) {
					t.set(LOWPIX_BASELINE);
					r_dcpersec.get().set(LOWPIX_DCPERSEC);
					r_rnsq.get().set(LOWPIX_RNSQ);
					r_tnsqpersec.get().set(LOWPIX_TNSQPERSEC);
					r_gain.get().set(LOWPIX_TNSQPERSEC/LOWPIX_DCPERSEC);
				} else {
					t.set(DIMPIX_BASELINE);
					r_dcpersec.get().set(DIMPIX_DCPERSEC);
					r_rnsq.get().set(DIMPIX_RNSQ);
					r_tnsqpersec.get().set(DIMPIX_TNSQPERSEC);
					r_gain.get().set(DIMPIX_TNSQPERSEC/DIMPIX_DCPERSEC);
				}
			}
		}
		
		ImgSaver saver = new ImgSaver();
		if(hotpix) {
			saver.saveImg(path+"\\hot_baseline.tiff", baseline);
			saver.saveImg(path+"\\hot_dcpersec.tiff", dcpersec);
			saver.saveImg(path+"\\hot_rnsq.tiff", rnsq);
			saver.saveImg(path+"\\hot_tnsqpersec.tiff", tnsqpersec);
			saver.saveImg(path+"\\hot_gain.tiff", gain);
		} else {
			saver.saveImg(path+"\\baseline.tiff", baseline);
			saver.saveImg(path+"\\dcpersec.tiff", dcpersec);
			saver.saveImg(path+"\\rnsq.tiff", rnsq);
			saver.saveImg(path+"\\tnsqpersec.tiff", tnsqpersec);
			saver.saveImg(path+"\\gain.tiff", gain);
		}
	}
	
	public static void generateAvgVar(String path, int width, int height, double exposure, boolean hotpix) {

		final long[] dim = new long[] { width, height};
		final ImgFactory<DoubleType> factory = new ArrayImgFactory<DoubleType>(new DoubleType());

		Img<DoubleType> avg = factory.create(dim);
		Img<DoubleType> var = factory.create(dim);

		Cursor<DoubleType> c_avg = avg.localizingCursor();
		RandomAccess<DoubleType> r_var = var.randomAccess();

		while(c_avg.hasNext()) {
			DoubleType t = c_avg.next();
			r_var.setPosition(c_avg);
			
			long x = c_avg.getIntPosition(0), y = c_avg.getIntPosition(1);
			if(hotpix) {
				if (x % 10 == 0 && y % 20 == 0) {
					t.set(getHotPixAverage(exposure));
					r_var.get().set(getHotPixVariance(exposure));
				} else {
					t.set(getLowPixAverage(exposure));
					r_var.get().set(getLowPixVariance(exposure));
				}
			} else {
				if (x % 10 == 0 && y % 20 == 0) {
					t.set(getLowPixAverage(exposure));
					r_var.get().set(getLowPixVariance(exposure));
				} else {
					t.set(getDimPixAverage(exposure));
					r_var.get().set(getDimPixVariance(exposure));
				}
			}
		}
		
		ImgSaver saver = new ImgSaver();
		if(hotpix) {
			saver.saveImg(path+"\\hot_avg_"+exposure+"ms.tiff", avg);
			saver.saveImg(path+"\\hot_var_"+exposure+"ms.tiff", var);
		} else {
			saver.saveImg(path+"\\avg_"+exposure+"ms.tiff", avg);
			saver.saveImg(path+"\\var_"+exposure+"ms.tiff", var);
		}
	}*/
	
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
	
	public static Img<UnsignedIntType> generateUnsignedIntType(int width, int height, int numFrames,
			double exposure) {

		final long[] dim = new long[] { width, height, numFrames };
		final ImgFactory<UnsignedIntType> factory = new ArrayImgFactory<UnsignedIntType>(new UnsignedIntType());
		Img<UnsignedIntType> img = factory.create(dim);

		double hot_avg = getHotPixAverage(exposure);
		double hot_var = getHotPixVariance(exposure);
		double low_avg = getLowPixAverage(exposure);
		double low_var = getLowPixVariance(exposure);

		RandomAccess<UnsignedIntType> r = img.randomAccess();
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
	
	public static ArrayList<Img<UnsignedIntType>> generateUnsignedIntTypeSingles(int width, int height, int numFrames,
			double exposure) {

		final long[] dim = new long[] { width, height, 1 };
		final ImgFactory<UnsignedIntType> factory = new ArrayImgFactory<UnsignedIntType>(new UnsignedIntType());
		ArrayList<Img<UnsignedIntType>> img = new ArrayList<Img<UnsignedIntType>>();
		ArrayList<RandomAccess<UnsignedIntType>> r_arr = new ArrayList<RandomAccess<UnsignedIntType>>();

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
	
	public static void generateAndWriteToDisk(String path, int width, int height, int numFrames, double[] exposure, boolean writeStacks, RealType<?> type) {
		for (double e : exposure) {
			
			if(type.getBitsPerPixel() == 16) {
				if(writeStacks) {			
					System.out.println(e+"ms: writing stack");
					Img<UnsignedShortType> img_s = generateUnsignedShortType(width, height, numFrames, e);
					String name_s = path + "\\" + e + "ms_unshort.tif";

					ImagePlus i = ImageJFunctions.wrap(img_s, e+"ms");
					IJ.saveAsTiff(i, name_s);	

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

						IJ.saveAs(ImageJFunctions.wrap( img_f.get(i), "ij1"), "Tiff", s);	
						
						if(i%1000 == 0) {
							System.out.println(e+"ms: writing image "+i);
						}
					}
				}
			} else if(type.getBitsPerPixel() == 8) {
				if(writeStacks) {
					System.out.println(e+"ms: writing stack");
					Img<UnsignedByteType> img_b = generateUnsignedByteType(width, height, numFrames, e);
					String name_b = path + "\\" + e + "ms_unbyte.tif";
					
					IJ.saveAs(ImageJFunctions.wrap(img_b, "ij1"), "Tiff", name_b);	
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
						if(i%1000 == 0) {
							System.out.println(e+"ms: writing image "+i);
						}
						
						int num = String.valueOf(i).length();
						String s = name_file+"single_byte_";
						for(int k=0; k<numZeros-num;k++) {
							s=s+"0";
						}
						s = s+i+".tif";

						IJ.saveAs(ImageJFunctions.wrap( img_f.get(i), "ij1"), "Tiff", s);	
						
						
					}
				}
			} else if(type.getBitsPerPixel() == 32 && type instanceof UnsignedIntType) {
				if(writeStacks) {
					System.out.println(e+"ms: writing stack");
					Img<UnsignedIntType> img_f = generateUnsignedIntType(width, height, numFrames, e);
					String name_f = path + "\\" + e + "ms_unint.tif";

					IJ.saveAs(ImageJFunctions.wrap(img_f, "ij1"), "Tiff", name_f);	
				} else {
					ArrayList<Img<UnsignedIntType>> img_f = generateUnsignedIntTypeSingles(width, height, numFrames, e);	
					String name_f = path + "\\" + e + "ms_unint";
					File f = new File(name_f);
					if(!f.exists()) {
						f.mkdir();
					}

					String name_file = name_f + "\\";
					int numZeros = String.valueOf(numFrames).length();
					for(int i=0;i<img_f.size();i++) {
						int num = String.valueOf(i).length();
						String s = name_file+"single_unint_";
						for(int k=0; k<numZeros-num;k++) {
							s=s+"0";
						}
						s = s+i+".tif";

						IJ.saveAs(ImageJFunctions.wrap( img_f.get(i), "ij1"), "Tiff", s);	
						
						if(i%1000 == 0) {
							System.out.println(e+"ms: writing image "+i);
						}
					}
				}
			} else {
				if (writeStacks) {
					System.out.println(e+"ms: writing stack");
					Img<FloatType> img_f = generateFloatType(width, height, numFrames, e);
					String name_f = path + "\\" + e + "ms_float.tif";

					IJ.saveAs(ImageJFunctions.wrap(img_f, "ij1"), "Tiff", name_f);		
				} else {
					ArrayList<Img<FloatType>> img_f = generateFloatTypeSingles(width, height, numFrames, e);
					String name_f = path + "\\" + e + "ms_float";
					File f = new File(name_f);
					if (!f.exists()) {
						f.mkdir();
					}

					String name_file = name_f + "\\";
					int numZeros = String.valueOf(numFrames).length();
					for (int i = 0; i < img_f.size(); i++) {
						int num = String.valueOf(i).length();
						String s = name_file + "single_float_";
						for (int k = 0; k < numZeros - num; k++) {
							s = s + "0";
						}
						s = s + i + ".tif";

						IJ.saveAs(ImageJFunctions.wrap( img_f.get(i), "ij1"), "Tiff", s);	
						
						if(i%1000 == 0) {
							System.out.println(e+"ms: writing image "+i);
						}
					}
				} 
			} 
		}
	}
	
	public static void generateAndWriteToDisk(String path, int width, int height, int numFrames, double e,
			boolean writeStacks, RealType<?> type) {
		generateAndWriteToDisk(path, width, height, numFrames, new double[] {e}, writeStacks, type);
	}

}
