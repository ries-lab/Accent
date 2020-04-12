package de.embl.rieslab.accent.fiji.dummys;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;

import javax.swing.JFrame;

import de.embl.rieslab.accent.common.interfaces.data.ArrayToImage;
import de.embl.rieslab.accent.common.interfaces.data.ImageSaver;
import de.embl.rieslab.accent.common.interfaces.pipeline.Loader;
import de.embl.rieslab.accent.common.interfaces.pipeline.PipelineController;
import de.embl.rieslab.accent.common.interfaces.ui.GeneratorPanelInterface;
import de.embl.rieslab.accent.common.interfaces.ui.ProcessorPanelInterface;
import de.embl.rieslab.accent.common.processor.CalibrationProcessor;
import de.embl.rieslab.accent.fiji.data.image.PlaneImg;
import de.embl.rieslab.accent.fiji.data.image.StackImg;

public class DummyController implements PipelineController<StackImg, PlaneImg>{

	@Override
	public JFrame getMainFrame() {return null;}

	@Override
	public ArrayToImage<PlaneImg> getImageConverter() {
		return null;
	}

	@Override
	public ImageSaver<PlaneImg> getImageSaver() {
		return null;
	}

	@Override
	public Loader<StackImg> getLoader(String parameter) {
		return null;
	}

	@Override
	public CalibrationProcessor<StackImg, PlaneImg> getProcessor(String path, Loader<StackImg> loader) {return null;}

	@Override
	public boolean isProcessorReady() {return false;}

	@Override
	public boolean startProcessor(String path) {return false;}

	@Override
	public boolean startProcessor(String path, HashMap<String, Double> openedDatasets) {return false;}

	@Override
	public boolean startProcessor(String path, ArrayList<ArrayBlockingQueue<StackImg>> queues) {return false;}

	@Override
	public void stopProcessor() {}

	@Override
	public void updateProcessorProgress(String progressString, int progress) {}

	@Override
	public void processorHasStopped() {}

	@Override
	public void processorHasStarted() {}

	@Override
	public void processorHasEnded() {}

	@Override
	public boolean isProcessorRunning() {return false;}

	@Override
	public void setProcessorPanelPath(String path) {}

	@Override
	public boolean startGenerator(String path, double[] exposures) {return false;}

	@Override
	public boolean isGeneratorRunning() {return false;}

	@Override
	public void updateGeneratorProgress(String progress) {}

	@Override
	public void setProcessorPanel(ProcessorPanelInterface procpane) {}

	@Override
	public void setGeneratorPanel(GeneratorPanelInterface genpane) {}

	@Override
	public boolean isReady() {return false;}

}
