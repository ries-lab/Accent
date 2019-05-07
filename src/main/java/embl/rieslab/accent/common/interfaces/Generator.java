package main.java.embl.rieslab.accent.common.interfaces;

import main.java.embl.rieslab.accent.common.data.calibration.Calibration;

public interface Generator {
	
	public void generate(String path, Calibration calibration, Integer[] exposures);
	
	public boolean isRunning();
	
}
