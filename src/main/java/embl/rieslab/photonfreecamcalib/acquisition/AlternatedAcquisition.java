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
	
	private ArrayList<ArrayBlockingQueue<FloatImage>> queues;
	
	public AlternatedAcquisition(Studio studio, AcquisitionSettings settings, PipelineController controller) {
		if(studio == null || settings == null || controller == null) {
			throw new NullPointerException();
		}
		
		this.studio = studio;
		this.settings = settings;
		this.controller = controller;
		
		queues = new ArrayList<ArrayBlockingQueue<FloatImage>>();
		for(int i=0;i<settings.exposures_.length;i++) {
			queues.add(new ArrayBlockingQueue<FloatImage>(200));
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
		
		// creates an array of stores
		Datastore[] stores = new Datastore[settings.exposures_.length];
		
		// folder's name
		String base = settings.folder_;
		
		for(int i=0;i<settings.exposures_.length;i++) {
			String expName = settings.name_ + "_" + settings.exposures_[i] + "ms";
			String exppath = base + "/" + expName;
			if(new File(exppath).exists()) {
				base = getFolderName(settings.folder_);
				exppath = base + "/" + expName;
				settings.folder_ = base;
			}
			
			// sets the SaveMode
			if (settings.saveMode_ == SaveMode.MULTIPAGE_TIFF) {
				try {
					stores[i] = studio.data().createMultipageTIFFDatastore(exppath, true, true);
				} catch (IOException e) {
					stop = true;
					System.out.println("Failed to create multi page TIFF");
					e.printStackTrace();
				}
			} else {
				try {
					stores[i] = studio.data().createSinglePlaneTIFFSeriesDatastore(exppath);
				} catch (IOException e) {
					stop = true;
					System.out.println("Failed to create single page TIFF");
					e.printStackTrace();
				}
			}
		}

		if (!stop) {
			// sets Roi
			if(settings.roi_ != null) {
				studio.getCMMCore().clearROI();
				studio.getCMMCore().setROI((int) settings.roi_.getXBase(), (int) settings.roi_.getYBase(), 
						(int) settings.roi_.getFloatWidth(), (int) settings.roi_.getFloatHeight());
				
				// write roi to disk
				JacksonRoiO.write(new File(settings.folder_+"/roi.roi"), settings.getRoi());
			}
			
			int frame = 0;

			// prepares coordinates
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
							// add to queue
							queues.get(i).add(new FloatImage(image.getWidth(), image.getHeight(), studio.data().ij().createProcessor(image).getFloatArray(), settings.exposures_[i]));
						}
					} catch (IOException e) {
						stop = true;
						e.printStackTrace();
					}
					
					
				}

				publish(frame);

				frame++;
			}
		} 
		
		stopTime = System.currentTimeMillis();

		if(stop) {
			publish(STOP);
		} else {
			publish(DONE);
		}
		
		for(int i=0;i<settings.exposures_.length;i++) {
			stores[i].close();
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
	public ArrayList<ArrayBlockingQueue<FloatImage>> getQueues() {
		return queues;
	}
	
	@Override
	public double getExecutionTime() {
		return ((double) stopTime-startTime)/1000.0;
	}
}