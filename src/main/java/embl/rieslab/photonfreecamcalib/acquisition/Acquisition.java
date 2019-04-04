package main.java.embl.rieslab.photonfreecamcalib.acquisition;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;

import main.java.embl.rieslab.photonfreecamcalib.data.FloatImage;

public interface Acquisition {

	public void start();
	
	public void stop();
	
	public boolean isRunning();

	public int getMaxNumberFrames();
	
	public AcquisitionSettings getSettings();
	
	public ArrayList<ArrayBlockingQueue<FloatImage>> getQueues();
	
	public double getExecutionTime();
}
    