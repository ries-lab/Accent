package de.embl.rieslab.accent.common.data.image;

public class AvgVarStacks {

	private FloatImage[] avgs;
	private FloatImage[] vars;
	
	public AvgVarStacks(FloatImage[] avgs, FloatImage[] vars) {
		this.avgs = avgs;
		this.vars = vars;
	}
	
	public FloatImage[] getAvgs() {
		return avgs;
	}
	
	public FloatImage[] getVars() {
		return vars;
	}
}
