package main.java.embl.rieslab.photonfreecamcalib.generator;

import main.java.embl.rieslab.photonfreecamcalib.calibration.Calibration;

public interface Generator {
	
	public void generate(String path, Calibration calibration, Integer[] exposures);
	
	public boolean isRunning();
	
}
