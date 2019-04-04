package main.java.embl.rieslab.photonfreecamcalib.processing;

import main.java.embl.rieslab.photonfreecamcalib.acquisition.AcquisitionSettings;

public class ProcessorFactory {

	private static ProcessorFactory factory;
	
	public static ProcessorFactory getFactory() {
		if(factory == null) {
			factory = new ProcessorFactory();
		}
		return factory;
	}
	
	public Processor getProcessor(AcquisitionSettings settings) {
		return null;
	}
	
}
