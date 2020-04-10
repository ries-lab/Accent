package de.embl.rieslab.accent.mm2;

import java.awt.Rectangle;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.micromanager.Studio;

import de.embl.rieslab.accent.common.AbstractController;
import de.embl.rieslab.accent.common.interfaces.data.ArrayToImage;
import de.embl.rieslab.accent.common.interfaces.data.ImageSaver;
import de.embl.rieslab.accent.common.interfaces.pipeline.Loader;
import de.embl.rieslab.accent.common.processor.CalibrationProcessor;
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
	private boolean acqDone;
	
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
	public boolean startProcessor(String path, ArrayList<ArrayBlockingQueue<BareImage>> queues) {

		if(path != null &&
				(isAcqPathKnown(path) || new File(path).exists()) && queues != null) {
			
			this.queues = queues;
			
			if(queues.size() > 2) {
				processor = getProcessor(path, getLoader(QUEUES_LOADER));
				
				if(processor != null) {
					processor.startProcess();
					return true;
				}
				return false;
			} else {
				JOptionPane.showMessageDialog(null, "Not enough data points to proceed (minimum 3).",
						"Error", JOptionPane.INFORMATION_MESSAGE);
				processingHasStopped();
				return false;
			}
		} 

		return false;
	}
	
	@Override
	public boolean startProcessor(String path) {	
		if(isReady() && path != null &&
				(isAcqPathKnown(path) || new File(path).exists())) {

			directoriesToLoad = getExposureFolders(path); // use utils instead
			
			if(directoriesToLoad == null) {
				JOptionPane.showMessageDialog(null, "No experimental folder found in:\n" + path + 
						"\n\nExperiment folder names end with <###ms> where ### is the exposure time.",
						"Error", JOptionPane.INFORMATION_MESSAGE);
				processingHasStopped();
				return false;
			} else if(directoriesToLoad.length < 3) {
				JOptionPane.showMessageDialog(null, "Not enough experimental folder (minimum 3).",
						"Error", JOptionPane.INFORMATION_MESSAGE);
				processingHasStopped();
				return false;
			} else if(directoriesToLoad != null && directoriesToLoad.length > 2) {
				processor = getProcessor(path, getLoader(DEFAULT_LOADER));
				processor.startProcess();
				return true;
			}
		} 
		return false;
	}
	
	public boolean startProcessor(String path, HashMap<String, Double> list) {
		// Do nothing
		return false;
	}
	
	//////////////////////// Other methods
	public boolean isAcquisitionDone() {
		return acqDone;
	}

	@Override
	public boolean isReady() {
		if(!acqController.isReady() || procPanel == null || genPanel == null) {
			return false;
		}
		
		if(acqController.acqExists() && acqController.acqRunning()) {
			return false;
		}
		
		if(isProcessingRunning()) {
			return false;
		}
		
		if(isGenerationRunning()) {
			return false;
		}
		
		return true;
	}

	/////////////////////// Private methods
	private String[] getExposureFolders(String path) {
		ArrayList<String> fullpaths = new ArrayList<String>();
		File[] files = new File(path).listFiles();
		for (File file : files) {
			if (file.isDirectory() && file.getName().substring(file.getName().length() - 2).equals("ms")) {
				fullpaths.add(file.getAbsolutePath());
			}
		}

		return fullpaths.toArray(new String[0]);
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
	public CalibrationProcessor<BareImage,FloatImage> getProcessor(String path, Loader<BareImage> loader) {
		if(loader instanceof QueuesLoader) {
			return new QueuesProcessor(path, this, (QueuesLoader) loader);
		} else if (loader instanceof MMStacksLoader) {
			return new StacksProcessor(path, this, (MMStacksLoader) loader);
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
	public ArrayToImage<FloatImage> getImageConverter() {
		return new ArrayToFloatImage();
	}

	@Override
	public ImageSaver<FloatImage> getImageSaver() {
		return new FloatImageSaver();
	}

	public MM2AcquisitionController getAcqController() {
		return acqController;
	}
}
