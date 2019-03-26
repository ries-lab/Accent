package main.java.embl.rieslab.photonfreecamcalib.processing;

public interface Processor {

	public String getCurrentParentPath();
	
	public void start();
	
	public void stop();
	
	public boolean isRunning();
	
}
