package de.embl.rieslab.accent.mm2.dummys;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import javax.swing.JFrame;

import de.embl.rieslab.accent.common.interfaces.data.ArrayToImage;
import de.embl.rieslab.accent.common.interfaces.data.ImageSaver;
import de.embl.rieslab.accent.common.interfaces.pipeline.Loader;
import de.embl.rieslab.accent.common.interfaces.pipeline.PipelineController;
import de.embl.rieslab.accent.common.interfaces.ui.GeneratePanelInterface;
import de.embl.rieslab.accent.common.interfaces.ui.ProcessingPanelInterface;
import de.embl.rieslab.accent.common.processor.CalibrationProcessor;
import de.embl.rieslab.accent.mm2.data.image.BareImage;
import de.embl.rieslab.accent.mm2.data.image.FloatImage;

public class DummyMM2Controller implements PipelineController<BareImage, FloatImage> {
	public List<String> gen_progress;
	public List<String> proc_progress;
	
	public DummyMM2Controller (){
		gen_progress = new ArrayList<String>();
		proc_progress = new ArrayList<String>();
	}
	
	@Override
	public JFrame getMainFrame() {return null;}

	@Override
	public Loader<BareImage> getLoader(String parameter) {return null;}

	@Override
	public CalibrationProcessor<BareImage, FloatImage> getProcessor(String path, Loader<BareImage> loader) {return null;}

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
	public void updateProcessorProgress(String progressString, int progress) {
		proc_progress.add(progressString);
	}

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
	public void updateGeneratorProgress(String progress) {
		gen_progress.add(progress);
	}

	@Override
	public void setProcessingPanel(ProcessingPanelInterface procpane) {}

	@Override
	public void setGeneratePanel(GeneratePanelInterface genpane) {}

	@Override
	public boolean isReady() {return false;}

	@Override
	public ArrayToImage<FloatImage> getImageConverter() {return null;}

	@Override
	public ImageSaver<FloatImage> getImageSaver() {return null;}

}