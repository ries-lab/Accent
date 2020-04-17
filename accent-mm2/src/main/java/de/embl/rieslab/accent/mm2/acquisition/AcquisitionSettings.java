package de.embl.rieslab.accent.mm2.acquisition;

import de.embl.rieslab.accent.mm2.data.roi.SimpleRoi;

/**
 * Settings used for acquiring data.
 * 
 * @author Joran Deschamps
 *
 */
public class AcquisitionSettings {

	/**
	 * Experiment name (used in naming the folder and files)
	 */
	public String name_ = null; 
	/** 
	 * Home folder in which to save the experiment
	 */
	public String folder_ = null; 
	/**
	 * Total number of frames per exposure to acquire
	 */
	public int numFrames_ = 20000; 
	/**
	 * Array of the different exposures in ms to acquire
	 */
	public Double[] exposures_ = {10.,20.,300.}; 
	/**
	 * Roi with which to crop the camera frame
	 */
	public SimpleRoi roi_ = null; 
	/**
	 * Save as stack if true, as individual frames if false
	 */
	public boolean saveAsStacks_ = true; 
	/**
	 * Start processing the frames in parallel to the acquisition
	 */
	public boolean parallelProcessing = true; 
	/**
	 * Pre-warming time in minutes of the camera
	 */
	public int preRunTime_= 1; 
		
}
