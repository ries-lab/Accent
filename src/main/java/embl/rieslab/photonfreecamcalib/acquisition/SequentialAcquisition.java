package main.java.embl.rieslab.photonfreecamcalib.acquisition;

import java.io.IOException;
import java.util.List;

import javax.swing.SwingWorker;

import org.micromanager.Studio;
import org.micromanager.data.Coords;
import org.micromanager.data.Datastore;
import org.micromanager.data.Image;
import org.micromanager.data.Datastore.SaveMode;
import org.micromanager.data.internal.DefaultCoords;

public class SequentialAcquisition extends SwingWorker<Integer, Integer> implements Acquisition {
	
	private Studio studio;
	private AcquisitionSettings settings;
	private AcquisitionPanelInterface panel;
	private boolean stop = false;
	private boolean running = false;
	
	public SequentialAcquisition(Studio studio, AcquisitionSettings settings, AcquisitionPanelInterface panel) {
		if(studio == null || settings == null || panel == null) {
			throw new NullPointerException();
		}
		
		this.studio = studio;
		this.settings = settings;
		this.panel = panel;
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
	
		int numExpo = 0;
	
		if(settings.roi_ != null) {
			studio.getCMMCore().clearROI();
			studio.getCMMCore().setROI((int) settings.roi_.getXBase(), (int) settings.roi_.getYBase(), 
					(int) settings.roi_.getFloatWidth(), (int) settings.roi_.getFloatHeight());
		}
		
		for(int exposure: settings.exposures_) {
			
			// set exposure time
			studio.getCMMCore().setExposure(exposure);
			
			// create data store
			Datastore currAcqStore = null;
			String exppath = settings.path_+settings.name_+"_"+exposure+"ms";
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
				
					builder = builder.time(frame);
					image = studio.live().snap(false).get(0);
					image = image.copyAtCoords(builder.build());
						
					try {
						currAcqStore.putImage(image);
					} catch (IOException e) {
						stop = true;
						e.printStackTrace();
					}
					
					if(frame % 100 == 0) {
						publish(frame+settings.numFrames_*numExpo);
					}
					
					frame ++;
				}
			}

			numExpo ++;			
		}
		
		if(stop) {
			publish(-1);
		} else {
			publish(-2);
		}
		
		return 0;
	}

	@Override
	protected void process(List<Integer> chunks) {
		for(Integer i:chunks) {
			if(i == 0) {
				panel.acqHasStarted();
			} else if(i == -1) {
				panel.acqHasEnded();
			} else if(i == -1) {
				panel.acqHasStopped();
			} else {
				int progress = i / getMaxNumberFrames();
				panel.setProgress(progress);
			}
		}
	}

	@Override
	public int getMaxNumberFrames() {
		return settings.numFrames_*settings.exposures_.length;
	}
}
