package de.embl.rieslab.accent.common.dummys;

import de.embl.rieslab.accent.common.interfaces.pipeline.Loader;

public class DummyLoader implements Loader<DummyImage>{
	public int nChannels;
	public double[] exposures;
	public DummyImage[] avgs, vars;
	
	public int numImages = 200;
	
	public double baseline = 3.5;
	public double dc_per_sec = 1.8; 
	public double rn_sq = 2.4; 
	public double tn_sq_per_sec = 1.8;
	
	public int[] curr_ind;
	public int curr_channel;
	
	public int width = 2;
	public int height = 3;
	
	public DummyLoader(int nChannels) {
		this.nChannels = nChannels;
		curr_ind = new int[nChannels];
		for(int i=0;i<nChannels;i++)
			curr_ind[i] = 0;

		curr_channel=0;
	
		exposures = new double[nChannels];
		for(int i = 0; i< nChannels; i++)
			exposures[i] = 10+500.5*i;
					
		avgs = new DummyImage[nChannels];
		vars = new DummyImage[nChannels];
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
			avgs[i] = new DummyImage(4, f_avg, width, height, exposures[i]);
			vars[i] = new DummyImage(4, f_var, width, height, exposures[i]);
		}	
	}
	
	@Override
	public DummyImage getNext(int channel) {
		float[] pix = new float[width*height];
		for(int y = 0; y<height; y++) {
			for(int x = 0; x<width; x++) {
				int p = x+width*y;
				double avg = (baseline+dc_per_sec*exposures[curr_channel]/1000.);
				double var = (rn_sq+tn_sq_per_sec*exposures[curr_channel]/1000.);
				
				pix[p] = (float) (avg+Math.sqrt(var)*Math.pow(-1, curr_ind[channel]));
			}
		}
		curr_ind[curr_channel]++;
		
		return new DummyImage(4, pix, width, height, exposures[curr_channel]);
	}

	@Override
	public boolean hasNext(int channel) {
		if(channel < nChannels)
			return curr_ind[channel] < 2 ? true:false;
		return false;
	}

	@Override
	public int getNumberOfChannels() {
		return nChannels;
	}
	@Override
	public boolean openChannel(int channel) {
		if(channel < 0 || channel >= nChannels)
			return false;
		
		curr_channel = channel;
		return true;
	}

	@Override
	public int getChannelLength() {
		return numImages;
	}
}
