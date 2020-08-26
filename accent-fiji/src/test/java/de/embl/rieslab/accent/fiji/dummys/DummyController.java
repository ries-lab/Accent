package de.embl.rieslab.accent.fiji.dummys;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;

import javax.swing.JFrame;

import de.embl.rieslab.accent.common.data.roi.SimpleRoi;
import de.embl.rieslab.accent.common.interfaces.pipeline.Loader;
import de.embl.rieslab.accent.common.interfaces.pipeline.PipelineController;
import de.embl.rieslab.accent.common.interfaces.ui.GeneratorPanelInterface;
import de.embl.rieslab.accent.common.interfaces.ui.ProcessorPanelInterface;
import de.embl.rieslab.accent.common.processor.CalibrationProcessor;
import de.embl.rieslab.accent.fiji.data.image.ArrayToPlaneImg;
import de.embl.rieslab.accent.fiji.data.image.PlaneImg;
import de.embl.rieslab.accent.fiji.data.image.PlaneImgSaver;
import de.embl.rieslab.accent.fiji.data.image.StackImg;

public class DummyController implements PipelineController<StackImg, PlaneImg>{

	@Override
	public JFrame getMainFrame() {return null;}

	@Override
	public ArrayToPlaneImg getArrayToImageConverter() {
		return new ArrayToPlaneImg();
	}

	@Override
	public PlaneImgSaver getImageSaver() {
		return new PlaneImgSaver();
	}

	@Override
	public Loader<StackImg> getLoader(String parameter) {
		return null;
	}

	@Override
	public CalibrationProcessor<StackImg, PlaneImg> getProcessor(String path, SimpleRoi roi, Loader<StackImg> loader) {return null;}

	@Override
	public boolean isProcessorReady() {return false;}

	@Override
	public boolean startProcessor(String path, SimpleRoi roi) {return false;}

	@Override
	public boolean startProcessor(String path, SimpleRoi roi, ArrayList<ArrayBlockingQueue<StackImg>> queues) {return false;}

	@Override
	public void stopProcessor() {}

	@Override
	public void updateProcessorProgress(String progressString, int progress) {
		System.out.println(progressString);
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
	public void updateGeneratorProgress(String progress) {}

	@Override
	public void setProcessorPanel(ProcessorPanelInterface procpane) {}

	@Override
	public void setGeneratorPanel(GeneratorPanelInterface genpane) {}

	@Override
	public boolean isReady() {return false;}

	@Override
	public void stopAll() {}

	@Override
	public void logMessage(String message) {
		System.out.println(message);
	}
}
