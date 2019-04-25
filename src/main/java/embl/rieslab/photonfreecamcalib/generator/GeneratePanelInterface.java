package main.java.embl.rieslab.photonfreecamcalib.generator;

public interface GeneratePanelInterface {

	public void setCalibrationPath(String path);
	
	public void setProgress(String progress);
	
	public Integer[] getExposures();
}
