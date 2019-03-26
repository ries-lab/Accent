package main.java.embl.rieslab.photonfreecamcalib.acquisition;

import org.micromanager.Studio;
import main.java.embl.rieslab.photonfreecamcalib.PipelineController;

public class AcquisitionFactory {

	private static AcquisitionFactory factory;
	
	private AcquisitionFactory() {}
	
	public static AcquisitionFactory getFactory() {
		if(factory == null) {
			factory = new AcquisitionFactory();
		}
		return factory;
	}
	
	public Acquisition getAcquisition(Studio studio, AcquisitionSettings settings, PipelineController controller) {
		if(!settings.simultaneousAcq) {
			return new SequentialAcquisition(studio, settings, controller);
		} else {
			return new MultiplexedAcquisition(studio, settings, controller);
		}
	}
}
