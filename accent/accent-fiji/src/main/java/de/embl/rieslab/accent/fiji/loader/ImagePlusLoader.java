package de.embl.rieslab.accent.fiji.loader;

import java.util.List;

import de.embl.rieslab.accent.common.data.image.BareImage;
import de.embl.rieslab.accent.common.interfaces.Loader;
import de.embl.rieslab.accent.fiji.data.image.ImagePlusDataset;
import ij.ImageStack;

public class ImagePlusLoader implements Loader{


	private List<ImagePlusDataset> list;
	private ImageStack currentDataset;
	private int currentPlane, currentFile, currentChannelLength, currentExposure;
	private BareImage.DataType type;
	
	public ImagePlusLoader(List<ImagePlusDataset> list) {
		this.list = list;
		currentPlane = 1;
		currentChannelLength = 0;
		currentExposure = 0;
		currentFile = -1;
		
		type = BareImage.DataType.FLOAT; // default
	}
	
	@Override
	public BareImage getNext(int channel) {
		if(channel == currentFile) {	
			return new BareImage(type, currentDataset.getPixels(currentPlane++), (int) currentDataset.getWidth(),
						(int) currentDataset.getHeight(), currentExposure);
		
		}
		return null;
	}

	@Override
	public boolean hasNext(int channel) {
		if(currentDataset != null) {
			return (currentPlane < getChannelLength()+1);
		}
		return false;
	}

	@Override
	public boolean isDone() {
		return (currentFile == getSize()-1 && currentPlane == getChannelLength());
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
			currentDataset = list.get(channel).getImage().getImageStack();
			
			// we only consider three types of images here but many more exist...
			if (currentDataset.getBitDepth() == 8) {
				type = BareImage.DataType.BYTE;
			} else if (currentDataset.getBitDepth() == 16) {
				type = BareImage.DataType.SHORT;
			} else if (currentDataset.getBitDepth() == 32) {
				type = BareImage.DataType.FLOAT;
			} else {
				throw new IllegalArgumentException("Unknown data type.");
			}
			
			currentChannelLength = (int) currentDataset.getSize();
			currentFile = channel;
			currentExposure = list.get(channel).getExposure();
			currentPlane = 1; // ij1 starts counting at 0
			return true;
		}
		return false;
	}

	@Override
	public int getChannelLength() {
		return currentChannelLength;
	}

}
