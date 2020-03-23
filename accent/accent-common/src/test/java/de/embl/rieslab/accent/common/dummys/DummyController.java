package de.embl.rieslab.accent.common.dummys;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import javax.swing.JFrame;

import de.embl.rieslab.accent.common.data.acquisition.AcquisitionSettings;
import de.embl.rieslab.accent.common.data.image.BareImage;
import de.embl.rieslab.accent.common.interfaces.Loader;
import de.embl.rieslab.accent.common.interfaces.PipelineController;
import de.embl.rieslab.accent.common.interfaces.ui.AcquisitionPanelInterface;
import de.embl.rieslab.accent.common.interfaces.ui.GeneratePanelInterface;
import de.embl.rieslab.accent.common.interfaces.ui.ProcessingPanelInterface;
import de.embl.rieslab.accent.common.processor.CalibrationProcessor;

public class DummyController implements PipelineController {
	public List<String> arr_str;
	
	public DummyController (){
		arr_str = new ArrayList<String>();
	}
	
	@Override
	public JFrame getMainFrame() {return null;}

	@Override
	public void updateAcquisitionProgress(String message, int progress) {}

	@Override
	public void acquisitionHasStarted() {}

	@Override
	public void acquisitionHasStopped() {}

	@Override
	public void acquisitionHasEnded() {}

	@Override
	public boolean isAcquisitionDone() {return false;}

	@Override
	public boolean startAcquisition(AcquisitionSettings settings) {return false;}

	@Override
	public void stopAcquisition() {}

	@Override
	public Loader getLoader(String parameter) {return null;}

	@Override
	public CalibrationProcessor getProcessor(String path, Loader loader) {return null;}

	@Override
	public boolean isProcessorReady() {return false;}

	@Override
	public boolean startProcessor(String path) {return false;}

	@Override
	public boolean startProcessor(String path, HashMap<String, Double> openedDatasets) {return false;}

	@Override
	public boolean startProcessor(String path, ArrayList<ArrayBlockingQueue<BareImage>> queues) {return false;}

	@Override
	public void stopProcessor() {}

	@Override
	public void updateProcessorProgress(String progressString, int progress) {}

	@Override
	public void processingHasStopped() {}

	@Override
	public void processingHasStarted() {}

	@Override
	public void processingHasEnded() {}

	@Override
	public boolean isProcessingRunning() {return false;}

	@Override
	public void setProcessorPanelPath(String path) {}

	@Override
	public boolean startMapGeneration(String path, double[] exposures) {return false;}

	@Override
	public boolean isGenerationRunning() {return false;}

	@Override
	public void setGeneratorProgress(String progress) {
		arr_str.add(progress);
	}

	@Override
	public void setAcquisitionPanel(AcquisitionPanelInterface procpane) {}

	@Override
	public void setProcessingPanel(ProcessingPanelInterface procpane) {}

	@Override
	public void setGeneratePanel(GeneratePanelInterface genpane) {}

	@Override
	public boolean isReady() {return false;}		
}