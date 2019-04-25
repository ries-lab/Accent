package main.java.embl.rieslab.photonfreecamcalib.processing;

import main.java.embl.rieslab.photonfreecamcalib.calibration.Calibration;

public interface Processor {
	
	public void start();
	
	public void stop();
	
	public boolean isRunning();
	
	public double getExecutionTime();
	
	public String getCalibrationPath();
	
	public Calibration getCalibration();
}
