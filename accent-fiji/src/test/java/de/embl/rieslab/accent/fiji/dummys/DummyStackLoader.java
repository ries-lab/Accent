package de.embl.rieslab.accent.fiji.dummys;

import de.embl.rieslab.accent.common.interfaces.pipeline.Loader;
import de.embl.rieslab.accent.fiji.data.image.StackImg;
import de.embl.rieslab.accent.fiji.datagen.GenerateData;
import net.imagej.DatasetService;
import net.imagej.ImageJ;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.integer.UnsignedIntType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.type.numeric.real.FloatType;

public class DummyStackLoader implements Loader<StackImg> {

	private int width, height, numFrames;
	private RealType<?> type;
	private double[] exposures;
	private boolean[] loaded;
	private int currChannel;
	private StackImg currImg;
	private DatasetService datasetService;
	
	public DummyStackLoader(int width, int height, int numFrames, double[] exposures, RealType<?> type) {
		this.width = width;
		this.height = height;
		this.numFrames = numFrames;
		this.type = type;
		this.exposures = exposures;
	
		loaded = new boolean[exposures.length];
		for(int i=0;i<exposures.length;i++) {
			loaded[i] = false;
		}
		
		ImageJ ij = new ImageJ();
		datasetService = ij.dataset();
	}
	
	@Override
	public StackImg getNext(int channel) {
		if(channel == currChannel && !loaded[currChannel]) {
			loaded[currChannel] = true;
			return currImg;
		}
		return null;
	}

	@Override
	public boolean hasNext(int channel) {
		if(channel < 0 && channel >= exposures.length) {
			return false;
		}
		return !loaded[channel];
	}

	@Override
	public int getNumberOfChannels() {
		return exposures.length;
	}

	@Override
	public boolean openChannel(int channel) {
		if(channel < 0 && channel >= exposures.length) {
			return false;
		}
		
		currChannel = channel;
		
		if(type.getBitsPerPixel() == 8) {
			Img<UnsignedByteType> img = GenerateData.generateUnsignedByteType(width, height, numFrames, exposures[channel]);
			currImg = new StackImg(datasetService.create(img), exposures[channel]);
		} else if(type.getBitsPerPixel() == 16) {
			Img<UnsignedShortType> img = GenerateData.generateUnsignedShortType(width, height, numFrames, exposures[channel]);
			currImg = new StackImg(datasetService.create(img), exposures[channel]);
		} else if(type.getBitsPerPixel() == 32 && type instanceof UnsignedIntType){
			Img<UnsignedIntType> img = GenerateData.generateUnsignedIntType(width, height, numFrames, exposures[channel]);
			currImg = new StackImg(datasetService.create(img), exposures[channel]);
		} else if(type.getBitsPerPixel() == 32 && type instanceof FloatType){
			Img<FloatType> img = GenerateData.generateFloatType(width, height, numFrames, exposures[channel]);
			currImg = new StackImg(datasetService.create(img), exposures[channel]);
		} else {
			return false;
		}
	
		return true;
	}

	@Override
	public int getChannelLength() {
		return numFrames;
	}
	
}