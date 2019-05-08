package main.java.embl.rieslab.accent.common.interfaces.ui;

import main.java.embl.rieslab.accent.common.data.roi.SimpleRoi;

public interface AcquisitionPanelInterface {

	public void setProgress(String progress, int percentage);
	
	public void acqHasStarted();
	
	public void acqHasStopped();
	
	public void acqHasEnded();
	
	public void setAdvancedSettings(int preRunTime, boolean saveAsStacks, boolean parallelProcessing, SimpleRoi roi);
}
