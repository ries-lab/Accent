package main.java.embl.rieslab.photonfreecamcalib;

import java.io.File;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import org.micromanager.Studio;

import main.java.embl.rieslab.photonfreecamcalib.acquisition.Acquisition;
import main.java.embl.rieslab.photonfreecamcalib.acquisition.AcquisitionFactory;
import main.java.embl.rieslab.photonfreecamcalib.acquisition.AcquisitionPanelInterface;
import main.java.embl.rieslab.photonfreecamcalib.acquisition.AcquisitionSettings;
import main.java.embl.rieslab.photonfreecamcalib.analysis.AnalysisPanelInterface;
import main.java.embl.rieslab.photonfreecamcalib.analysis.Analyzer;
import main.java.embl.rieslab.photonfreecamcalib.analysis.CameraCalibrationAnalyzer;
import main.java.embl.rieslab.photonfreecamcalib.processing.AvgAndVarProcessor;
import main.java.embl.rieslab.photonfreecamcalib.processing.ProcessingPanelInterface;
import main.java.embl.rieslab.photonfreecamcalib.processing.Processor;

public class PipelineController {

	private Studio studio;
	private AcquisitionPanelInterface acqPanel;
	private Acquisition acq;
	private AcquisitionSettings acqSettings;
	private ProcessingPanelInterface procPanel;
	private Processor proc;
	private AnalysisPanelInterface analysisPanel;
	private Analyzer analyzer;
		
	
	public PipelineController(Studio studio) {
		
		if(studio == null) {
			throw new NullPointerException();
		}
		
		this.studio = studio;
	}
	
	/////////////// Acquisition
	
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
		acqPanel.acqHasEnded();
		setProcessorParameters(acqSettings.folder_);
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
			
			String[] directories = getExposureFolders(path);
			
			if(directories.length > 0) {
				proc = new AvgAndVarProcessor(studio, directories, this);
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
		procPanel.processingHasEnded();
		setAnalyzerParameters(proc.getCurrentParentPath());
	}
	
	//////////// Analysis
	
	private void setAnalyzerParameters(String folder) {		
		if(new File(folder).exists()) {
			analysisPanel.setDataPath(folder);
		}
	}
	
	public void startAnalyzer(String path) {	
		if(isReady() && path != null &&
				(isAcqPathKnown(path) || new File(path).exists()) && (analyzer == null || !analyzer.isRunning())) {

			String[] avgs = getAverageImages(path);
			String[] vars = getVarianceImages(path);

			if(avgs.length > 0 && avgs.length == vars.length) {
				analyzer = new CameraCalibrationAnalyzer(avgs, vars, this);
				analyzer.start();
			}
		}
	}
	
	public void stopAnalyzer() {
		if(analyzer != null) {
			analyzer.stop();
		}
	}
	
	public void updateAnalyzerProgress(int progress) {
		analysisPanel.setProgress(progress);
	}

	public void analysisHasStopped() {
		analysisPanel.analysisHasStopped();
	}

	public void analysisHasStarted() {
		analysisPanel.analysisHasStarted();
	}
	
	public void analysisHasEnded() {
		analysisPanel.analysisHasEnded();
	}

	//////////////////////// Other methods
	
	public void setAcquisitionPanel(AcquisitionPanelInterface acqpane) {
		this.acqPanel = acqpane;
	}

	public void setProcessingPanel(ProcessingPanelInterface procpane) {
		this.procPanel = procpane;
	}
	
	public void setAnalysisPanel(AnalysisPanelInterface analysisPanel) {
		this.analysisPanel = analysisPanel;
	}
	
	public boolean isReady() {
		return (acqPanel != null && procPanel != null && analysisPanel != null);
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

	private String[] getAverageImages(String path) {
		ArrayList<String> fullpaths = new ArrayList<String>();
		File[] files = new File(path).listFiles();
		for (File file : files) {
			if (!file.isDirectory() && file.getName().substring(0,3).equals("Avg") 
				&& file.getName().substring(file.getName().length() - 7).equals("ms.tiff")) {
				fullpaths.add(file.getAbsolutePath());
			}
		}

		return fullpaths.toArray(new String[0]);
	}
	
	private String[] getVarianceImages(String path) {
		ArrayList<String> fullpaths = new ArrayList<String>();
		File[] files = new File(path).listFiles();
		for (File file : files) {
			if (!file.isDirectory() && file.getName().substring(0,3).equals("Var") 
				&& file.getName().substring(file.getName().length() - 7).equals("ms.tiff")) {
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