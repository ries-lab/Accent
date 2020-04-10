package de.embl.rieslab.accent.common;

import java.io.File;

import javax.swing.JOptionPane;

import de.embl.rieslab.accent.common.data.calibration.Calibration;
import de.embl.rieslab.accent.common.data.calibration.CalibrationIO;
import de.embl.rieslab.accent.common.generator.AvgVarMapsGenerator;
import de.embl.rieslab.accent.common.interfaces.data.CalibrationImage;
import de.embl.rieslab.accent.common.interfaces.data.RawImage;
import de.embl.rieslab.accent.common.interfaces.pipeline.Generator;
import de.embl.rieslab.accent.common.interfaces.pipeline.PipelineController;
import de.embl.rieslab.accent.common.interfaces.ui.GeneratePanelInterface;
import de.embl.rieslab.accent.common.interfaces.ui.ProcessingPanelInterface;
import de.embl.rieslab.accent.common.processor.CalibrationProcessor;
import de.embl.rieslab.accent.common.utils.Dialogs;


public abstract class AbstractController<U extends RawImage, T extends CalibrationImage> implements PipelineController<U, T>{

	public static String DEFAULT_LOADER = "Default";
	
	protected ProcessingPanelInterface procPanel;
	protected CalibrationProcessor<U,T> processor;
	protected GeneratePanelInterface genPanel;
	protected Generator generator;
	
	// Processor		
	public boolean startProcessor(String path) {

		if(isReady() && path != null) {
			if(!(new File(path).exists())) {
				boolean b = (new File(path)).mkdirs();
				if(!b) {
					Dialogs.showErrorMessage("Could not create:\n"+path);
					return false;
				}
			}
	
			processor = getProcessor(path, getLoader(DEFAULT_LOADER));
			if (isProcessorReady()) {
				processor.startProcess();
				return true;
			}
		}
		
		return false;
	}

	public boolean isProcessorReady() {
		if(processor != null && processor.getLoader() != null) {
			return true;
		}
		return false;
	}
	
	public void stopProcessor() {
		if(processor != null) {
			processor.stopProcess();
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
		System.out.println("Processing running time (s): "+processor.getExecutionTime());
		
		if(isReady()) {	
			// set path to calibration file on generation panel and start
			genPanel.setCalibrationPath(processor.getCalibrationPath());
			
			double[] exposures = genPanel.getExposures();
			if(exposures.length > 0) {
				generator = new AvgVarMapsGenerator<U,T>(this);
				generator.generate(new File(processor.getCalibrationPath()).getParentFile().getAbsolutePath(), 
						processor.getCalibration(), exposures);
			}
		}
	}
	
	public boolean isProcessingRunning() {
		if(processor != null && processor.isRunning()) {
			return true;
		}
		return false;
	}
	
	//////// map generation
	public boolean startMapGeneration(String path, double[] exposures) {		
		if(isReady() && path != null &&
				(new File(path).exists()) && (exposures != null && exposures.length > 0)) {
			
			if(path.endsWith(CalibrationIO.CALIB_EXT)) {
				File calibFile = new File(path);
				Calibration calib = CalibrationIO.read(calibFile);		
				String parent = calibFile.getParentFile().getAbsolutePath();
				generator = new AvgVarMapsGenerator<U,T>(this);
				generator.generate(parent, calib, exposures);
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
		if(generator != null) {
			return generator.isRunning();
		}
		return false;
	}
	
	public void updateGeneratorProgress(String progress) {
		if(genPanel != null) {
			genPanel.setProgress(progress);
		}
	}
	
	public void setProcessorPanelPath(String path) {
		if(procPanel != null) {
			procPanel.setDataPath(path);
		}
	}
	
	//////////////////////// Other methods
	public void setProcessingPanel(ProcessingPanelInterface procpane) {
		this.procPanel = procpane;
	}
	
	public void setGeneratePanel(GeneratePanelInterface genpane) {
		this.genPanel = genpane;
	}

	public boolean isReady() {
		if(procPanel == null || genPanel == null) {
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
}
