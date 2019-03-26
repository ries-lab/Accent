package main.java.embl.rieslab.photonfreecamcalib.acquisition;


public interface Acquisition {

	public void start();
	
	public void stop();
	
	public boolean isRunning();

	public int getMaxNumberFrames();
	
	public AcquisitionSettings getSettings();
}
