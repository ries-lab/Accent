package main.java.embl.rieslab.photonfreecamcalib.loader;

import main.java.embl.rieslab.photonfreecamcalib.data.FloatImage;

public interface Loader {

	public boolean hasNext();
	
	public FloatImage getNext();
}
