package main.java.embl.rieslab.accent.acquisition;

import org.micromanager.data.Datastore;

import main.java.embl.rieslab.accent.data.SimpleRoi;

public class AcquisitionSettings {

	public String name_ = null;
	
	public String folder_ = null;
	
	public int numFrames_ = 20000;
	
	public Integer[] exposures_ = {10,20,300};
	
	public SimpleRoi roi_ = null;
	
	public Datastore.SaveMode saveMode_ = Datastore.SaveMode.MULTIPAGE_TIFF;
	
	public boolean parallelProcessing = true;

	public int preRunTime_= 0;
		
}
