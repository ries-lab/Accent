package de.embl.rieslab.accent.mm2.interfaces;

import de.embl.rieslab.accent.common.data.roi.SimpleRoi;
/**
 * Interface for an acquisition UI panel.
 * @author Joran Deschamps
 *
 */
public interface AcquisitionPanelInterface {

	/**
	 * Updates the progress status of the acquisition.
	 * @param progress String describing the current status
	 * @param percentage Percentage of progress
	 */
	public void setProgress(String progress, int percentage);
		/**
	 * Updates the UI to reflect that the acquisition has started.
	 */
	public void acqHasStarted();
		/**
	 * Updates the UI to reflect that the acquisition has stopped.
	 */
	public void acqHasStopped();
		/**
	 * Updates the UI to reflect that the acquisition has ended.
	 */
	public void acqHasEnded();
	/**
	 * Changes the state of the UI to reflect the advanced settings values.
	 * 
	 * @param preRunTime
	 * @param saveAsStacks
	 * @param parallelProcessing
	 * @param roi
	 */
	public void setAdvancedSettings(int preRunTime, boolean saveAsStacks, boolean parallelProcessing, SimpleRoi roi);
}
