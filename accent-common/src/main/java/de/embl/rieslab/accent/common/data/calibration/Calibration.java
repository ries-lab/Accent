package de.embl.rieslab.accent.common.data.calibration;

import java.util.Arrays;

/**
 * Represents a camera calibration, including different estimated measurements such as the baseline,
 * the dark current per second, the read-noise square, the thermal noise square per second, the gain
 * and the fit coefficient of determinations.
 * 
 * @author Joran Deschamps
 *
 */
public class Calibration {

	private int width;
	private int height;
	
	/**
	 * Baseline: offset of the linear fit (average_pixel, exposure)
	 */
	private double[] baseline;
	/**
	 * Dark current per second: slope of the fit (average_pixel, exposure) 
	 */
	private double[] dc_per_sec;
	/**
	 * (average_pixel, exposure) fit coefficient of determination
	 */
	private double[] r_sq_avg;

	/**
	 * Read-noise square: offset of the linear fit (variance_pixel, exposure)
	 */
	private double[] rn_sq; 
	/**
	 * Thermal noise square: slope of the fit (variance_pixel, exposure) 
	 */
	private double[] tn_sq_per_sec;
	/**
	 * (variance_pixel, exposure) fit coefficient of determination
	 */
	private double[] r_sq_var;

	/**
	 * Thermal noise square: slope of the fit (variance_pixel, average_pixel) 
	 */
	private double[] gain; 
	/**
	 * (variance_pixel, average_pixel) fit coefficient of determination
	 */
	private double[] r_sq_gain; 

	/**
	 * Empty constructor used by JacksonJSON to build a Calibration object
	 */
	public Calibration() {}

	public Calibration(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	/**
	 * Constructor.
	 * 
	 * @param width Width of the calibrated roi
	 * @param height Height of the calibrated roi
	 * @param baseline Baseline 
	 * @param dc_per_sec Dark-current per second
	 * @param r_sq_avg Average-exposure fit coefficient of determination
	 * @param rn_sq Read-noise square
	 * @param tn_sq_per_sec Thermal noise square per second
	 * @param r_sq_var Variance-exposure fit coefficient of determination
	 * @param gain Gain
	 * @param r_sq_gain Variance-average fit coefficient of determination
	 */
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

	/**
	 * Returns calibration roi width in pixels.
	 * @return Width in pixels.
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Returns calibration roi height in pixels.
	 * @return Height in pixels.
	 */
	public int getHeight() {
		return height;
	}
	
	/**
	 * Returns the baseline for each pixel.
	 * @return Baseline as a double array of length width*height
	 */
	public double[] getBaseline() {
		return baseline;
	}
	
	/**
	 * Sets the baseline for each pixel.
	 * @param baseline Baseline for each pixel
	 */
	public void setBaseline(double[] baseline) {
		if(baseline == null)
			throw new NullPointerException();
		
		if(baseline.length != width*height)
			throw new IllegalArgumentException("The array must be of length "+width*height+" (size = "+baseline.length+").");
		
		this.baseline = baseline;
	}
	/**
	 * Returns the dark current per second for each pixel.
	 * @return Dark current per second as a double array of length width*height
	 */
	public double[] getDcPerSec() {
		return dc_per_sec;
	}
	/**
	 * Sets the dark current per second for each pixel.
	 * @param dc_per_sec Dark current per second fo each pixel
	 */
	public void setDcPerSec(double[] dc_per_sec) {
		if(dc_per_sec == null)
			throw new NullPointerException();
		
		if(dc_per_sec.length != width*height)
			throw new IllegalArgumentException("The array must be of length "+width*height+" (size = "+dc_per_sec.length+").");
		
		this.dc_per_sec = dc_per_sec;
	}

	/**
	 * Returns the (average,exposure) fit coefficient of determination for each pixel.
	 * @return Double array of length width*height.
	 */
	public double[] getRSqAvg() {
		return r_sq_avg;
	}
	/**
	 * Sets the (average,exposure) fit coeff of determination for each pixel.
	 * @param r_sq_avg 
	 */
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
	
	/**
	 * Compares two configurations.
	 * 
	 * @param c1 Configuration 1
	 * @param c2 Configuration 2
	 * @return True if all members of c1 are equal to those of c2, false otherwise.
	 */
	public static boolean areEquals(Calibration c1, Calibration c2) {
		return c1.getWidth()==c2.getWidth() && c1.getHeight()==c2.getHeight() && Arrays.equals(c1.getBaseline(), c2.getBaseline()) 
				&& Arrays.equals(c1.getDcPerSec(), c2.getDcPerSec()) && Arrays.equals(c1.getRSqAvg(), c2.getRSqAvg()) 
				&& Arrays.equals(c1.getRnSq(), c2.getRnSq()) && Arrays.equals(c1.getTnSqPerSec(), c2.getTnSqPerSec()) 
				&& Arrays.equals(c1.getRSqVar(), c2.getRSqVar()) && Arrays.equals(c1.getGain(), c2.getGain()) 
				&& Arrays.equals(c1.getRSqGain(), c2.getRSqGain());
	}
}
