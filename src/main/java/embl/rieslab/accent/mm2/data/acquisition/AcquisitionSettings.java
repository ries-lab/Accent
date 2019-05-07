package main.java.embl.rieslab.accent.mm2.data.acquisition;


import main.java.embl.rieslab.accent.mm2.data.roi.SimpleRoi;

public class AcquisitionSettings {

	public String name_ = null;
	
	public String folder_ = null;
	
	public int numFrames_ = 20000;
	
	public Integer[] exposures_ = {10,20,300};
	
	public SimpleRoi roi_ = null;
	
	public boolean saveAsStacks_ = true;
	
	public boolean parallelProcessing = true;

	public int preRunTime_= 0;
		
}
