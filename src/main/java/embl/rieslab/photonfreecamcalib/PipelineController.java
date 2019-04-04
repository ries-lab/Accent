package main.java.embl.rieslab.photonfreecamcalib;

import java.io.File;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import org.micromanager.Studio;
import org.micromanager.data.Datastore;

import main.java.embl.rieslab.photonfreecamcalib.acquisition.Acquisition;
import main.java.embl.rieslab.photonfreecamcalib.acquisition.AcquisitionFactory;
import main.java.embl.rieslab.photonfreecamcalib.acquisition.AcquisitionPanelInterface;
import main.java.embl.rieslab.photonfreecamcalib.acquisition.AcquisitionSettings;
import main.java.embl.rieslab.photonfreecamcalib.processing.CalibrationProcessor;
import main.java.embl.rieslab.photonfreecamcalib.processing.ConcurrentCalibrationProcessor;
import main.java.embl.rieslab.photonfreecamcalib.processing.ProcessingPanelInterface;
import main.java.embl.rieslab.photonfreecamcalib.processing.Processor;

public class PipelineController {

	private Studio studio;
	private AcquisitionPanelInterface acqPanel;
	private Acquisition acq;
	private AcquisitionSettings acqSettings;
	private ProcessingPanelInterface procPanel;
	private Processor proc;
	
	private boolean acqDone, procDone;
		
	
	public PipelineController(Studio studio) {
		
		if(studio == null) {
			throw new NullPointerException();
		}
		
		this.studio = studio;
	}
	
	/////////////// Acquisition
	
	public void startAcquisition(AcquisitionSettings settings) {
		if(isReady() && (acq == null || !acq.isRunning())) {
			acqDone = false;
			acqSettings = settings;
			acq = AcquisitionFactory.getFactory().getAcquisition(studio, acqSettings, this);
			acq.start();
			
			if(acqSettings.onlineAnalysis) {
				procDone = false;
				proc = new ConcurrentCalibrationProcessor(acqSettings.folder_, acq.getQueues(), this);
				proc.start();
			} 
			
		}
	}
	
	public void stopAcquisition() {
		if(acq != null) {
			acq.stop();
			acqDone = true;
		}
	}
	
	public void updateAcquisitionProgress(int progress) {
		acqPanel.setProgress(progress);
	}
	
	public void acquisitionHasStarted() {
		acqPanel.acqHasStarted();
	}
	
	public void acquisitionHasStopped() {
		acqPanel.acqHasStopped();
		setProcessorParameters(acqSettings.folder_);
	}
	
	public void acquisitionHasEnded() {
		acqDone = true;
		acqPanel.acqHasEnded();
		setProcessorParameters(acqSettings.folder_);
		System.out.println("Acquisition running time (s): "+acq.getExecutionTime());
	}	
	
	//////// Processing
	
	private void setProcessorParameters(String folder) {		
		if(new File(folder).exists()) {
			procPanel.setDataPath(folder);
		}
	}
	
	public void startProcessor(String path) {		
		if(isReady() && path != null &&
				(isAcqPathKnown(path) || new File(path).exists()) && (proc == null || !proc.isRunning())) {
			
			procDone = false;
			String[] directories = getExposureFolders(path);
			
			if(directories.length > 0) {
				proc = new CalibrationProcessor(studio, directories, this);
				proc.start();
			} else {
				JOptionPane.showMessageDialog(null, "No experimental folder found in:\n" + path + 
						"\n\nExperiment folder names end with <###ms> where ### is the exposure time.",
						"Error", JOptionPane.INFORMATION_MESSAGE);
				processingHasStopped();
			}
		}
	}

	public void stopProcessor() {
		if(proc != null) {
			procDone = true;
			proc.stop();
		}
	}
	
	public void updateProcessorProgress(int progress) {
		procPanel.setProgress(progress);
	}

	public void processingHasStopped() {
		procPanel.processingHasStopped();
	}

	public void processingHasStarted() {
		procPanel.processingHasStarted();
	}
	
	public void processingHasEnded() {
		procDone = true;
		procPanel.processingHasEnded();
		System.out.println("Processing running time (s): "+proc.getExecutionTime());
	}
	

	//////////////////////// Other methods
	
	public boolean isAcquisitionDone() {
		return acqDone;
	}
	
	public void setAcquisitionPanel(AcquisitionPanelInterface acqpane) {
		this.acqPanel = acqpane;
	}

	public void setProcessingPanel(ProcessingPanelInterface procpane) {
		this.procPanel = procpane;
	}
		
	public boolean isReady() {
		return (acqPanel != null && procPanel != null);
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

}
