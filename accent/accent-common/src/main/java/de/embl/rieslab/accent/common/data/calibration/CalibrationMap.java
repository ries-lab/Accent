package de.embl.rieslab.accent.common.data.calibration;

/**
 * A class used to generate the average and variance map for a specific exposure.
 * 
 * @author Joran Deschamps
 *
 */
public class CalibrationMap {

	/**
	 * Generates a float array of the average pixel value at a specific exposure.
	 * 
	 * @param calib Calibration used to generate the image.
	 * @param exposure Exposure at which the image should be generated.
	 * @return Average array.
	 */
	public static float[] generateAvgMap(Calibration calib, double exposure) {
		float[] avpix = new float[calib.getWidth()*calib.getHeight()];
		
		for(int i=0;i<calib.getWidth()*calib.getHeight();i++) {
			avpix[i] = (float) (calib.getBaseline()[i]+calib.getDcPerSec()[i]*exposure/1000.0);
		}
		
		return avpix;
	}

	/**
	 * Generates a float array of the pixel variance at a specific exposure.
	 * 
	 * @param calib Calibration used to generate the image.
	 * @param exposure Exposure at which the image should be generated.
	 * @return Variance array.
	 */
	public static float[] generateVarMap(Calibration calib, double exposure) {
		float[] varpix= new float[calib.getWidth()*calib.getHeight()];
		
		for(int i=0;i<calib.getWidth()*calib.getHeight();i++) {
			varpix[i] = (float) (calib.getRnSq()[i]+calib.getTnSqPerSec()[i]*exposure/1000.0);
		}
		
		return varpix;
	}
}
