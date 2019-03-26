package main.java.embl.rieslab.photonfreecamcalib.acquisition;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.SwingWorker;

import org.micromanager.Studio;
import org.micromanager.data.Coords;
import org.micromanager.data.Datastore;
import org.micromanager.data.Image;
import org.micromanager.data.Datastore.SaveMode;
import org.micromanager.data.internal.DefaultCoords;

import main.java.embl.rieslab.photonfreecamcalib.PipelineController;

public class MultiplexedAcquisition extends SwingWorker<Integer, Integer> implements Acquisition {

	private Studio studio;
	private AcquisitionSettings settings;
	private PipelineController controller;
	private boolean stop = false;
	private boolean running = false;
	
	public MultiplexedAcquisition(Studio studio, AcquisitionSettings settings, PipelineController controller) {
		if(studio == null || settings == null || controller == null) {
			throw new NullPointerException();
		}
		
		this.studio = studio;
		this.settings = settings;
		this.controller = controller;
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
			
		Datastore[] stores = new Datastore[settings.exposures_.length];
		for(int i=0;i<settings.exposures_.length;i++) {
			String expName = settings.name_ + "_" + settings.exposures_[i] + "ms";
			String exppath = settings.folder_ + "/" + expName;
			if(new File(exppath).exists()) {
				int n = getLastFileNumber(settings.folder_,expName);
				exppath = settings.folder_+"_"+n+"/"+expName;
				settings.folder_ = settings.folder_+"_"+n;
			}
			
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
			if(settings.roi_ != null) {
				studio.getCMMCore().clearROI();
				studio.getCMMCore().setROI((int) settings.roi_.getXBase(), (int) settings.roi_.getYBase(), 
						(int) settings.roi_.getFloatWidth(), (int) settings.roi_.getFloatHeight());
			}
			
			int frame = 0;

			Image image;
			Coords.CoordsBuilder builder = new DefaultCoords.Builder();
			builder.channel(0).z(0).stagePosition(0);

			while (!stop && frame < settings.numFrames_) {

				builder = builder.time(frame);
				
				for(int i=0;i<settings.exposures_.length; i++) {
					image = studio.live().snap(false).get(0);
					image = image.copyAtCoords(builder.build());
	
					try {
						stores[i].putImage(image);
					} catch (IOException e) {
						stop = true;
						e.printStackTrace();
					}
				}

				if (frame % 30 == 0) {
					publish(frame);
				}

				frame++;
			}
		} 
		
		if(stop) {
			publish(-2);
		} else {
			publish(-1);
		}
		
		for(int i=0;i<settings.exposures_.length;i++) {
			stores[i].close();
		}
		
		return 0;
	}

	@Override
	protected void process(List<Integer> chunks) {
		for(Integer i:chunks) {
			if(i == 0) {
				controller.acquisitionHasStarted();;
			} else if(i == -2) {
				controller.acquisitionHasStopped();
			} else if(i == -1) {
				controller.acquisitionHasEnded();
			} else {
				int progress = 100*i / getMaxNumberFrames(); 
				controller.updateAcquisitionProgress(progress);
			}
		}
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
	public int getMaxNumberFrames() {
		return settings.numFrames_;
	}

	@Override
	public AcquisitionSettings getSettings() {
		return settings;
	}
}