package de.embl.rieslab.accent.mm2.processor;

import de.embl.rieslab.accent.common.data.calibration.Calibration;
import de.embl.rieslab.accent.common.interfaces.pipeline.Loader;
import de.embl.rieslab.accent.common.interfaces.pipeline.PipelineController;
import de.embl.rieslab.accent.common.processor.CalibrationProcessor;
import de.embl.rieslab.accent.mm2.data.image.FloatImage;

public abstract class MM2Processor extends CalibrationProcessor{

	public MM2Processor(String folder, PipelineController controller, Loader loader) {
		super(folder, controller, loader);
	}

	@Override
	protected void writeCalibrationToImages(String folder, Calibration results) {
		// Writes the results as images
		FloatImage baseline = new FloatImage(results.getWidth(), results.getHeight(), results.getBaseline(), 0);
		baseline.saveAsTiff(folder+"\\Baseline.tiff");
		//ImagePlus baseline_imp = new ImagePlus("Baseline", baseline.getImage());
		//new HistogramWindow(baseline_imp).showHistogram(baseline_imp, baseline_imp.getAllStatistics());
		
		FloatImage dc_per_sec = new FloatImage(results.getWidth(), results.getHeight(), results.getDcPerSec(), 0);
		dc_per_sec.saveAsTiff(folder+"\\DC_per_sec.tiff");
		//HistogramWindow hw_dc_per_sec = new HistogramWindow(new ImagePlus("DC per sec", dc_per_sec.getImage()));
		
		FloatImage gain = new FloatImage(results.getWidth(), results.getHeight(), results.getGain(), 0);
		gain.saveAsTiff(folder+"\\Gain.tiff");
		//HistogramWindow hw_gain = new HistogramWindow(new ImagePlus("Gain", gain.getImage()));
		
		FloatImage rn_sq = new FloatImage(results.getWidth(), results.getHeight(), results.getRnSq(), 0);
		rn_sq.saveAsTiff(folder+"\\RN_sq.tiff");
		//HistogramWindow hw_rn_sq = new HistogramWindow(new ImagePlus("RN square", rn_sq.getImage()));
		
		FloatImage tn_sq_per_sec = new FloatImage(results.getWidth(), results.getHeight(), results.getTnSqPerSec(), 0);
		tn_sq_per_sec.saveAsTiff(folder+"\\TN_sq_per_sec.tiff");
		//HistogramWindow hw_tn_sq_per_sec = new HistogramWindow(new ImagePlus("TN square per sec", tn_sq_per_sec.getImage()));
		
		FloatImage r_sq_avg = new FloatImage(results.getWidth(), results.getHeight(), results.getRSqAvg(), 0);
		r_sq_avg.saveAsTiff(folder+"\\R_sq_avg.tiff");
		//HistogramWindow hw_r_sq_avg = new HistogramWindow(new ImagePlus("R square of the avg", r_sq_avg.getImage()));
		
		FloatImage r_sq_var = new FloatImage(results.getWidth(), results.getHeight(), results.getRSqVar(), 0);
		r_sq_var.saveAsTiff(folder+"\\R_sq_var.tiff");
		//HistogramWindow hw_r_sq_var = new HistogramWindow(new ImagePlus("R square of the var", r_sq_var.getImage()));
		
		FloatImage r_sq_gain = new FloatImage(results.getWidth(), results.getHeight(), results.getRSqGain(), 0);
		r_sq_gain.saveAsTiff(folder+"\\R_sq_gain.tiff");
		//HistogramWindow hw_r_sq_gain = new HistogramWindow(new ImagePlus("R square of the gain", r_sq_gain.getImage()));
	}

	
	
}
