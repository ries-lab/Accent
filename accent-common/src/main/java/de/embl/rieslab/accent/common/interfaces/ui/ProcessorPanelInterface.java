package de.embl.rieslab.accent.common.interfaces.ui;

import de.embl.rieslab.accent.common.data.roi.SimpleRoi;

/**
 * Interface for a processor UI panel.
 * @author Joran Deschamps
 *
 */
public interface ProcessorPanelInterface {

	/**
	 * Shows the path to the data.
	 * @param path
	 */
	public void setDataPath(String path);
	
	public void setRoi(SimpleRoi roi);
	
	/**
	 * Updates the progress status of the processing.
	 * @param progress String describing the current status
	 * @param percentage Percentage of progress
	 */
	public void setProgress(String progress, int percentage);
	
	/**
	 * Updates the UI to reflect that the processing has started.
	 */
	public void processingHasStarted();
	/**
	 * Updates the UI to reflect that the processing has stopped.
	 */
	public void processingHasStopped();
	/**
	 * Updates the UI to reflect that the processing has ended.
	 */
	public void processingHasEnded();
}
