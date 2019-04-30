package main.java.embl.rieslab.accent.processing;

public interface ProcessingPanelInterface {

	public void setDataPath(String path);
	
	public void setProgress(String progress, int percentage);
	
	public void processingHasStarted();
	
	public void processingHasStopped();
	
	public void processingHasEnded();
}
