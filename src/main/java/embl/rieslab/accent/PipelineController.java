package main.java.embl.rieslab.accent;

import java.io.File;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import org.micromanager.Studio;

import main.java.embl.rieslab.accent.acquisition.Acquisition;
import main.java.embl.rieslab.accent.acquisition.AcquisitionFactory;
import main.java.embl.rieslab.accent.acquisition.AcquisitionPanelInterface;
import main.java.embl.rieslab.accent.acquisition.AcquisitionSettings;
import main.java.embl.rieslab.accent.calibration.Calibration;
import main.java.embl.rieslab.accent.calibration.CalibrationIO;
import main.java.embl.rieslab.accent.generator.AvgVarMapsGenerator;
import main.java.embl.rieslab.accent.generator.GeneratePanelInterface;
import main.java.embl.rieslab.accent.generator.Generator;
import main.java.embl.rieslab.accent.loader.IJLoader;
import main.java.embl.rieslab.accent.loader.MMStacksLoader;
import main.java.embl.rieslab.accent.loader.QueuesLoader;
import main.java.embl.rieslab.accent.processing.IJProcessor;
import main.java.embl.rieslab.accent.processing.MMStacksProcessor;
import main.java.embl.rieslab.accent.processing.ProcessingPanelInterface;
import main.java.embl.rieslab.accent.processing.Processor;
import main.java.embl.rieslab.accent.processing.QueuesProcessor;

public class PipelineController {

	private Studio studio;
	private AcquisitionPanelInterface acqPanel;
	private Acquisition acq;
	private AcquisitionSettings acqSettings;
	private ProcessingPanelInterface procPanel;
	private Processor proc;
	private GeneratePanelInterface genPanel;
	private Generator gen;
	
	private boolean acqDone;
	private boolean fiji;

	public PipelineController() {
		fiji = true;
	}
	
	public PipelineController(Studio studio) {
		if(studio == null) {
			throw new NullPointerException();
		}
		fiji = false;
		this.studio = studio;
	}
	
	/////////////// Acquisition
	
	public boolean startAcquisition(AcquisitionSettings settings) {
		if(!fiji && isReady()) {
			acqDone = false;
			acqSettings = settings;
			acq = AcquisitionFactory.getFactory().getAcquisition(studio, acqSettings, this);
			acq.start();
			
			if(acqSettings.parallelProcessing) {
				proc = new QueuesProcessor(acqSettings.folder_, this, new QueuesLoader(acq.getQueues()));
				proc.start();
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
	
	public boolean startProcessor(String path) {		
		if(isReady() && path != null &&
				(isAcqPathKnown(path) || new File(path).exists())) {
			
			String[] directories = getExposureFolders(path);
			
			if(directories.length > 0) {
				if(!fiji) {
					proc = new MMStacksProcessor(path, this, new MMStacksLoader(studio, directories));
				} else {
					proc = new IJProcessor(path, this, new IJLoader(directories)); 
				}
				proc.start();
				return true;
			} else {
				JOptionPane.showMessageDialog(null, "No experimental folder found in:\n" + path + 
						"\n\nExperiment folder names end with <###ms> where ### is the exposure time.",
						"Error", JOptionPane.INFORMATION_MESSAGE);
				processingHasStopped();
				return false;
			}
		} 
		return false;
	}

	public void stopProcessor() {
		if(proc != null) {
			proc.stop();
		}
	}
	
	public void updateProcessorProgress(String progressString, int progress) {
		procPanel.setProgress(progressString, progress);
	}

	public void processingHasStopped() {
		procPanel.processingHasStopped();
	}

	public void processingHasStarted() {
		procPanel.processingHasStarted();
	}
	
	public void processingHasEnded() {
		procPanel.processingHasEnded();
		System.out.println("Processing running time (s): "+proc.getExecutionTime());
		
		// set path to calibration file on generation panel and start
		genPanel.setCalibrationPath(proc.getCalibrationPath());

		if(isReady()) {
			Integer[] exposures = genPanel.getExposures();
			if(exposures.length > 0) {
				gen = new AvgVarMapsGenerator(this);
				gen.generate(new File(proc.getCalibrationPath()).getParentFile().getAbsolutePath(), 
						proc.getCalibration(), exposures);
			}
		}
	}

	public boolean isProcessingRunning() {
		if(proc != null && proc.isRunning()) {
			return true;
		}
		return false;
	}

	
	//////// map generation
	public boolean startMapGeneration(String path, Integer[] exposures) {		
		if(isReady() && path != null &&
				(new File(path).exists()) && (exposures != null && exposures.length > 0)) {
			
			if(path.endsWith(CalibrationIO.CALIB_EXT)) {
				File calibFile = new File(path);
				Calibration calib = CalibrationIO.read(calibFile);		
				String parent = calibFile.getParentFile().getAbsolutePath();
				gen = new AvgVarMapsGenerator(this);
				gen.generate(parent, calib, exposures);
				return true;
			} else {
				JOptionPane.showMessageDialog(null, path + 
						"\n\nis not a calibration file.",
						"Error", JOptionPane.INFORMATION_MESSAGE);
				return false;
			}
		}
		return false;
	}
	
	public boolean isGenerationRunning() {
		if(gen != null) {
			return gen.isRunning();
		}
		return false;
	}
	
	public void setGeneratorProgress(String progress) {
		if(genPanel != null) {
			genPanel.setProgress(progress);
		}
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
	
	public void setGeneratePanel(GeneratePanelInterface genpane) {
		this.genPanel = genpane;
	}
		
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

}
