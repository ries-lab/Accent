package main.java.embl.rieslab.accent.loader;

import java.util.List;

import main.java.embl.rieslab.accent.data.DatasetExposurePair;
import main.java.embl.rieslab.accent.data.FloatImage;

public class CurrentImgsLoader implements Loader<FloatImage>{

	private List<DatasetExposurePair> list;
	private DatasetExposurePair currentDataset;
	private int currentPlane, currentFile;
	
	
	public CurrentImgsLoader(List<DatasetExposurePair> list) {
		this.list = list;
		currentPlane = 0;
		currentFile = -1;
	}
	
	@Override
	public FloatImage getNext(int channel) {
		if(channel == currentFile) {
			String type = currentDataset.getImage().getTypeLabelShort();
			if (type.equals("8-bit uint")) {
				return new FloatImage((int) currentDataset.getImage().getWidth(),
						(int) currentDataset.getImage().getHeight(),
						(byte[]) currentDataset.getImage().getPlane(currentPlane++), currentDataset.getExposure());
			} else if (type.equals("16-bit uint")) {
				return new FloatImage((int) currentDataset.getImage().getWidth(),
						(int) currentDataset.getImage().getHeight(),
						(short[]) currentDataset.getImage().getPlane(currentPlane++), currentDataset.getExposure());
			} else if (type.equals("32-bit uint")) {
				return new FloatImage((int) currentDataset.getImage().getWidth(),
						(int) currentDataset.getImage().getHeight(),
						(float[]) currentDataset.getImage().getPlane(currentPlane++), currentDataset.getExposure());
			}
		}
		return null;
	}

	@Override
	public boolean hasNext(int channel) {
		if(currentDataset != null) {
			return (currentPlane < getChannelLength());
		}
		return false;
	}

	@Override
	public boolean isDone() {
		return (currentFile == getSize()-1 && currentPlane == getChannelLength()-1);
	}

	@Override
	public void close() {
		// do nothing
	}

	@Override
	public int getSize() {
		return list.size();
	}

	@Override
	public boolean isOpen(int channel) {
		if(channel == currentFile) {
			return true;
		}
		return false;
	}

	@Override
	public boolean openChannel(int channel) {
		if(channel == currentFile + 1) {
			currentDataset = list.get(channel);
			currentFile = channel;
			currentPlane = 0;
			return true;
		}
		return false;
	}

	@Override
	public int getChannelLength() {
		if(currentDataset != null) {
			return (int) currentDataset.getImage().getFrames();
		}
		return 0;
	}

}
