package main.java.embl.rieslab.photonfreecamcalib.acquisition;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import javax.swing.SwingWorker;

import org.micromanager.Studio;
import org.micromanager.data.Coords;
import org.micromanager.data.Datastore;
import org.micromanager.data.Image;
import org.micromanager.data.Datastore.SaveMode;
import org.micromanager.data.internal.DefaultCoords;

import main.java.embl.rieslab.photonfreecamcalib.PipelineController;
import main.java.embl.rieslab.photonfreecamcalib.data.FloatImage;

public class SequentialAcquisition extends SwingWorker<Integer, Integer> implements Acquisition {
	
	private Studio studio;
	private AcquisitionSettings settings;
	private PipelineController controller;
	private boolean stop = false;
	private boolean running = false;
	
	private long startTime, stopTime;
	
	private final static int START = 0;
	private final static int DONE = -1;
	private final static int STOP = -2;
	private final static int PRERUN = -3;
	
	public SequentialAcquisition(Studio studio, AcquisitionSettings settings, PipelineController controller) {
		if(studio == null || settings == null || controller == null) {
			throw new NullPointerException();
		}
		
		this.studio = studio;
		this.settings = settings;
		this.controller = controller;
		
		startTime = 0;
		stopTime = 0;
	}
	
	@Override
	public void start() {
		stop = false;
		running = true;
		this.execute();
	}

	@Override
	public void stop() {
		stop = true;
	}

	@Override
	public boolean isRunning() {
		return running;
	}

	@Override
	protected Integer doInBackground() throws Exception {
		startTime = System.currentTimeMillis();
		int numExpo = 0;
	
		// pre-run
		if(settings.preRunTime_ > 0) {
			publish(PRERUN);
			
			int tot_expo = 0;
			for(int i: settings.exposures_) {
				tot_expo += i;
			}
			int prunFrames = settings.preRunTime_*1000*60 / tot_expo;
			
			int frame = 0;

			while (!stop && frame < prunFrames) {

				// for each exposure, sets the exposure, snaps an image
				for (int i = 0; i < settings.exposures_.length; i++) {
					studio.getCMMCore().setExposure(settings.exposures_[i]);
					studio.live().snap(false).get(0);
				}

				frame++;
			}

		}
		
		// clears ROIs and apply the ROI from the settings if non-null 
		if(settings.roi_ != null) {
			studio.getCMMCore().clearROI();
			studio.getCMMCore().setROI((int) settings.roi_.getXBase(), (int) settings.roi_.getYBase(), 
					(int) settings.roi_.getFloatWidth(), (int) settings.roi_.getFloatHeight());
		}
		
		for(int exposure: settings.exposures_) {
			
			// sets exposure time
			studio.getCMMCore().setExposure(exposure);
			
			// creates data store
			Datastore currAcqStore = null;
			String expName = settings.name_ + "_" + exposure + "ms";
			String exppath = settings.folder_ + "/" + expName;
			if(new File(exppath).exists()) {
				int n = getLastFileNumber(settings.folder_,expName);
				exppath = settings.folder_+"_"+n+"/"+expName;
				settings.folder_ = settings.folder_+"_"+n;
			}
			
			// sets SaveMode
			if(settings.saveMode_ == SaveMode.MULTIPAGE_TIFF){
				try {
					currAcqStore = studio.data().createMultipageTIFFDatastore(exppath, true, true);
				} catch (IOException e) {
					stop = true;
					System.out.println("Failed to create multi page TIFF");
					e.printStackTrace();
				}
			} else {
				try {
					currAcqStore = studio.data().createSinglePlaneTIFFSeriesDatastore(exppath);
				} catch (IOException e) {
					stop = true;
					System.out.println("Failed to create single page TIFF");
					e.printStackTrace();
				}
			}
			
			if(!stop) {			
				int frame = 0;	
				
				Image image;
				Coords.CoordsBuilder builder = new DefaultCoords.Builder();
				builder.channel(0).z(0).stagePosition(0);
					
				while(!stop && frame < settings.numFrames_) {
				
					// snaps an image and adds it to the store
					builder = builder.time(frame);
					image = studio.live().snap(false).get(0);
					image = image.copyAtCoords(builder.build());
						
					try {
						currAcqStore.putImage(image);
					} catch (IOException e) {
						stop = true;
						e.printStackTrace();
					}
					
					publish(frame+settings.numFrames_*numExpo);
					
					
					frame ++;
				}
			}

			currAcqStore.close();
			
			numExpo ++;			
		}
		

		stopTime = System.currentTimeMillis();
		
		if(stop) {
			publish(STOP);
		} else {
			publish(DONE);
		}
		
		return 0;
	}

	private int getLastFileNumber(String folder, String expName) {
		int num = 0;
		String base = folder+"/"+expName;
		while(new File(base).exists()) {
			num++;
			base = folder+"_"+num+"/"+expName;
		}
		return num;
	}

	@Override
	protected void process(List<Integer> chunks) {
		for(Integer i:chunks) {
			if(i == START) {
				controller.acquisitionHasStarted();
				controller.updateAcquisitionProgress(0+"/"+getMaxNumberFrames(), 0);
			} else if(i == DONE) {
				controller.acquisitionHasEnded();
				controller.updateAcquisitionProgress("Done.", 100);
				running = false;
			} else if(i == STOP) {
				controller.acquisitionHasStopped();
				controller.updateAcquisitionProgress("Interrupted.", 50);
				running = false;
			} else if(i == PRERUN) {
				controller.updateAcquisitionProgress("Pre-run...", 0);
			} else {
				int progress = 100*i / getMaxNumberFrames();
				controller.updateAcquisitionProgress(i+"/"+getMaxNumberFrames(), progress);
			}
		}
	}

	@Override
	public int getMaxNumberFrames() {
		return settings.numFrames_*settings.exposures_.length;
	}

	@Override
	public AcquisitionSettings getSettings() {
		return settings ;
	}

	@Override
	public ArrayList<ArrayBlockingQueue<FloatImage>> getQueues() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public double getExecutionTime() {
		return ((double) stopTime-startTime)/1000.0;
	}
}
