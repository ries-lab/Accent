package main.java.embl.rieslab.accent.mm2.acquisition;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;

import main.java.embl.rieslab.accent.common.data.acquisition.AcquisitionSettings;
import main.java.embl.rieslab.accent.common.data.image.BareImage;

public interface Acquisition {

	public void start();
	
	public void stop();
	
	public boolean isRunning();

	public int getMaxNumberFrames();
	
	public AcquisitionSettings getSettings();
	
	public ArrayList<ArrayBlockingQueue<BareImage>> getQueues();
	
	public double getExecutionTime();
}
    