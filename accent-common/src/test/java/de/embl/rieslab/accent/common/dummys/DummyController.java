package de.embl.rieslab.accent.common.dummys;

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
import ij.ImagePlus;
import ij.io.FileSaver;
import ij.process.FloatProcessor;

public class DummyController implements PipelineController<DummyImage, DummyImage>{
	public List<String> gen_progress;
	public List<String> proc_progress;
	
	public DummyController (){
		gen_progress = new ArrayList<String>();
		proc_progress = new ArrayList<String>();
	}
	
	@Override
	public JFrame getMainFrame() {return null;}

	@Override
	public Loader<DummyImage> getLoader(String parameter) {return null;}

	@Override
	public CalibrationProcessor<DummyImage, DummyImage> getProcessor(String path, SimpleRoi roi, Loader<DummyImage> loader) {return null;}

	@Override
	public boolean isProcessorReady() {return false;}

	@Override
	public boolean startProcessor(String path, SimpleRoi roi) {return true;}

	@Override
	public boolean startProcessor(String path, SimpleRoi roi, ArrayList<ArrayBlockingQueue<DummyImage>> queues) {return true;}

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
	public void setProcessorPanelPath(String path) {}

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
	public ArrayToImage<DummyImage> getArrayToImageConverter() {
		return new ArrayToImage<DummyImage>() {
			@Override
			public DummyImage getImage(float[] pixels, int width, int height,
					double exposure) {
				DummyImage im = new DummyImage(4, pixels, width, height, exposure);
				return im;
			}
		};
	}

	@Override
	public ImageSaver<DummyImage> getImageSaver() {
		return new ImageSaver<DummyImage>() {
			@Override
			public boolean saveAsTiff(DummyImage image, String filePath) {
				if (!(filePath.endsWith(".tif") || filePath.endsWith(".tiff") || filePath.endsWith(".TIF")
						|| filePath.endsWith(".TIFF"))) {
					filePath = filePath + ".tif";
				}
				
				FloatProcessor img = new FloatProcessor(image.getWidth(), image.getHeight());
				img.setPixels(image.getImage());
				
				FileSaver fs = new FileSaver(new ImagePlus("", img));
				return fs.saveAsTiff(filePath);
			}
		};
	}

	@Override
	public void stopAll() {}

	@Override
	public void logMessage(String message) {
		System.out.println(message);
	}
}
