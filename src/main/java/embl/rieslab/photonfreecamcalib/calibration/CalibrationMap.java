package main.java.embl.rieslab.photonfreecamcalib.calibration;

import main.java.embl.rieslab.photonfreecamcalib.data.FloatImage;

public class CalibrationMap {

	
	public static FloatImage generateAvgMap(Calibration calib, int exposure) {
		float[][] avpix = new float[calib.width][calib.height];
		
		for(int y=0;y<calib.height;y++) {
			for(int x=0;x<calib.width;x++) {
				avpix[x][y] = (float) (calib.baseline[x+y*calib.width]+calib.dc_per_sec[x+y*calib.width]*exposure/1000.0);
			}
		}
		
		return new FloatImage(calib.width, calib.height, avpix, exposure);
	}
	
	public static FloatImage generateVarMap(Calibration calib, int exposure) {
		float[][] varpix = new float[calib.width][calib.height];
		
		for(int y=0;y<calib.height;y++) {
			for(int x=0;x<calib.width;x++) {
				varpix[x][y] = (float) (calib.rn_sq[x+y*calib.width]+calib.tn_sq_per_sec[x+y*calib.width]*exposure/1000.0);
			}
		}
		
		return new FloatImage(calib.width, calib.height, varpix, exposure);
	}
}
