package de.embl.rieslab.accent.common.data.calibration;

import de.embl.rieslab.accent.common.data.image.FloatImage;

public class CalibrationMap {

	
	public static FloatImage generateAvgMap(Calibration calib, int exposure) {
		float[] avpix = new float[calib.width*calib.height];
		
		for(int i=0;i<calib.width*calib.height;i++) {
			avpix[i] = (float) (calib.baseline[i]+calib.dc_per_sec[i]*exposure/1000.0);
		}
		
		return new FloatImage(calib.width, calib.height, avpix, exposure);
	}
	
	public static FloatImage generateVarMap(Calibration calib, int exposure) {
		float[] varpix= new float[calib.width*calib.height];
		
		for(int i=0;i<calib.width*calib.height;i++) {
			varpix[i] = (float) (calib.rn_sq[i]+calib.tn_sq_per_sec[i]*exposure/1000.0);
		}
		
		return new FloatImage(calib.width, calib.height, varpix, exposure);
	}
}
