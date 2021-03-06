package de.embl.rieslab.accent.mm2.loader;

import java.io.IOException;

import org.micromanager.Studio;
import org.micromanager.data.Coords;
import org.micromanager.data.Datastore;
import org.micromanager.data.Image;
import org.micromanager.data.internal.DefaultCoords;

import de.embl.rieslab.accent.common.interfaces.pipeline.Loader;
import de.embl.rieslab.accent.common.utils.AccentUtils;
import de.embl.rieslab.accent.mm2.data.image.BareImage;

public class MMStacksLoader implements Loader<BareImage>{

	private Studio studio;
	private String[] directories;
	private int currentDirectory, currentPlane;
	private double currentExposure;
	private Datastore store;
	
	public MMStacksLoader(Studio studio, String[] directories) {
		this.studio = studio;
		this.directories = directories;
		currentDirectory = -1;
		currentPlane = 0;
	}

	@Override
	public BareImage getNext(int channel) {
		Coords.CoordsBuilder builder = new DefaultCoords.Builder();
		builder.channel(0).z(0).stagePosition(0).time(currentPlane++);

		try {
			Image im = store.getImage(builder.build());
			
			if(currentPlane == store.getNumImages()) {
				store.close();
			}
			
			return new BareImage(im.getBytesPerPixel(), im.getRawPixels(), im.getWidth(), im.getHeight(), currentExposure);
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
	public int getNumberOfChannels() {
		return directories.length;
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
				currentExposure = AccentUtils.extractExposureMs(directories[currentDirectory]);

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
	public int getChannelLength() {
		return store.getNumImages();
	}
}
