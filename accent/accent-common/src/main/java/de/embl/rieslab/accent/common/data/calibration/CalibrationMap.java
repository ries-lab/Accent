package de.embl.rieslab.accent.common.data.calibration;

import de.embl.rieslab.accent.common.data.image.FloatImage;

public class CalibrationMap {

	public static FloatImage generateAvgMap(Calibration calib, double exposure) {
		float[] avpix = new float[calib.getWidth()*calib.getHeight()];
		
		for(int i=0;i<calib.getWidth()*calib.getHeight();i++) {
			avpix[i] = (float) (calib.getBaseline()[i]+calib.getDcPerSec()[i]*exposure/1000.0);
		}
		
		return new FloatImage(calib.getWidth(), calib.getHeight(), avpix, exposure);
	}
	
	public static FloatImage generateVarMap(Calibration calib, double exposure) {
		float[] varpix= new float[calib.getWidth()*calib.getHeight()];
		
		for(int i=0;i<calib.getWidth()*calib.getHeight();i++) {
			varpix[i] = (float) (calib.getRnSq()[i]+calib.getTnSqPerSec()[i]*exposure/1000.0);
		}
		
		return new FloatImage(calib.getWidth(), calib.getHeight(), varpix, exposure);
	}
}
