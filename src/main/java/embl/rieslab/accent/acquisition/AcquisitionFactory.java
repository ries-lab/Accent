package main.java.embl.rieslab.accent.acquisition;

import org.micromanager.Studio;

import main.java.embl.rieslab.accent.PipelineController;

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
		return new AlternatedAcquisition(studio, settings, controller);
	}
}
