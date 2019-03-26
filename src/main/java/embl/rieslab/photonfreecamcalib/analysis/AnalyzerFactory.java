package main.java.embl.rieslab.photonfreecamcalib.analysis;

import main.java.embl.rieslab.photonfreecamcalib.PipelineController;

public class AnalyzerFactory {

	private static AnalyzerFactory factory;
	
	private AnalyzerFactory() {}
	
	public static AnalyzerFactory getFactory() {
		if(factory == null) {
			factory = new AnalyzerFactory();
		}
		return factory;
	}
	
	public Analyzer getProcessor(String[] avgs, String[] vars, PipelineController controller) {
		return new CameraCalibrationAnalyzer(avgs, vars, controller);
	}
}
