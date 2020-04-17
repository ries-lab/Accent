package de.embl.rieslab.accent.mm2.acquisition;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;

import de.embl.rieslab.accent.mm2.data.image.BareImage;

public interface Acquisition {

	public void start();
	
	public void stop();
	
	public boolean isRunning();

	public int getMaxNumberFrames();
	
	public AcquisitionSettings getSettings();
	
	public ArrayList<ArrayBlockingQueue<BareImage>> getQueues();
	
	public double getExecutionTime();
	
}
    