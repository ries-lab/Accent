package main.java.embl.rieslab.accent.generator;

import main.java.embl.rieslab.accent.data.calibration.Calibration;

public interface Generator {
	
	public void generate(String path, Calibration calibration, Integer[] exposures);
	
	public boolean isRunning();
	
}
