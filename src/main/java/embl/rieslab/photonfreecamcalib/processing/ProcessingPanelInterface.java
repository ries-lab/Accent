package main.java.embl.rieslab.photonfreecamcalib.processing;

public interface ProcessingPanelInterface {

	public void setDataPath(String path);
	
	public void setProgress(int progress);
	
	public void procHasStarted();
	
	public void procHasStopped();
	
	public void procHasEnded();
}
