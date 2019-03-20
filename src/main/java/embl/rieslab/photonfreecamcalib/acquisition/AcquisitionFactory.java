package main.java.embl.rieslab.photonfreecamcalib.acquisition;

import org.micromanager.Studio;

public class AcquisitionFactory {

	private static AcquisitionFactory factory;
	
	private AcquisitionFactory() {}
	
	public static AcquisitionFactory getFactory() {
		if(factory == null) {
			factory = new AcquisitionFactory();
		}
		return factory;
	}
	
	public Acquisition getAcquisition(Studio studio, AcquisitionSettings settings, AcquisitionPanelInterface panel) {
		if(!settings.simultaneousAcq) {
			return new SequentialAcquisition(studio, settings, panel);
		} else {
			return new MultiplexedAcquisition(studio, settings, panel);
		}
	}
}
