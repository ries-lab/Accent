package de.embl.rieslab.accent.common.dummys;

import de.embl.rieslab.accent.common.data.image.BareImage;
import de.embl.rieslab.accent.common.data.image.FloatImage;
import de.embl.rieslab.accent.common.interfaces.Loader;

public class DummyLoader implements Loader {

	public int nChannels;
	public double[] exposures;
	public FloatImage[] avgs, vars;
	
	public double baseline = 3.5;
	public double dc_per_sec = 1.8; 
	public double rn_sq = 2.4; 
	public double tn_sq_per_sec = 1.8;
	
	int curr_chan;
	int curr_im;
	
	public DummyLoader(int nChannels) {
		this.nChannels = nChannels;
		curr_chan = 0;
		curr_im = 0;
	
		exposures = new double[nChannels];
		for(int i = 0; i< nChannels; i++)
			exposures[i] = 10+500.5*i;
			
		int width = 2;
		int height = 3;
		
		avgs = new FloatImage[nChannels];
		vars = new FloatImage[nChannels];
		for(int i =0; i< nChannels; i++) {
			float[] f_avg = new float[width*height];
			float[] f_var = new float[width*height];
			for(int y = 0; y<height; y++) {
				for(int x = 0; x<width; x++) {
					int p = x+width*y;
					f_avg[p] = (float) (baseline+dc_per_sec*exposures[i]/1000.);
					f_var[p] = (float) (rn_sq+tn_sq_per_sec*exposures[i]/1000.);
				}
			}
			avgs[i] = new FloatImage(width, height, f_avg, exposures[i]);
			vars[i] = new FloatImage(width, height, f_var, exposures[i]);
		}
	}
	
	@Override
	public BareImage getNext(int channel) {
		return null;
	}

	@Override
	public boolean hasNext(int channel) {
		return false;
	}

	@Override
	public boolean isDone() {
		return false;
	}

	@Override
	public void close() {}

	@Override
	public int getNumberOfChannels() {
		return nChannels;
	}

	@Override
	public boolean isOpen(int channel) {
		return false;
	}

	@Override
	public boolean openChannel(int channel) {
		return false;
	}

	@Override
	public int getChannelLength() {
		return 0;
	}

}
