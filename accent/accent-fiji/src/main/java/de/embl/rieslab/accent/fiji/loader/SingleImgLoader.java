package de.embl.rieslab.accent.fiji.loader;

import java.io.File;
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
	private boolean openedAll_;
	private double[] mapping_;
	private DatasetIOService ioservice_;
	
	
	public SingleImgLoader(DatasetIOService ioservice, Map<Double, String> folders) {
		// sanity check
		folders_ = folders.entrySet()
					.stream()
					.filter(e -> (new File(e.getValue()).isDirectory()))
					.collect(Collectors.toMap(Entry::getKey,Entry::getValue));
		
		ioservice_ = ioservice;
		
		mapping_ = new double[folders_.size()];
		int i = 0;
		for(Entry<Double, String> e: folders_.entrySet()) {
			mapping_[i] = e.getKey();
			i++;
		}

		currIndex_ = 0;
		openedAll_ = false;
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
		
		if(openedAll_) { // first image opened all as a series
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
	
		if(hasNext(channel)) {
			try {
				img = ioservice_.open(currImgs_.get(currIndex_));
			} catch (IOException e) {
				e.printStackTrace();
			}
		
			if(img != null) { // something was read
				int temp = (int) img.dimension(2);
				if (currIndex_ == 0 && temp == currImgs_.size()) { // first time we load an image from this folder
					// if the dimension equals the number of images to load,
					// we assume that it has loaded all the images.
					openedAll_ = true;
				}
				currIndex_ ++;
			}
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
					.collect(Collectors.toList());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		if(c != null && !c.isEmpty()) {
			currImgs_.clear();
			currImgs_.addAll(c);
			currIndex_ = 0;
			openedAll_ = false; // back to default
			return true;
		}
		
		return false;
	}

	@Override
	public int getChannelLength() {
		return currImgs_.size();
	}

}