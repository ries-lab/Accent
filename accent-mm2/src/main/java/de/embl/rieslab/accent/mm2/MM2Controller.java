package de.embl.rieslab.accent.mm2;

import java.awt.Rectangle;
import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;

import javax.swing.JFrame;

import org.micromanager.Studio;

import de.embl.rieslab.accent.common.AbstractController;
import de.embl.rieslab.accent.common.data.roi.SimpleRoi;
import de.embl.rieslab.accent.common.interfaces.data.ArrayToImage;
import de.embl.rieslab.accent.common.interfaces.data.ImageSaver;
import de.embl.rieslab.accent.common.interfaces.pipeline.Loader;
import de.embl.rieslab.accent.common.processor.CalibrationProcessor;
import de.embl.rieslab.accent.common.utils.Dialogs;
import de.embl.rieslab.accent.mm2.acquisition.MM2AcquisitionController;
import de.embl.rieslab.accent.mm2.data.image.ArrayToFloatImage;
import de.embl.rieslab.accent.mm2.data.image.BareImage;
import de.embl.rieslab.accent.mm2.data.image.FloatImage;
import de.embl.rieslab.accent.mm2.data.image.FloatImageSaver;
import de.embl.rieslab.accent.mm2.loader.MMStacksLoader;
import de.embl.rieslab.accent.mm2.loader.QueuesLoader;
import de.embl.rieslab.accent.mm2.processor.QueuesProcessor;
import de.embl.rieslab.accent.mm2.processor.StacksProcessor;
import de.embl.rieslab.accent.mm2.ui.MainFrame;

public class MM2Controller extends AbstractController<BareImage, FloatImage> {

	public static String QUEUES_LOADER = "Queues";
	
	private Studio studio;
	
	private MM2AcquisitionController acqController;
	private String[] directoriesToLoad;
	private ArrayList<ArrayBlockingQueue<BareImage>> queues;
	private int image_width = -1;
	private int image_height = -1;
	
	public MM2Controller(Studio studio) {
		this.studio = studio;

		// hack to get image size
		try {
			studio.getCMMCore().clearROI();
			Rectangle r = studio.getCMMCore().getROI();

			image_width = r.width;
			image_height = r.height;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		acqController = new MM2AcquisitionController(this);
	}
	
	//////// Processing
	@Override
	public boolean startProcessor(String path, SimpleRoi roi, ArrayList<ArrayBlockingQueue<BareImage>> queues) {

		if(path != null &&
				(isAcqPathKnown(path) || new File(path).exists()) && queues != null) {
			
			this.queues = queues;
			
			if(queues.size() >= 2) {
				processor = getProcessor(path, roi, getLoader(QUEUES_LOADER));
				
				if(processor != null) {
					processor.startProcess();
					return true;
				}
				return false;
			} else {
				processorHasStopped();
				return false;
			}
		} 

		return false;
	}
	
	@Override
	public boolean startProcessor(String path, SimpleRoi roi) {	
		if(isReady() && path != null &&
				(isAcqPathKnown(path) || new File(path).exists())) {

			directoriesToLoad = getExposureFolders(path); // should use AccentUtils method instead instead
			
			if(directoriesToLoad == null) {
				Dialogs.showErrorMessage("No experimental folder found in:\n" + path + 
						"\n\nExperiment folder names end with <###ms> where ### is the exposure time.");
				processorHasStopped();
				return false;
			} else if(directoriesToLoad.length < 2) {
				Dialogs.showErrorMessage("Not enough experimental folder (minimum 2).");
				processorHasStopped();
				return false;
			} else if(directoriesToLoad != null && directoriesToLoad.length > 1) {
				processor = getProcessor(path, roi, getLoader(DEFAULT_LOADER));
				processor.startProcess();
				return true;
			}
		} 
		return false;
	}
	
	public boolean startProcessor() {
		// Do nothing
		return false;
	}
	
	//////////////////////// Other methods
	@Override
	public boolean isReady() {
		if(!acqController.isReady() || procPanel == null || genPanel == null) {
			return false;
		}
		
		if(acqController.acqExists() && acqController.acqRunning()) {
			return false;
		}
		
		if(isProcessorRunning()) {
			return false;
		}
		
		if(isGeneratorRunning()) {
			return false;
		}
		
		return true;
	}

	/////////////////////// Private methods
	private String[] getExposureFolders(String path) {
		ArrayList<String> fullpaths = new ArrayList<String>();
		File[] files = new File(path).listFiles();
		if(files != null) {
			for (File file : files) {
				if (file.isDirectory() && file.getName().substring(file.getName().length() - 2).equals("ms")) {
					fullpaths.add(file.getAbsolutePath());
				}
			}

			return fullpaths.toArray(new String[0]);
		} else {
			return null;
		}
	}

	private boolean isAcqPathKnown(String path) {
		return acqController.isAcqPathKnown(path);
	}
	
	@Override
	public JFrame getMainFrame() {		
		MainFrame frame = new MainFrame(studio, this);		
		return frame;
	}

	@Override
	public Loader<BareImage> getLoader(String parameter) {
		if(parameter.equals(DEFAULT_LOADER)) {
			if(directoriesToLoad != null) {
				return new MMStacksLoader(studio, directoriesToLoad);
			} else {
				return null;
			}
		} else if(parameter.equals(QUEUES_LOADER)){ 
			if (queues != null) {
				return new QueuesLoader(queues);
			} else {
				return null;
			}
		}
		return null;
	}

	@Override
	public CalibrationProcessor<BareImage,FloatImage> getProcessor(String path, SimpleRoi roi, Loader<BareImage> loader) {
		if(loader instanceof QueuesLoader) {
			return new QueuesProcessor(path, roi, this, (QueuesLoader) loader, acqController);
		} else if (loader instanceof MMStacksLoader) {
			return new StacksProcessor(path, roi, this, (MMStacksLoader) loader);
		}
		return null;
	}

	public Studio getStudio() {
		return studio;
	}

	public int getImageHeight() {
		return image_height;
	}
	
	public int getImageWidth() {
		return image_width;
	}

	@Override
	public ArrayToImage<FloatImage> getArrayToImageConverter() {
		return new ArrayToFloatImage();
	}

	@Override
	public ImageSaver<FloatImage> getImageSaver() {
		return new FloatImageSaver();
	}

	public MM2AcquisitionController getAcqController() {
		return acqController;
	}

	@Override
	public void stopAll() {
		if(acqController.acqExists() && acqController.acqRunning()) {
			acqController.stopAcquisition();
		}
		
		if(isProcessorRunning()) {
			stopProcessor();
		}
	}

	@Override
	public void logMessage(String message) {
		studio.logs().logMessage(message);
	}
}
