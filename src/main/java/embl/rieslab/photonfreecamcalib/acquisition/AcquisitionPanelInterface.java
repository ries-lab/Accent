package main.java.embl.rieslab.photonfreecamcalib.acquisition;

import ij.gui.Roi;

public interface AcquisitionPanelInterface {

	public void setProgress(String progress, int percentage);
	
	public void acqHasStarted();
	
	public void acqHasStopped();
	
	public void acqHasEnded();
	
	public void setAdvancedSettings(int preRunTime, boolean alternatedAcquisition, boolean saveAsStacks, boolean parallelProcessing, Roi roi);
}
