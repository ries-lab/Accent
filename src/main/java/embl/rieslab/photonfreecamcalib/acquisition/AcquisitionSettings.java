package main.java.embl.rieslab.photonfreecamcalib.acquisition;

import org.micromanager.data.Datastore;

import ij.gui.Roi;
import main.java.embl.rieslab.photonfreecamcalib.calibration.JacksonRoi;

public class AcquisitionSettings {

	public String name_ = null;
	
	public String folder_ = null;
	
	public int numFrames_ = 20000;
	
	public Integer[] exposures_ = {10,20,300};
	
	public Roi roi_ = null;
	
	public Datastore.SaveMode saveMode_ = Datastore.SaveMode.MULTIPAGE_TIFF;
	
	public boolean alternatedAcquisition_ = true;
	
	public boolean parallelProcessing = true;

	public int preRunTime_= 0;
	
	public JacksonRoi getRoi() {
		if(roi_ != null) {
			JacksonRoi roi = new JacksonRoi();
			roi.x0 = (int) roi_.getXBase();
			roi.y0 = (int) roi_.getYBase();
			roi.width = (int) roi_.getFloatWidth();
			roi.height = (int) roi_.getFloatHeight();
			return roi;
		}
		return null;
	}
	
}
