package main.java.embl.rieslab.accent.processing;

import main.java.embl.rieslab.accent.calibration.Calibration;

public interface Processor {
	
	public void start();
	
	public void stop();
	
	public boolean isRunning();
	
	public double getExecutionTime();
	
	public String getCalibrationPath();
	
	public Calibration getCalibration();
}
