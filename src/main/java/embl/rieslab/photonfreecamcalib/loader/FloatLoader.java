package main.java.embl.rieslab.photonfreecamcalib.loader;

import java.io.IOException;

import org.micromanager.Studio;
import org.micromanager.data.Coords;
import org.micromanager.data.Datastore;
import org.micromanager.data.internal.DefaultCoords;

import ij.process.ImageProcessor;
import main.java.embl.rieslab.photonfreecamcalib.data.FloatImage;

public class FloatLoader implements Loader {

	private Studio studio;
	private final int exposure;
	private Datastore store;
	private int stackSize;
	
	private int stackPosition;
	
	public FloatLoader(Studio studio, int exposure, String folder) {
		this.studio = studio;
		this.exposure = exposure;
		
		stackPosition = 0;
		
		try {
			store = studio.data().loadData(folder, true);
			stackSize = store.getNumImages();
		} catch (IOException e) {
			stackSize = 0;
			e.printStackTrace();
		}
	}

	@Override
	public boolean hasNext() {
		return stackPosition < stackSize;
	}

	@Override
	public FloatImage getNext() {
		if(hasNext()) {
			Coords.CoordsBuilder builder = new DefaultCoords.Builder();
			builder.channel(0).z(stackPosition).stagePosition(0).time(0);
			try {
				ImageProcessor improc = studio.data().ij().createProcessor(store.getImage(builder.build()));
				stackPosition ++;
				return new FloatImage(improc.getWidth(), improc.getHeight(), improc.getFloatArray(), exposure);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}
