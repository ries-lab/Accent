package de.embl.rieslab.accent.common.interfaces.pipeline;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;

import javax.swing.JFrame;

import de.embl.rieslab.accent.common.interfaces.data.ArrayToImage;
import de.embl.rieslab.accent.common.interfaces.data.CalibrationImage;
import de.embl.rieslab.accent.common.interfaces.data.ImageSaver;
import de.embl.rieslab.accent.common.interfaces.data.RawImage;
import de.embl.rieslab.accent.common.interfaces.ui.GeneratorPanelInterface;
import de.embl.rieslab.accent.common.interfaces.ui.ProcessorPanelInterface;
import de.embl.rieslab.accent.common.processor.CalibrationProcessor;

/**
 * Interface for a pipeline controller.
 * 
 * @author Joran Deschamps
 *
 */
public interface PipelineController<U extends RawImage, T extends CalibrationImage> {
	
	/**
	 * Returns the plugin main frame.
	 * @return
	 */
	public JFrame getMainFrame();

	public ArrayToImage<T> getArrayToImageConverter();
	
	public ImageSaver<T> getImageSaver();
	
	public void stopAll();
		
	//////// Processing
	/**
	 * Gets Loader.
	 * 
	 * @param parameter Optional parameter.
	 * @return
	 */
	public Loader<U> getLoader(String parameter);
	
	/**
	 * Gets processor.
	 * 
	 * @param path Path to load images from.
	 * @param loader
	 * @return
	 */
	public CalibrationProcessor<U,T> getProcessor(String path, Loader<U> loader);
	
	/**
	 * Checks if the processor is ready.
	 * @return True if it is, false otherwise
	 */
	public boolean isProcessorReady();
	
	public boolean startProcessor(String path);
	
	public boolean startProcessor(String path, ArrayList<ArrayBlockingQueue<U>> queues);

	public void stopProcessor();
	
	public void updateProcessorProgress(String progressString, int progress);

	public void processorHasStopped();

	public void processorHasStarted();
	
	public void processorHasEnded();

	public boolean isProcessorRunning();
	
	public void setProcessorPanelPath(String path);
	
	//////// map generation
	public boolean startGenerator(String path, double[] exposures);
	
	public boolean isGeneratorRunning();
	
	public void updateGeneratorProgress(String progress);
	
	//////////////////////// Other methods
	public void setProcessorPanel(ProcessorPanelInterface procpane);
	
	public void setGeneratorPanel(GeneratorPanelInterface genpane);
		
	public boolean isReady();

	public void logMessage(String message);
}