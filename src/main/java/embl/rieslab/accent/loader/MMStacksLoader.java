package main.java.embl.rieslab.accent.loader;

import java.io.IOException;

import org.micromanager.Studio;
import org.micromanager.data.Coords;
import org.micromanager.data.Datastore;
import org.micromanager.data.Image;
import org.micromanager.data.internal.DefaultCoords;

import main.java.embl.rieslab.accent.data.images.ImageExposurePair;
import main.java.embl.rieslab.accent.utils.utils;

public class MMStacksLoader implements Loader<ImageExposurePair>{

	private Studio studio;
	private String[] directories;
	private int currentDirectory, currentPlane, currentExposure;
	private Datastore store;
	
	public MMStacksLoader(Studio studio, String[] directories) {
		this.studio = studio;
		this.directories = directories;
		currentDirectory = -1;
		currentPlane = 0;
	}

	@Override
	public boolean isDone() {
		return (currentDirectory == directories.length-1 && currentPlane == store.getNumImages());
	}

	@Override
	public ImageExposurePair getNext(int channel) {
		Coords.CoordsBuilder builder = new DefaultCoords.Builder();
		builder.channel(0).z(0).stagePosition(0).time(currentPlane++);

		try {
			Image im = store.getImage(builder.build());
			
			if(currentPlane == store.getNumImages()) {
				store.close();
			}
			
			return new ImageExposurePair(im, currentExposure);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	public boolean hasNext(int channel) {
		if(channel == currentDirectory) {
			return (currentPlane < store.getNumImages());
		}
		return false;
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
		if(channel == currentDirectory+1) {
			currentDirectory = channel;
			currentPlane = 0;
			
			try {
				if(currentDirectory > 0) {
					// close previous store
					store.close();
				}
				store = studio.data().loadData(directories[currentDirectory], true);
				currentExposure = utils.extractExposurefromFolderName(directories[currentDirectory]);

			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
			
			return true;
		}
		return false;
	}

	public int getCurrentChannel() {
		return currentDirectory;
	}

	@Override
	public void close() {
		try {
			store.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int getChannelLength() {
		return store.getNumImages();
	}
}
