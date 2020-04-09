package de.embl.rieslab.accent.common.data.calibration;

import de.embl.rieslab.accent.mm2.data.image.FloatImage;

/**
 * A class used to generate the average and variance map for a specific exposure.
 * 
 * @author Joran Deschamps
 *
 */
public class CalibrationMap {

	/**
	 * Generates a FloatImage of the average pixel value at a specific exposure/ 
	 * 
	 * @param calib Calibration used to generate the image.
	 * @param exposure Exposure at which the image should be generated.
	 * @return Average image.
	 */
	public static FloatImage generateAvgMap(Calibration calib, double exposure) {
		float[] avpix = new float[calib.getWidth()*calib.getHeight()];
		
		for(int i=0;i<calib.getWidth()*calib.getHeight();i++) {
			avpix[i] = (float) (calib.getBaseline()[i]+calib.getDcPerSec()[i]*exposure/1000.0);
		}
		
		return new FloatImage(calib.getWidth(), calib.getHeight(), avpix, exposure);
	}

	/**
	 * Generates a FloatImage of the pixel variance at a specific exposure/ 
	 * 
	 * @param calib Calibration used to generate the image.
	 * @param exposure Exposure at which the image should be generated.
	 * @return Variance image.
	 */
	public static FloatImage generateVarMap(Calibration calib, double exposure) {
		float[] varpix= new float[calib.getWidth()*calib.getHeight()];
		
		for(int i=0;i<calib.getWidth()*calib.getHeight();i++) {
			varpix[i] = (float) (calib.getRnSq()[i]+calib.getTnSqPerSec()[i]*exposure/1000.0);
		}
		
		return new FloatImage(calib.getWidth(), calib.getHeight(), varpix, exposure);
	}
}
