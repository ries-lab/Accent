package de.embl.rieslab.accent.fiji.loader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import de.embl.rieslab.accent.common.interfaces.pipeline.Loader;
import de.embl.rieslab.accent.fiji.data.image.StackImg;
import io.scif.services.DatasetIOService;
import net.imagej.Dataset;

public class SingleImgLoader implements Loader<StackImg>{

	private Map<Double, String> folders_;
	private List<String> currImgs_;
	private int currIndex_;
	private double[] mapping_;
	private DatasetIOService ioservice_;
	
	public SingleImgLoader(DatasetIOService ioservice, Map<Double, String> folders) {
		folders_ = folders;
		ioservice_ = ioservice;
		
		mapping_ = new double[folders_.size()];
		
		int i = 0;
		for(Entry<Double, String> e: folders_.entrySet()) {
			mapping_[i] = e.getKey();
			i++;
		}
		
		currIndex_ = 0;
		currImgs_ = new ArrayList<String>();
	}
	
	@Override
	public StackImg getNext(int channel) {
		if(!(channel >= folders_.size() || channel < 0) && hasNext(channel)) {
			return new StackImg(loadNext(channel), mapping_[channel]);
		}
		return null;
	}

	@Override
	public boolean hasNext(int channel) {
		if(channel >= folders_.size() || channel < 0) {
			return false;
		}
		return currIndex_ < getChannelLength();
	}

	@Override
	public int getNumberOfChannels() {
		return folders_.size();
	}

	private Dataset loadNext(int channel) {
		Dataset img = null;
	
		try {
			img = ioservice_.open(currImgs_.get(currIndex_));
			currIndex_++;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return img;
	}
	
	@Override
	public boolean openChannel(int channel) {
		if(channel >= folders_.size() || channel < 0) {
			return false;
		}

		List<String> c = null;
		try {
			c = Files.list( Paths.get( folders_.get(mapping_[channel]) ) )
					.map(Path::toString)
					.filter(e -> e.endsWith(".tif") || e.endsWith(".tiff") || e.endsWith(".TIF") || e.endsWith(".TIFF"))
					.filter(e -> ioservice_.canOpen(e))
					.collect(Collectors.toList());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		if(c != null && !c.isEmpty()) {
			currImgs_.clear();
			currImgs_.addAll(c);
			currIndex_ = 0;
			return true;
		}
		
		return false;
	}

	@Override
	public int getChannelLength() {
		return currImgs_.size();
	}

}