package de.embl.rieslab.accent.mm2.dummys;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import javax.swing.JFrame;

import de.embl.rieslab.accent.common.data.roi.SimpleRoi;
import de.embl.rieslab.accent.common.interfaces.data.ArrayToImage;
import de.embl.rieslab.accent.common.interfaces.data.ImageSaver;
import de.embl.rieslab.accent.common.interfaces.pipeline.Loader;
import de.embl.rieslab.accent.common.interfaces.pipeline.PipelineController;
import de.embl.rieslab.accent.common.interfaces.ui.GeneratorPanelInterface;
import de.embl.rieslab.accent.common.interfaces.ui.ProcessorPanelInterface;
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
	public CalibrationProcessor<BareImage, FloatImage> getProcessor(String path, SimpleRoi roi, Loader<BareImage> loader) {return null;}

	@Override
	public boolean isProcessorReady() {return false;}

	@Override
	public boolean startProcessor(String path, SimpleRoi roi) {return false;}

	@Override
	public boolean startProcessor(String path, SimpleRoi roi, ArrayList<ArrayBlockingQueue<BareImage>> queues) {return false;}

	@Override
	public void stopProcessor() {}

	@Override
	public void updateProcessorProgress(String progressString, int progress) {
		proc_progress.add(progressString);
	}

	@Override
	public void processorHasStopped() {}

	@Override
	public void processorHasStarted() {}

	@Override
	public void processorHasEnded() {}

	@Override
	public boolean isProcessorRunning() {return false;}

	@Override
	public void setProcessorPanelPath(String path, SimpleRoi roi) {}

	@Override
	public boolean startGenerator(String path, double[] exposures) {return false;}

	@Override
	public boolean isGeneratorRunning() {return false;}

	@Override
	public void updateGeneratorProgress(String progress) {
		gen_progress.add(progress);
	}

	@Override
	public void setProcessorPanel(ProcessorPanelInterface procpane) {}

	@Override
	public void setGeneratorPanel(GeneratorPanelInterface genpane) {}

	@Override
	public boolean isReady() {return false;}

	@Override
	public ArrayToImage<FloatImage> getArrayToImageConverter() {return null;}

	@Override
	public ImageSaver<FloatImage> getImageSaver() {return null;}

	@Override
	public void stopAll() {}

	@Override
	public void logMessage(String message) {
		System.out.println(message);
	}
}