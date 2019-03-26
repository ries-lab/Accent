package main.java.embl.rieslab.photonfreecamcalib;

import java.io.File;
import java.util.ArrayList;

import org.micromanager.Studio;

import main.java.embl.rieslab.photonfreecamcalib.acquisition.Acquisition;
import main.java.embl.rieslab.photonfreecamcalib.acquisition.AcquisitionFactory;
import main.java.embl.rieslab.photonfreecamcalib.acquisition.AcquisitionPanelInterface;
import main.java.embl.rieslab.photonfreecamcalib.acquisition.AcquisitionSettings;
import main.java.embl.rieslab.photonfreecamcalib.processing.ProcessingPanelInterface;
import main.java.embl.rieslab.photonfreecamcalib.processing.Processor;
import main.java.embl.rieslab.photonfreecamcalib.processing.ProcessorFactory;
import main.java.embl.rieslab.photonfreecamcalib.ui.AcquirePanel;
import main.java.embl.rieslab.photonfreecamcalib.ui.ProcessPanel;

public class PipelineController {

	private Studio studio;
	private AcquisitionPanelInterface acqpane;
	private Acquisition acq;
	private AcquisitionSettings acqSettings;
	private ProcessingPanelInterface procpane;
	private Processor proc;
		
	
	public PipelineController(Studio studio) {
		
		if(studio == null) {
			throw new NullPointerException();
		}
		
		this.studio = studio;
	}
	
	public void startAcquisition(AcquisitionSettings settings) {
		if(isReady() && (acq == null || !acq.isRunning())) {
			acqSettings = settings;
			acq = AcquisitionFactory.getFactory().getAcquisition(studio, acqSettings, this);
			acq.start();
		}
	}
	
	public void stopAcquisition() {
		if(acq != null) {
			acq.stop();
		}
	}
	
	public void updateAcquisitionProgress(int progress) {
		acqpane.setProgress(progress);
	}
	
	public void acquisitionHasStarted() {
		acqpane.acqHasStarted();
	}
	
	public void acquisitionHasStopped() {
		acqpane.acqHasStopped();
		setProcessorParameters(acqSettings.folder_);
	}
	
	public void acquisitionHasEnded() {
		acqpane.acqHasEnded();
		setProcessorParameters(acqSettings.folder_);
	}	
	
	private boolean isAcqPathKnown(String path) {
		if(acqSettings != null && acqSettings.folder_ != null) {
			return acqSettings.folder_.equals(path);
		}
		return false;
	}
	
	private void setProcessorParameters(String folder) {		
		if(new File(folder).exists()) {
			procpane.setDataPath(folder);
		}
	}
	
	public void startProcessor(String path) {		
		if(isReady() && path != null &&
				(isAcqPathKnown(path) || new File(path).exists()) && (proc == null || !proc.isRunning())) {
			
			String[] directories = getExposureFolders(path);
			
			if(directories.length > 0) {
				proc = ProcessorFactory.getFactory().getProcessor(studio, directories, this);
				proc.start();
			}
		}
	}
	
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

	public void stopProcessor() {
		if(proc != null) {
			proc.stop();
		}
	}
	
	public void updateProcessorProgress(int progress) {
		procpane.setProgress(progress);
	}

	public void processingHasStopped() {
		procpane.procHasStopped();
	}

	public void processingHasStarted() {
		procpane.procHasStarted();
	}
	
	public void processingHasEnded() {
		procpane.procHasEnded();
	}

	public void addAcquisitionPanel(AcquirePanel acqpane) {
		this.acqpane = acqpane;
	}

	public void addProcessingPanel(ProcessPanel procpane) {
		this.procpane = procpane;
	}
	
	public boolean isReady() {
		return (acqpane != null && procpane != null);
	}
}
