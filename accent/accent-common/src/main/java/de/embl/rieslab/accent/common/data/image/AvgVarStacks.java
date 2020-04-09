package de.embl.rieslab.accent.common.data.image;

import de.embl.rieslab.accent.common.interfaces.data.CalibrationImage;

/**
 * A pair of FloatImage array used to return average and variance maps for multiple exposures.
 * 
 * @author Joran Deschamps
 *
 */
public class AvgVarStacks<T extends CalibrationImage> {

	private T[] avgs;
	private T[] vars;
	
	public AvgVarStacks(T[] avgs, T[] vars) {
		this.avgs = avgs;
		this.vars = vars;
	}
	
	/**
	 * Get average maps stack.
	 * @return Array of FloatImage 
	 */
	public T[] getAvgs() {
		return avgs;
	}

	/**
	 * Get variance maps stack.
	 * @return Array of FloatImage 
	 */
	public T[] getVars() {
		return vars;
	}
}
