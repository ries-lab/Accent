package main.java.embl.rieslab.photonfreecamcalib.analysis;

import main.java.embl.rieslab.photonfreecamcalib.calibration.Calibration;

public interface Analyzer {

	public void start();
	
	public void stop();
	
	public boolean isRunning();
	
	public Calibration getResults();
	
}
