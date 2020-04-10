package de.embl.rieslab.accent.mm2.dummys;

import de.embl.rieslab.accent.mm2.acquisition.AcquisitionSettings;
import de.embl.rieslab.accent.mm2.interfaces.AcquisitionController;
import de.embl.rieslab.accent.mm2.interfaces.AcquisitionPanelInterface;

public class DummyAcquisitionController implements AcquisitionController {

	@Override
	public boolean startAcquisition(AcquisitionSettings settings) {
		return true;
	}

	@Override
	public void stopAcquisition() {}

	@Override
	public void updateAcquisitionProgress(String progressText, int progress) {}

	@Override
	public void acquisitionHasStarted() {}

	@Override
	public void acquisitionHasStopped() {}

	@Override
	public void acquisitionHasEnded() {}

	@Override
	public void setAcquisitionPanel(AcquisitionPanelInterface acqpane) {}

	@Override
	public boolean isAcquisitionDone() {
		return true;
	}

}
