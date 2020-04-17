package de.embl.rieslab.accent.common.interfaces.pipeline;

import de.embl.rieslab.accent.common.data.calibration.Calibration;
/**
 * Interface for an average and variance maps generator.
 * 
 * @author Joran Deschamps
 *
 */
public interface Generator {
	
	/**
	 * Generates the average and variance maps for the different exposures using the calibration.
	 * @param path Path where to save the results
	 * @param calibration Calibration to use
	 * @param exposures Exposures in ms
	 */
	public void generate(String path, Calibration calibration, double[] exposures);
	
	/**
	 * Checks if the generator is running.
	 * @return True if it is running, false otherwise.
	 */
	public boolean isRunning();
	
}
