package de.embl.rieslab.accent.mm2.dummys;

import java.util.concurrent.ThreadLocalRandom;

import de.embl.rieslab.accent.common.interfaces.pipeline.Loader;
import de.embl.rieslab.accent.mm2.data.image.BareImage;

public class DummyMM2Loader implements Loader<BareImage> {

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
	
	public double[] exposures;
	public int numFrames;
	public int[] curr_ind;
	public int curr_channel;
	public int width, height, bytesPerPixels;
	
	public DummyMM2Loader(int bytesPerPixels, int width, int height, int numFrames, double[] exposures) {
		this.exposures = exposures;
		this.numFrames = numFrames;
		this.width = width;
		this.height = height;
		this.bytesPerPixels = bytesPerPixels;
		
		curr_ind = new int[exposures.length];
		for(int i=0;i<exposures.length;i++)
			curr_ind[i] = 0;

		curr_channel = 0;
	}
	
	@Override
	public BareImage getNext(int channel) {
		if(curr_ind[curr_channel] < numFrames) {
			Object pix;
			
			if(bytesPerPixels == 1) {
				double dim_avg = getDimPixAverage(exposures[channel]);
				double dim_var = getDimPixVariance(exposures[channel]);
				double low_avg = getLowPixAverage(exposures[channel]);
				double low_var = getLowPixVariance(exposures[channel]);
				
				pix = new byte[width*height];
				for (int x = 0; x < width; x++) {
					for (int y = 0; y < height; y++) {
						int p = y*width+x;
						
						if (x % 10 == 0 && y % 20 == 0) {
							((byte[]) pix)[p] = (byte) generateGaussianDistributedValues(low_avg, low_var);
						} else {
							((byte[]) pix)[p] = (byte) generateGaussianDistributedValues(dim_avg, dim_var);
						}
					}
				}
			} else if(bytesPerPixels == 2) {
				double hot_avg = getHotPixAverage(exposures[channel]);
				double hot_var = getHotPixVariance(exposures[channel]);
				double low_avg = getLowPixAverage(exposures[channel]);
				double low_var = getLowPixVariance(exposures[channel]);
				
				pix = new short[width*height];
				for (int x = 0; x < width; x++) {
					for (int y = 0; y < height; y++) {
						int p = y*width+x;
						
						if (x % 10 == 0 && y % 20 == 0) {
							((short[]) pix)[p] = (short) generateGaussianDistributedValues(hot_avg, hot_var);
						} else {
							((short[]) pix)[p] = (short) generateGaussianDistributedValues(low_avg, low_var);
						}
					}
				}
			} else {
				double hot_avg = getHotPixAverage(exposures[channel]);
				double hot_var = getHotPixVariance(exposures[channel]);
				double low_avg = getLowPixAverage(exposures[channel]);
				double low_var = getLowPixVariance(exposures[channel]);
				
				pix = new int[width*height];
				for (int x = 0; x < width; x++) {
					for (int y = 0; y < height; y++) {
						int p = y*width+x;
						
						if (x % 10 == 0 && y % 20 == 0) {
							((int[]) pix)[p] = (int) generateGaussianDistributedValues(hot_avg, hot_var);
						} else {
							((int[]) pix)[p] = (int) generateGaussianDistributedValues(low_avg, low_var);
						}
					}
				}
			}
			
			curr_ind[curr_channel]++;
			return new BareImage(bytesPerPixels, pix, width, height, exposures[curr_channel]);
		} else {
			return null;
		}
	}

	@Override
	public boolean hasNext(int channel) {
		if(channel < numFrames)
			return curr_ind[channel] < numFrames ? true:false;
		return false;
	}

	@Override
	public int getNumberOfChannels() {
		return exposures.length;
	}
	
	@Override
	public boolean openChannel(int channel) {
		if(channel < 0 || channel >= numFrames)
			return false;
		
		curr_channel = channel;
		return true;
	}

	@Override
	public int getChannelLength() {
		return numFrames;
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
	
	public static double generateGaussianDistributedValues(double mean, double variance) {
		ThreadLocalRandom generator = ThreadLocalRandom.current();
		double std = Math.sqrt(variance);
		
		return  mean + generator.nextGaussian() * std;
	}
}
