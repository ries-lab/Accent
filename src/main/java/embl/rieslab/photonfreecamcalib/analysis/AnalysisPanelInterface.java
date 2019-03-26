package main.java.embl.rieslab.photonfreecamcalib.analysis;

public interface AnalysisPanelInterface {

	public void setDataPath(String path);
	
	public void setProgress(int progress);
	
	public void analysisHasStarted();
	
	public void analysisHasStopped();
	
	public void analysisHasEnded();
}
