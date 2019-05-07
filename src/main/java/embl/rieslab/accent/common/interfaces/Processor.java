package main.java.embl.rieslab.accent.common.interfaces;

import main.java.embl.rieslab.accent.common.data.calibration.Calibration;

public interface Processor {
	
	public boolean startProcess();
	
	public void stopProcess();
	
	public boolean isRunning();
	
	public double getExecutionTime();
	
	public String getCalibrationPath();
	
	public Calibration getCalibration();
}
