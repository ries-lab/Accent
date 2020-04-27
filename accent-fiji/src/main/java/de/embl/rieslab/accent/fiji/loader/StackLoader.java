package de.embl.rieslab.accent.fiji.loader;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.scijava.log.LogService;

import de.embl.rieslab.accent.common.interfaces.pipeline.Loader;
import de.embl.rieslab.accent.fiji.data.image.StackImg;
import io.scif.services.DatasetIOService;
import net.imagej.Dataset;

public class StackLoader implements Loader<StackImg> {

	private Map<Double, String> stacks_;
	private double[] mapping_;
	private boolean[] polled_;
	private Dataset currImg_;
	private DatasetIOService ioservice_;
	private LogService logservice_;
	private long currSize_;
	
	public StackLoader(DatasetIOService ioservice, LogService logservice, Map<Double, String> stacks) {
		stacks_ = stacks;
		ioservice_ = ioservice;
		logservice_ = logservice;
		
		polled_ = new boolean[stacks_.size()];
		mapping_ = new double[stacks_.size()];
		
		int i = 0;
		for(Entry<Double, String> e: stacks_.entrySet()) {
			polled_[i] = false;
			mapping_[i] = e.getKey();
			i++;
		}
		currSize_ = 0;
	}
	
	@Override
	public StackImg getNext(int channel) {
		if(!(channel >= stacks_.size() || channel < 0) && !polled_[channel]) {
			polled_[channel] = true;
			return new StackImg(currImg_, mapping_[channel]);
		}
		return null;
	}

	@Override
	public boolean hasNext(int channel) {
		if(channel >= stacks_.size() || channel < 0) {
			return false;
		}
		return !polled_[channel];
	}

	@Override
	public int getNumberOfChannels() {
		return stacks_.size();
	}

	private Dataset load(int channel) {
		Dataset img = null;
		if(ioservice_.canOpen(stacks_.get(mapping_[channel]))) {
			try {
				img = ioservice_.open(stacks_.get(mapping_[channel]));
				
				// we assume only 3 dimensions with depth > 1 and we look for the third dimension value
				long max = 1;
				if(img.numDimensions() > 2) { // identify the dimension with the largest depth, ignoring X,Y
					for(int i=2;i<img.numDimensions();i++) {
						if(img.dimension(i) > max) {
							max = img.dimension(i);
						}
					}
				}
				currSize_ = max;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return img;
	}
	
	@Override
	public boolean openChannel(int channel) {
		if(channel >= stacks_.size() || channel < 0) {
			return false;
		}
		if(!polled_[channel]) {
			Dataset img = load(channel);
			
			if(img != null) {
				currImg_ = img;
				return true;
			}
		} 
			
		return false;
	}

	@Override
	public int getChannelLength() {
		return (int) currSize_;
	}

}
