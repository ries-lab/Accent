package de.embl.rieslab.accent.common.data.image;

/**
 * A pair of FloatImage array used to return average and variance maps for multiple exposures.
 * 
 * @author Joran Deschamps
 *
 */
public class AvgVarStacks {

	private FloatImage[] avgs;
	private FloatImage[] vars;
	
	public AvgVarStacks(FloatImage[] avgs, FloatImage[] vars) {
		this.avgs = avgs;
		this.vars = vars;
	}
	
	/**
	 * Get average maps stack.
	 * @return Array of FloatImage 
	 */
	public FloatImage[] getAvgs() {
		return avgs;
	}

	/**
	 * Get variance maps stack.
	 * @return Array of FloatImage 
	 */
	public FloatImage[] getVars() {
		return vars;
	}
}
