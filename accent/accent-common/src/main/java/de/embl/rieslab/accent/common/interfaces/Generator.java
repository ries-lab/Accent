package de.embl.rieslab.accent.common.interfaces;

import de.embl.rieslab.accent.common.data.calibration.Calibration;

public interface Generator {
	
	public void generate(String path, Calibration calibration, double[] exposures);
	
	public boolean isRunning();
	
}
