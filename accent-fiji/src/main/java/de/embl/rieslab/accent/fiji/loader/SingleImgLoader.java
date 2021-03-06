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

import org.scijava.log.LogService;

import de.embl.rieslab.accent.common.interfaces.pipeline.Loader;
import de.embl.rieslab.accent.fiji.data.image.StackImg;
import io.scif.services.DatasetIOService;
import net.imagej.Dataset;

// should be renamed, the difference in the two loaders is not single vs stack, but stack vs folders...
public class SingleImgLoader implements Loader<StackImg>{

	private Map<Double, String> folders_;
	private List<String> currImgs_;
	private int currIndex_;
	private boolean openedAll_;
	private double[] mapping_;
	private DatasetIOService ioservice_;
	private LogService logservice_;
	private Dataset currImg;
	private long currSize_;
	
	public SingleImgLoader(DatasetIOService ioservice, LogService logservice, Map<Double, String> folders) {
		// sanity check
		folders_ = folders.entrySet()
					.stream()
					.filter(e -> (new File(e.getValue()).isDirectory()))
					.collect(Collectors.toMap(Entry::getKey,Entry::getValue));
		
		ioservice_ = ioservice;
		logservice_ = logservice;
		
		mapping_ = new double[folders_.size()];
		int i = 0;
		for(Entry<Double, String> e: folders_.entrySet()) {
			mapping_[i] = e.getKey();
			i++;
		}
		currSize_ = 0;
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
		if(hasNext(channel)) {
			currImg = null;
			try {
				currImg = ioservice_.open(currImgs_.get(currIndex_));
			} catch (IOException e) {
				e.printStackTrace();
			}
		
			if(currImg != null) { // something was read		
				// identify the third dimension: we assume there is at max three dimensions where dimSize > 1				
				long max = 1;
				if(currImg.numDimensions() > 2) { // identify the dimension with the largest depth, ignoring X,Y
					for(int i=2;i<currImg.numDimensions();i++) {
						if(currImg.dimension(i) > max) {
							max = currImg.dimension(i);
						}
					}
				}
				
				if (currIndex_ == 0 && max > 1) { // first time we load an image from this folder
					// if the dimension is not one, we assume that it has loaded all the images.
					// This means that in the case of multi-stacks not correctly loaded, only the
					// first stack will be considered.
					openedAll_ = true;
					currSize_ = max;
				}
				currIndex_ ++;
			}
		}
		return currImg;
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
		return openedAll_ ? (int) currSize_ : currImgs_.size();
	}

}