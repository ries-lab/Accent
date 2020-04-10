package de.embl.rieslab.accent.mm2.acquisition;

import de.embl.rieslab.accent.mm2.MM2Controller;
import de.embl.rieslab.accent.mm2.interfaces.AcquisitionController;
import de.embl.rieslab.accent.mm2.interfaces.AcquisitionPanelInterface;

public class MM2AcquisitionController implements AcquisitionController {

	private MM2Controller controller;
	private Acquisition acq;
	private AcquisitionSettings acqSettings;
	private AcquisitionPanelInterface acqPanel;
	private boolean acqDone;
	private boolean ready;
	
	public MM2AcquisitionController(MM2Controller controller) {
		this.controller = controller;
		
		ready = false;
		acqDone = true;
	}
	
	/////////////// Acquisition
	@Override
	public boolean startAcquisition(AcquisitionSettings settings) {
		if(controller.isReady()) {
			acqDone = false;
			acqSettings = settings;
					
			acq = new AlternatedAcquisition(controller.getStudio(), acqSettings, this);
			
			acq.start();
			
			if(acqSettings.parallelProcessing) {
				boolean b = controller.startProcessor(settings.folder_, acq.getQueues());
				if(!b) {
					System.out.println("Processor failed to start");
				}
			} 
			
			return true;
		}
		return false;
	}

	@Override
	public void stopAcquisition() {
		if(acq != null) {
			acq.stop();
			acqDone = true;
		}
	}

	@Override
	public void updateAcquisitionProgress(String progressText, int progress) {
		acqPanel.setProgress(progressText, progress);
	}

	@Override
	public void acquisitionHasStarted() {
		acqPanel.acqHasStarted();
	}

	@Override
	public void acquisitionHasStopped() {
		acqPanel.acqHasStopped();
		
		controller.setProcessorPanelPath(acqSettings.folder_);
	}

	@Override
	public void acquisitionHasEnded() {
		acqDone = true;
		acqPanel.acqHasEnded();
		controller.setProcessorPanelPath(acqSettings.folder_);
		System.out.println("Acquisition running time (s): "+acq.getExecutionTime());
	}	

	@Override
	public void setAcquisitionPanel(AcquisitionPanelInterface acqpane) {
		this.acqPanel = acqpane;
		ready = true;
	}
	
	public boolean isAcqPathKnown(String path) {
		if(acqSettings != null && acqSettings.folder_ != null) {
			return acqSettings.folder_.equals(path);
		}
		return false;
	}
	
	public boolean isReady() {
		return ready;
	}
	
	public boolean isAcqDone() {
		return acqDone;
	}

	public boolean acqExists() {
		return acq != null;
	}
	
	public boolean acqRunning() {
		return acq.isRunning();
	}
}
