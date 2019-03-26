package main.java.embl.rieslab.photonfreecamcalib.processing;

import org.micromanager.Studio;

import main.java.embl.rieslab.photonfreecamcalib.PipelineController;


public class ProcessorFactory {

	private static ProcessorFactory factory;
	
	private ProcessorFactory() {}
	
	public static ProcessorFactory getFactory() {
		if(factory == null) {
			factory = new ProcessorFactory();
		}
		return factory;
	}
	
	public Processor getProcessor(Studio studio, String[] directories, PipelineController controller) {
		return new AvgAndVarProcessor(studio, directories, controller);
	}
}
