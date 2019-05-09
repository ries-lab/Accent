package main.java.embl.rieslab.accent.mm2.acquisition;

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
import org.micromanager.data.internal.DefaultCoords;

import main.java.embl.rieslab.accent.common.data.acquisition.AcquisitionSettings;
import main.java.embl.rieslab.accent.common.data.image.BareImage;
import main.java.embl.rieslab.accent.common.data.roi.SimpleRoiWriter;
import main.java.embl.rieslab.accent.common.interfaces.PipelineController;
import main.java.embl.rieslab.accent.common.utils.Dialogs;
import main.java.embl.rieslab.accent.common.utils.utils;

public class AlternatedAcquisition extends SwingWorker<Integer, Integer> implements Acquisition {

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
	
	private ArrayList<ArrayBlockingQueue<BareImage>> queues;
	
	public AlternatedAcquisition(Studio studio, AcquisitionSettings settings, PipelineController controller) {
		if(studio == null || settings == null || controller == null) {
			throw new NullPointerException();
		}
		
		this.studio = studio;
		this.settings = settings;
		this.controller = controller;
		
		queues = new ArrayList<ArrayBlockingQueue<BareImage>>();
		for(int i=0;i<settings.exposures_.length;i++) {
			queues.add(new ArrayBlockingQueue<BareImage>(200));
		}
		
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
		
		if(stop) {
			publish(STOP);
			return 0;
		}
		
		// creates an array of stores
		Datastore[] stores = new Datastore[settings.exposures_.length];
				
		// test if any acquisition already exists
		boolean b = false;
		for(int i=0;i<settings.exposures_.length;i++) {
			String expName = settings.name_ + "_" + settings.exposures_[i] + "ms";
			String expPath =  settings.folder_ + "/" + expName;
			if(new File(expPath).exists()) {
				b = true;
			}
		}
		if(b) { // set the folder name with an increment
			settings.folder_ = getFolderName(settings.folder_);
		}
		
		for(int i=0;i<settings.exposures_.length;i++) {
			String expName = settings.name_ + "_" + settings.exposures_[i] + "ms";
			String expPath = settings.folder_ + "/" + expName;
						
			// sets the SaveMode
			if (settings.saveAsStacks_) {
				try {
					stores[i] = studio.data().createMultipageTIFFDatastore(expPath, true, true);
				} catch (IOException e) {
					stop = true;
					Dialogs.showErrorMessage(e.getMessage());
					e.printStackTrace();
				}
			} else {
				try {
					stores[i] = studio.data().createSinglePlaneTIFFSeriesDatastore(expPath);
				} catch (IOException e) {
					stop = true;
					Dialogs.showErrorMessage(e.getMessage());
					e.printStackTrace();
				}
			}
		}

		if (stop) {
			closeDatastores(stores);
			publish(STOP);
			return 0;
		}
			
		// sets Roi
		if(settings.roi_ != null) {
			studio.getCMMCore().clearROI();
			studio.getCMMCore().setROI(settings.roi_.x0, settings.roi_.y0, 
					settings.roi_.width, settings.roi_.height);
			
			// write roi to disk
			SimpleRoiWriter.write(new File(settings.folder_+"/roi.roi"), settings.roi_);
		}

		// prepares coordinates
		int frame = 0;
		Image image;
		Coords.CoordsBuilder builder = new DefaultCoords.Builder();
		builder.channel(0).z(0).stagePosition(0);

		while (!stop && frame < settings.numFrames_) {

			builder = builder.time(frame);
			
			// for each exposure, sets the exposure, snaps an image and adds it to its respective store
			for(int i=0;i<settings.exposures_.length; i++) {
				studio.getCMMCore().setExposure(settings.exposures_[i]);
				image = studio.live().snap(false).get(0);
				image = image.copyAtCoords(builder.build());

				try {
					stores[i].putImage(image);
					
					if(settings.parallelProcessing) {
						// adds to queue
						queues.get(i).add(new BareImage(image.getBytesPerPixel(), image.getRawPixels(), 
									image.getWidth(), image.getHeight(), settings.exposures_[i]));
					}
				} catch (IOException e) {
					stop = true;
					e.printStackTrace();
				}
			}

			publish(frame+1);

			frame++;
		}
		
		stopTime = System.currentTimeMillis();
		closeDatastores(stores);
		
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
			} else if(i == STOP) {
				controller.acquisitionHasStopped();
				controller.updateAcquisitionProgress("Interrupted.", 50);
				running = false;
			} else if(i == DONE) {
				controller.acquisitionHasEnded();
				controller.updateAcquisitionProgress("Done.", 100);
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

	private void closeDatastores(Datastore[] stores) {
		for(int i=0;i<stores.length;i++) {
			try {
				if(stores[i] != null) {
					stores[i].close();
				}
			} catch (IOException e) {
				// do nothing
			}
		}
	}

	private String getFolderName(String folder) {
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
		
		String base;
		if(num == 0) {
			base = folder+"_";
		} else {
			base = folder.substring(0,i)+"_";			
		}
		
		while(new File(base+(num+1)).exists()) {
			num++;
		}
		
		return base+(num+1);
	}
	
	@Override
	public int getMaxNumberFrames() {
		return settings.numFrames_;
	}

	@Override
	public AcquisitionSettings getSettings() {
		return settings;
	}

	@Override
	public ArrayList<ArrayBlockingQueue<BareImage>> getQueues() {
		return queues;
	}
	
	@Override
	public double getExecutionTime() {
		return ((double) stopTime-startTime)/1000.0;
	}
}