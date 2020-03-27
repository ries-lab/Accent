package de.embl.rieslab.accent.mm2;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.micromanager.Studio;

import de.embl.rieslab.accent.common.AbstractController;
import de.embl.rieslab.accent.common.data.acquisition.AcquisitionSettings;
import de.embl.rieslab.accent.common.data.image.BareImage;
import de.embl.rieslab.accent.common.interfaces.Loader;
import de.embl.rieslab.accent.common.processor.CalibrationProcessor;
import de.embl.rieslab.accent.common.processor.StacksProcessor;
import de.embl.rieslab.accent.mm2.acquisition.Acquisition;
import de.embl.rieslab.accent.mm2.acquisition.AlternatedAcquisition;
import de.embl.rieslab.accent.mm2.loader.MMStacksLoader;
import de.embl.rieslab.accent.mm2.loader.QueuesLoader;
import de.embl.rieslab.accent.mm2.processor.QueuesProcessor;
import de.embl.rieslab.accent.mm2.ui.MainFrame;

public class MM2Controller extends AbstractController {

	public static String QUEUES_LOADER = "Queues";
	
	private Studio studio;
	private Acquisition acq;
	private AcquisitionSettings acqSettings;
	private String[] directoriesToLoad;
	private ArrayList<ArrayBlockingQueue<BareImage>> queues;

	private boolean acqDone;
	
	public MM2Controller(Studio studio) {
		if(studio == null) {
			throw new NullPointerException();
		}
		this.studio = studio;
	}
	
	/////////////// Acquisition
	public boolean startAcquisition(AcquisitionSettings settings) {
		if(isReady()) {
			acqDone = false;
			acqSettings = settings;
					
			acq = new AlternatedAcquisition(studio, acqSettings, this);
			
			acq.start();
			
			if(acqSettings.parallelProcessing) {
				boolean b = startProcessor(settings.folder_, acq.getQueues());
				if(!b) {
					System.out.println("Processor failed to start");
				}
			} 
			
			return true;
		}
		return false;
	}
	
	public void stopAcquisition() {
		if(acq != null) {
			acq.stop();
			acqDone = true;
		}
	}
	
	public void updateAcquisitionProgress(String progressText, int progress) {
		acqPanel.setProgress(progressText, progress);
	}
	
	public void acquisitionHasStarted() {
		acqPanel.acqHasStarted();
	}
	
	public void acquisitionHasStopped() {
		acqPanel.acqHasStopped();
		
		setProcessorPanelPath(acqSettings.folder_);
	}
	
	public void acquisitionHasEnded() {
		acqDone = true;
		acqPanel.acqHasEnded();
		setProcessorPanelPath(acqSettings.folder_);
		System.out.println("Acquisition running time (s): "+acq.getExecutionTime());
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
		if(acqPanel == null || procPanel == null || genPanel == null) {
			return false;
		}
		
		if(acq != null && acq.isRunning()) {
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
		if(acqSettings != null && acqSettings.folder_ != null) {
			return acqSettings.folder_.equals(path);
		}
		return false;
	}

	@Override
	public JFrame getMainFrame() {		
		MainFrame frame = new MainFrame(studio, this);		
		return frame;
	}

	@Override
	public Loader getLoader(String parameter) {
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
	public CalibrationProcessor getProcessor(String path, Loader loader) {
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

}
