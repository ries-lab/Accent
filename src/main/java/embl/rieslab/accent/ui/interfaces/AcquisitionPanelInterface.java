package main.java.embl.rieslab.accent.ui.interfaces;

import main.java.embl.rieslab.accent.data.SimpleRoi;

public interface AcquisitionPanelInterface {

	public void setProgress(String progress, int percentage);
	
	public void acqHasStarted();
	
	public void acqHasStopped();
	
	public void acqHasEnded();
	
	public void setAdvancedSettings(int preRunTime, boolean saveAsStacks, boolean parallelProcessing, SimpleRoi roi);
}
