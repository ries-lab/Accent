package main.java.embl.rieslab.photonfreecamcalib.acquisition;

public interface AcquisitionPanelInterface {

	public void setProgress(int currStep, int totalSteps, int percentage);
	
	public void acqHasStarted();
	
	public void acqHasStopped();
	
	public void acqHasEnded();
	
	public void setAdvancedSettings(boolean alternatedAcquisition, boolean saveAsStacks, boolean parallelProcessing);
}
