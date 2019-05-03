package main.java.embl.rieslab.accent.loader;

import ij.IJ;
import ij.ImageStack;
import main.java.embl.rieslab.accent.data.ImageProcessorExposurePair;
import main.java.embl.rieslab.accent.utils.utils;

public class IJLoader implements Loader<ImageProcessorExposurePair>{

	private String[] directories;
	private int currentDirectory, currentPlane, currentExposure;
	private ImageStack image;
	
	public IJLoader(String[] directories) {
		this.directories = directories;
		currentDirectory = 0;
		currentPlane = 1;
	}
	
	@Override
	public ImageProcessorExposurePair getNext(int channel) {
		if(currentPlane < image.getSize()+1) {
			return new ImageProcessorExposurePair(image.getProcessor(currentPlane++),currentExposure);
		}
		return null;
	}

	@Override
	public boolean hasNext(int channel) {
		if(channel == currentDirectory) {
			return (currentPlane < image.getSize()+1);
		}
		return false;
	}

	@Override
	public boolean isDone() {
		return (currentDirectory == directories.length-1 && currentPlane == image.getSize());
	}

	@Override
	public void close() {
		// do nothing
	}

	@Override
	public int getSize() {
		return directories.length;
	}

	@Override
	public boolean isOpen(int channel) {
		if(channel == currentDirectory) {
			return true;
		}
		return false;
	}

	@Override
	public boolean openChannel(int channel) {
		if(channel < directories.length && channel == currentDirectory+1) {

			image = IJ.openVirtual(directories[channel]).getImageStack();
			currentExposure = utils.extractExposurefromFolderName(directories[currentDirectory]);

			currentDirectory = channel;
			currentPlane = 1; // imagej starts counting planes at 1
			return true;
		}
		return false;
	}

	@Override
	public int getChannelLength() {
		return image.getSize();
	}

}
