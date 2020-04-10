package de.embl.rieslab.accent.mm2.interfaces;

import de.embl.rieslab.accent.mm2.acquisition.AcquisitionSettings;

public interface AcquisitionController {
	
	public boolean startAcquisition(AcquisitionSettings settings);
	
	public void stopAcquisition();

	public void updateAcquisitionProgress(String progressText, int progress);

	public void acquisitionHasStarted();

	public void acquisitionHasStopped();

	public void acquisitionHasEnded();

	public void setAcquisitionPanel(AcquisitionPanelInterface acqpane);
	
	public boolean isAcquisitionDone();
}
