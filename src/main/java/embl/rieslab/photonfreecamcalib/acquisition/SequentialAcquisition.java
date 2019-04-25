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
import main.java.embl.rieslab.photonfreecamcalib.calibration.JacksonRoiO;
import main.java.embl.rieslab.photonfreecamcalib.data.FloatImage;
import main.java.embl.rieslab.photonfreecamcalib.utils.utils;

public class SequentialAcquisition extends SwingWorker<Integer, Integer> implements Acquisition {
	
	private Studio studio;
	private AcquisitionSettings settings;
	private PipelineController controller;
	private boolean stop = false;
	private boolean running = false;
	private boolean prerun = false;
	private int prerunFrames = 0;
	
	private long startTime, stopTime;
	
	private final static int START = 0;
	private final static int DONE = -1;
	private final static int STOP = -2;
	
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
		
		// check if camera is running
		if(studio.getCMMCore().isSequenceRunning()){
			studio.getCMMCore().stopSequenceAcquisition();
		}
		
		startTime = System.currentTimeMillis();
		int numExpo = 0;
	
		// pre-run
		if(settings.preRunTime_ > 0) {
			prerun = true;
			
			int tot_expo = 0;
			for(int i: settings.exposures_) {
				tot_expo += i;
			}
			prerunFrames = settings.preRunTime_*1000*60 / tot_expo;
			
			int frame = 0;

			while (!stop && frame < prerunFrames) {

				// for each exposure, sets the exposure, snaps an image
				for (int i = 0; i < settings.exposures_.length; i++) {
					studio.getCMMCore().setExposure(settings.exposures_[i]);
					studio.live().snap(false).get(0);
				}

				frame++;
				publish(frame);
			}

		}
		prerun = false;
		
		// clears ROIs and apply the ROI from the settings if non-null 
		if(settings.roi_ != null) {
			studio.getCMMCore().clearROI();
			studio.getCMMCore().setROI((int) settings.roi_.getXBase(), (int) settings.roi_.getYBase(), 
					(int) settings.roi_.getFloatWidth(), (int) settings.roi_.getFloatHeight());
			
			JacksonRoiO.write(new File(settings.folder_+"/roi.roi"), settings.getRoi());
		}
		
		for(int exposure: settings.exposures_) {
			
			// sets exposure time
			studio.getCMMCore().setExposure(exposure);
			
			// creates data store
			Datastore currAcqStore = null;
			String expName = settings.name_ + "_" + exposure + "ms";
			String exppath = settings.folder_ + "/" + expName;
			if(new File(exppath).exists()) {
				String base = getFolderName(settings.folder_,expName);
				exppath = base+"/"+expName;
				settings.folder_ = base;
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
			} else {
				if(prerun) {
					controller.updateAcquisitionProgress("Pre-run "+i+"/"+prerunFrames, 0);
				} else {
					int progress = 100*i / getMaxNumberFrames();
					controller.updateAcquisitionProgress(i+"/"+getMaxNumberFrames(), progress);
				}
			}
		}
	}

	private String getFolderName(String folder, String expName) {
		// check if the folder has _# 
		int num = 0;
		int i;
		for(i=folder.length()-1; i>=0; i--) {
			if(folder.charAt(i) == '_') {
				if(utils.isInteger(folder.substring(i+1))){
					num = Integer.parseInt(folder.substring(i+1));
					break;
				}
			}
		}
		
		if(num == 0) {
			return folder+"_1";
		} else {
			return folder.substring(0,i)+"_"+(num+1);			
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
