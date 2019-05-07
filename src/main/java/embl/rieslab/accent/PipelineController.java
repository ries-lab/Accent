package main.java.embl.rieslab.accent;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.micromanager.Studio;

import main.java.embl.rieslab.accent.acquisition.Acquisition;
import main.java.embl.rieslab.accent.acquisition.AlternatedAcquisition;
import main.java.embl.rieslab.accent.common.data.calibration.Calibration;
import main.java.embl.rieslab.accent.common.data.calibration.CalibrationIO;
import main.java.embl.rieslab.accent.common.generator.AvgVarMapsGenerator;
import main.java.embl.rieslab.accent.common.interfaces.AcquisitionPanelInterface;
import main.java.embl.rieslab.accent.common.interfaces.GeneratePanelInterface;
import main.java.embl.rieslab.accent.common.interfaces.Generator;
import main.java.embl.rieslab.accent.common.interfaces.ProcessingPanelInterface;
import main.java.embl.rieslab.accent.common.processor.CalibrationProcessor;
import main.java.embl.rieslab.accent.fiji.data.image.DatasetExposurePair;
import main.java.embl.rieslab.accent.fiji.loader.CurrentImgsLoader;
import main.java.embl.rieslab.accent.fiji.processor.FloatImageProcessor;
import main.java.embl.rieslab.accent.mm2.data.acquisition.AcquisitionSettings;
import main.java.embl.rieslab.accent.mm2.loader.MMStacksLoader;
import main.java.embl.rieslab.accent.mm2.loader.QueuesLoader;
import main.java.embl.rieslab.accent.mm2.processor.MMStacksProcessor;
import main.java.embl.rieslab.accent.mm2.processor.QueuesProcessor;

public class PipelineController {

	private Studio studio;
	private AcquisitionPanelInterface acqPanel;
	private Acquisition acq;
	private AcquisitionSettings acqSettings;
	private ProcessingPanelInterface procPanel;
	private CalibrationProcessor<?> proc;
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
			acq = new AlternatedAcquisition(studio, acqSettings, this);
			acq.start();
			
			if(acqSettings.parallelProcessing) {
				proc = new QueuesProcessor(acqSettings.folder_, this, new QueuesLoader(acq.getQueues()));
				proc.startProcess();
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
		if(!fiji && isReady() && path != null &&
				(isAcqPathKnown(path) || new File(path).exists())) {
			
			String[] directories = getExposureFolders(path);
			
			if(directories.length > 0) {
				proc = new MMStacksProcessor(path, this, new MMStacksLoader(studio, directories));
				proc.startProcess();
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
	
	public boolean startProcessor(String path, List<DatasetExposurePair> extractedDatasets) {
		if(isReady() && path != null &&
				(isAcqPathKnown(path) || new File(path).exists())) {
			
			// sanity check on the datasets
			List<String> smallDatasets = new ArrayList<String>();
			List<String> badDatasets = new ArrayList<String>();
			List<String> noTimeDatasets = new ArrayList<String>();
			Iterator<DatasetExposurePair> it = extractedDatasets.iterator();
			while(it.hasNext()) {
				DatasetExposurePair dataset = it.next();
				if(dataset.getImage().numDimensions() > 3) {
					it.remove();
					badDatasets.add(dataset.getImage().getName());
				}
				if(dataset.getImage().getFrames() == 1) {
					it.remove();
					noTimeDatasets.add(dataset.getImage().getName());
				} else if(dataset.getImage().getFrames() < 1000) {
					smallDatasets.add(dataset.getImage().getName());
				}
			}
			
			// informs user that datasets were removed
			if(badDatasets.size() > 0) {
				String bad = "";
				for(String s : badDatasets) {
					bad += s+"\n";
				}
				
				JOptionPane.showMessageDialog(new JFrame(), "The following datasets had the wrong number of dimensions and were removed: \n"+bad,
						"Invalid datasets", JOptionPane.INFORMATION_MESSAGE);
			}
			
			// informs user that some datasets are too small
			if(smallDatasets.size() > 0) {
				String small = "";
				for(String s : smallDatasets) {
					small += s+"\n";
				}
				
				JOptionPane.showMessageDialog(new JFrame(), "The following datasets are small and might lead to an inaccurate calibration: \n"+small,
						"Small datasets", JOptionPane.INFORMATION_MESSAGE);
			}

			
			// informs user that some datasets don't have the time dimension
			if(noTimeDatasets.size() > 0) {
				String noTime = "";
				for(String s : noTimeDatasets) {
					noTime += s+"\n";
				}
				
				JOptionPane.showMessageDialog(new JFrame(), "The following datasets do not have a time axis and have been removed: \n"+noTime,
						"Small datasets", JOptionPane.INFORMATION_MESSAGE);
			}
			
			
			// need at least three points to fit
			if(extractedDatasets.size() > 2) {
				if(fiji) {
					proc = new FloatImageProcessor(path, this, new CurrentImgsLoader(extractedDatasets));
					proc.startProcess();
					return true;
				}
			} else {
				JOptionPane.showMessageDialog(new JFrame(), "The processing pipeline was not given enough datasets to proceed (minimum of three).",
						"Error", JOptionPane.INFORMATION_MESSAGE);
				processingHasStopped();
				return false;
			}
		} 
		
		return false;
	}

	public void stopProcessor() {
		if(proc != null) {
			proc.stopProcess();
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
		if((acqPanel == null && !fiji) || procPanel == null || genPanel == null) {
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
