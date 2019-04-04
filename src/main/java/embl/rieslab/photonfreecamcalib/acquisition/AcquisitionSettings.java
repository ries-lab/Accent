package main.java.embl.rieslab.photonfreecamcalib.acquisition;

import org.micromanager.data.Datastore;

import ij.gui.Roi;

public class AcquisitionSettings {

	public String name_ = null;
	
	public String folder_ = null;
	
	public int numFrames_ = 20000;
	
	public Integer[] exposures_ = {10,20,300};
	
	public Roi roi_ = null;
	
	public Datastore.SaveMode saveMode_ = Datastore.SaveMode.MULTIPAGE_TIFF;
	
	public boolean multiplexedAcq = true;
	
	public boolean onlineAnalysis = true;
	
}
