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

public interface PipelineController {
	
	public JFrame getMainFrame();
	
	//////// acquisition
	public void updateAcquisitionProgress(String message, int progress);

	public void acquisitionHasStarted();
	
	public void acquisitionHasStopped();

	public void acquisitionHasEnded();
	
	public boolean isAcquisitionDone();
	
	public boolean startAcquisition(AcquisitionSettings settings);
	
	public void stopAcquisition();
	
	//////// Processing
	public Loader getLoader(String parameter);
	
	public CalibrationProcessor getProcessor(String path, Loader loader);
	
	public boolean isProcessorReady();
	
	public boolean startProcessor(String path);

	public boolean startProcessor(String path, HashMap<String, Integer> openedDatasets);
	
	public boolean startProcessor(String path, ArrayList<ArrayBlockingQueue<BareImage>> queues);

	public void stopProcessor();
	
	public void updateProcessorProgress(String progressString, int progress);

	public void processingHasStopped();

	public void processingHasStarted();
	
	public void processingHasEnded();

	public boolean isProcessingRunning();
	
	public void setProcessorPanelPath(String path);
	
	//////// map generation
	public boolean startMapGeneration(String path, Integer[] exposures);
	
	public boolean isGenerationRunning();
	
	public void setGeneratorProgress(String progress);
	
	//////////////////////// Other methods
	public void setAcquisitionPanel(AcquisitionPanelInterface procpane);
	
	public void setProcessingPanel(ProcessingPanelInterface procpane);
	
	public void setGeneratePanel(GeneratePanelInterface genpane);
		
	public boolean isReady();

}