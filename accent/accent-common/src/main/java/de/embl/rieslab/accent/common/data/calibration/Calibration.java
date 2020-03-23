package de.embl.rieslab.accent.common.data.calibration;

import java.util.Arrays;

/**
 * 
 * 
 * @author Joran Deschamps
 *
 */
public class Calibration {

	private int width;
	private int height;
	
	// avg over time fit
	private double[] baseline; // avg over time fit offset 
	private double[] dc_per_sec; // dark current per time: slope of the fit
	private double[] r_sq_avg; // fit coefficient of determination
	
	// var over time fit
	private double[] rn_sq; // read-noise square: fit offset
	private double[] tn_sq_per_sec; // thermal noise square: fit slope
	private double[] r_sq_var; // fit coefficient of determination
	
	private double[] gain; // var vs avg, fit slope
	private double[] r_sq_gain; // fit coefficient of determination

	public Calibration() {
	}

	public Calibration(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	public Calibration(int width, int height, double[] baseline, double[] dc_per_sec, double[] r_sq_avg, double[] rn_sq,
			double[] tn_sq_per_sec, double[] r_sq_var, double[] gain, double[] r_sq_gain) {
		this.width = width;
		this.height = height;

		setBaseline(baseline);
		setDcPerSec(dc_per_sec);
		setRSqAvg(r_sq_avg);
		
		setRnSq(rn_sq);
		setTnSqPerSec(tn_sq_per_sec);
		setRSqVar(r_sq_var);
		
		setGain(gain);
		setRSqGain(r_sq_gain);
	}

	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public double[] getBaseline() {
		return baseline;
	}
	
	public void setBaseline(double[] baseline) {
		if(baseline == null)
			throw new NullPointerException();
		
		if(baseline.length != width*height)
			throw new IllegalArgumentException("The array must be of length "+width*height+" (size = "+baseline.length+").");
		
		this.baseline = baseline;
	}
	
	public double[] getDcPerSec() {
		return dc_per_sec;
	}
	
	public void setDcPerSec(double[] dc_per_sec) {
		if(dc_per_sec == null)
			throw new NullPointerException();
		
		if(dc_per_sec.length != width*height)
			throw new IllegalArgumentException("The array must be of length "+width*height+" (size = "+dc_per_sec.length+").");
		
		this.dc_per_sec = dc_per_sec;
	}

	
	public double[] getRSqAvg() {
		return r_sq_avg;
	}
	
	public void setRSqAvg(double[] r_sq_avg) {
		if(r_sq_avg == null)
			throw new NullPointerException();
		
		if(r_sq_avg.length != width*height)
			throw new IllegalArgumentException("The array must be of length "+width*height+" (size = "+r_sq_avg.length+").");
		
		this.r_sq_avg = r_sq_avg;
	}

	
	public double[] getRnSq() {
		return rn_sq;
	}
	
	public void setRnSq(double[] rn_sq) {
		if(rn_sq == null)
			throw new NullPointerException();
		
		if(rn_sq.length != width*height)
			throw new IllegalArgumentException("The array must be of length "+width*height+" (size = "+rn_sq.length+").");
		
		this.rn_sq = rn_sq;
	}

	
	public double[] getTnSqPerSec() {
		return tn_sq_per_sec;
	}
	
	public void setTnSqPerSec(double[] tn_sq_per_sec) {
		if(tn_sq_per_sec == null)
			throw new NullPointerException();
		
		if(tn_sq_per_sec.length != width*height)
			throw new IllegalArgumentException("The array must be of length "+width*height+" (size = "+tn_sq_per_sec.length+").");
		
		this.tn_sq_per_sec = tn_sq_per_sec;
	}

	
	public double[] getRSqVar() {
		return r_sq_var;
	}
	
	public void setRSqVar(double[] r_sq_var) {
		if(r_sq_var == null)
			throw new NullPointerException();
		
		if(r_sq_var.length != width*height)
			throw new IllegalArgumentException("The array must be of length "+width*height+" (size = "+r_sq_var.length+").");
		
		this.r_sq_var = r_sq_var;
	}

	
	public double[] getGain() {
		return gain;
	}
	
	public void setGain(double[] gain) {
		if(gain == null)
			throw new NullPointerException();
		
		if(gain.length != width*height)
			throw new IllegalArgumentException("The array must be of length "+width*height+" (size = "+gain.length+").");
		
		this.gain = gain;
	}

	
	public double[] getRSqGain() {
		return r_sq_gain;
	}
	
	public void setRSqGain(double[] r_sq_gain) {
		if(r_sq_gain == null)
			throw new NullPointerException();
		
		if(r_sq_gain.length != width*height)
			throw new IllegalArgumentException("The array must be of length "+width*height+" (size = "+r_sq_gain.length+").");
		
		this.r_sq_gain = r_sq_gain;
	}
	
	public static boolean areEquals(Calibration c1, Calibration c2) {
		return c1.getWidth()==c2.getWidth() && c1.getHeight()==c2.getHeight() && Arrays.equals(c1.getBaseline(), c2.getBaseline()) 
				&& Arrays.equals(c1.getDcPerSec(), c2.getDcPerSec()) && Arrays.equals(c1.getRSqAvg(), c2.getRSqAvg()) 
				&& Arrays.equals(c1.getRnSq(), c2.getRnSq()) && Arrays.equals(c1.getTnSqPerSec(), c2.getTnSqPerSec()) 
				&& Arrays.equals(c1.getRSqVar(), c2.getRSqVar()) && Arrays.equals(c1.getGain(), c2.getGain()) 
				&& Arrays.equals(c1.getRSqGain(), c2.getRSqGain());
	}
}
