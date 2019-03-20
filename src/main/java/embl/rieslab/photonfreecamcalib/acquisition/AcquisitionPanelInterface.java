package main.java.embl.rieslab.photonfreecamcalib.acquisition;

public interface AcquisitionPanelInterface {

	public void setProgress(int progress);
	
	public void acqHasStarted();
	
	public void acqHasStopped();
	
	public void acqHasEnded();
}
