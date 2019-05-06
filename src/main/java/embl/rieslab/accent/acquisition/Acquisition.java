package main.java.embl.rieslab.accent.acquisition;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;

import main.java.embl.rieslab.accent.data.acquisition.AcquisitionSettings;
import main.java.embl.rieslab.accent.data.images.ImageExposurePair;

public interface Acquisition {

	public void start();
	
	public void stop();
	
	public boolean isRunning();

	public int getMaxNumberFrames();
	
	public AcquisitionSettings getSettings();
	
	public ArrayList<ArrayBlockingQueue<ImageExposurePair>> getQueues();
	
	public double getExecutionTime();
}
    