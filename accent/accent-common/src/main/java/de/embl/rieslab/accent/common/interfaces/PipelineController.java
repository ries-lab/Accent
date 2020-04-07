package de.embl.rieslab.accent.common.interfaces;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;

import javax.swing.JFrame;

import de.embl.rieslab.accent.common.data.acquisition.AcquisitionSettings;
import de.embl.rieslab.accent.common.data.image.BareImage;
import de.embl.rieslab.accent.common.interfaces.ui.AcquisitionPanelInterface;
import de.embl.rieslab.accent.common.interfaces.ui.GeneratePanelInterface;
import de.embl.rieslab.accent.common.interfaces.ui.ProcessingPanelInterface;
import de.embl.rieslab.accent.common.processor.CalibrationProcessor;

/**
 * Interface for a pipeline controller.
 * 
 * @author Joran Deschamps
 *
 */
public interface PipelineController {
	
	/**
	 * Returns the plugin main frame.
	 * @return
	 */
	public JFrame getMainFrame();
	
	//////// acquisition
	/**
	 * Updates the acquisition panel.
	 * @param message Current acquisition status
	 * @param progress Acquisition progress percentage
	 */
	public void updateAcquisitionProgress(String message, int progress);

	/**
	 * Reflects on the acquisition panel that the acquisition has started. 
	 */
	public void acquisitionHasStarted();

	/**
	 * Reflects on the acquisition panel that the acquisition has stopped. 
	 */
	public void acquisitionHasStopped();

	/**
	 * Reflects on the acquisition panel that the acquisition has ended. 
	 */
	public void acquisitionHasEnded();
	
	/**
	 * Checks if the acquisition is done.
	 * @return True if it is, false otherwise.
	 */
	public boolean isAcquisitionDone();
	
	/**
	 * Starts an acquisition with the specified acquisition settings.
	 * @param settings
	 * @return True if the acquisition was started, false otherwise.
	 */
	public boolean startAcquisition(AcquisitionSettings settings);
	
	/**
	 * Stops acquisition.
	 */
	public void stopAcquisition();
	
	//////// Processing
	/**
	 * Gets Loader.
	 * 
	 * @param parameter Optional parameter.
	 * @return
	 */
	public Loader getLoader(String parameter);
	
	/**
	 * Gets processor.
	 * 
	 * @param path Path to load images from.
	 * @param loader
	 * @return
	 */
	public CalibrationProcessor getProcessor(String path, Loader loader);
	
	/**
	 * Checks if the processor is ready.
	 * @return True if it is, false otherwise
	 */
	public boolean isProcessorReady();
	
	public boolean startProcessor(String path);

	public boolean startProcessor(String path, HashMap<String, Double> openedDatasets);
	
	public boolean startProcessor(String path, ArrayList<ArrayBlockingQueue<BareImage>> queues);

	public void stopProcessor();
	
	public void updateProcessorProgress(String progressString, int progress);

	public void processingHasStopped();

	public void processingHasStarted();
	
	public void processingHasEnded();

	public boolean isProcessingRunning();
	
	public void setProcessorPanelPath(String path);
	
	//////// map generation
	public boolean startMapGeneration(String path, double[] exposures);
	
	public boolean isGenerationRunning();
	
	public void updateGeneratorProgress(String progress);
	
	//////////////////////// Other methods
	public void setAcquisitionPanel(AcquisitionPanelInterface procpane);
	
	public void setProcessingPanel(ProcessingPanelInterface procpane);
	
	public void setGeneratePanel(GeneratePanelInterface genpane);
		
	public boolean isReady();

}