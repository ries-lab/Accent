package de.embl.rieslab.accent.fiji.loader;

import java.util.List;

import de.embl.rieslab.accent.common.interfaces.pipeline.Loader;
import de.embl.rieslab.accent.fiji.data.image.FijiDataset;
import de.embl.rieslab.accent.mm2.data.image.BareImage;

@Deprecated
public class CurrentImgsLoader implements Loader{

	private List<FijiDataset> list;
	private FijiDataset currentDataset;
	private int currentPlane, currentFile, currentChannelLength;
	private BareImage.DataType type;
	
	public CurrentImgsLoader(List<FijiDataset> list) {
		this.list = list;
		currentPlane = 0;
		currentChannelLength = 0;
		currentFile = -1;
		
		type = BareImage.DataType.FLOAT; // default
	}
	
	@Override
	public BareImage getNext(int channel) {
		if(channel == currentFile) {	
			return new BareImage(type, currentDataset.getImage().getPlane(currentPlane++), (int) currentDataset.getImage().getWidth(),
						(int) currentDataset.getImage().getHeight(),currentDataset.getExposure());
		
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
	public int getNumberOfChannels() {
		return list.size();
	}

	@Override
	public boolean openChannel(int channel) {
		if(channel == currentFile + 1) {
			currentDataset = list.get(channel);
			
			// we only consider three types of images here but many more exist...
			if (currentDataset.getType().equals("8-bit uint")) {
				type = BareImage.DataType.BYTE;
			} else if (currentDataset.getType().equals("16-bit uint")) {
				type = BareImage.DataType.SHORT;
			} else if (currentDataset.getType().equals("32-bit uint")) {
				type = BareImage.DataType.FLOAT;
			} else {
				throw new IllegalArgumentException("Unknown data type.");
			}
			
			currentChannelLength = (int) currentDataset.getImage().getFrames();
			currentFile = channel;
			currentPlane = 0;
			return true;
		}
		return false;
	}

	@Override
	public int getChannelLength() {
		return currentChannelLength;
	}

}
