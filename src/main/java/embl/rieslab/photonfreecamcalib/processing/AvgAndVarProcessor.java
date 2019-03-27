package main.java.embl.rieslab.photonfreecamcalib.processing;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.SwingWorker;

import org.micromanager.Studio;
import org.micromanager.data.Coords;
import org.micromanager.data.Datastore;
import org.micromanager.data.internal.DefaultCoords;

import ij.ImagePlus;
import ij.io.FileSaver;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import main.java.embl.rieslab.photonfreecamcalib.PipelineController;
import main.java.embl.rieslab.photonfreecamcalib.utils.utils;

public class AvgAndVarProcessor extends SwingWorker<Integer, Integer> implements Processor{
	
	private Studio studio;
	private String[] directories;
	private PipelineController controller;
	private boolean stop = false;
	private boolean running = false;
	
	private final static int START = 0;
	private final static int DONE = -1;
	private final static int STOP = -2;
	
	public AvgAndVarProcessor(Studio studio, String[] directories, PipelineController controller) {
		if(studio == null || directories == null || controller == null) {
			throw new NullPointerException();
		}
		
		this.studio = studio;
		this.directories = directories;
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
		running = false;
	}

	@Override
	public boolean isRunning() {
		return running;
	}

	@Override
	protected Integer doInBackground() throws Exception {
		
		publish(START);
		int counter = 0;
		double percentile = 100/directories.length;
		
		for(String file: directories) {
						
		    try {
				Datastore store = studio.data().loadData(file, true);
				float stackSize = (float) store.getNumImages();
				
				// gets the first image of the stack
	 			Coords.CoordsBuilder builder = new DefaultCoords.Builder();
				builder.channel(0).z(0).stagePosition(0).time(0);
				
				ImageProcessor improc = studio.data().ij().createProcessor(store.getImage(builder.build()));
						
				int height = improc.getHeight();
				int width = improc.getWidth();

				FloatProcessor avg_im = new FloatProcessor(width, height);
				FloatProcessor avgsq_im = new FloatProcessor(width, height);
				avg_im.setFloatArray(improc.getFloatArray());
				avgsq_im.setFloatArray(improc.getFloatArray());
				
				FloatProcessor var_im = new FloatProcessor(width, height);

				// loops over the stack and adds pixel-wise the value of each pixels and their square (normalized by the stack size)
				for(int z=1;z<stackSize;z++) {

					// gets image at position z in the stack
					builder.time(z);
					improc = studio.data().ij().createProcessor(store.getImage(builder.build()));
					
					// updates progress bar
					publish((int) (percentile*counter+percentile*z/stackSize)); 
					
					for(int x=0;x<width;x++) {

						if(stop) {
							break;
						}
						
						for(int y=0;y<height;y++){
							
							if(stop) {
								break;
							}
							
							avg_im.setf(x, y, avg_im.getf(x, y)+improc.getf(x, y));
							avgsq_im.setf(x, y, avgsq_im.getf(x, y)+improc.getf(x, y)*improc.getf(x, y));	
						}
					}
					
					if(stop) {
						break;
					}

				}
				
				if(stop) {
					store.close();
					break;
				}

				// computes the variance image from the average square and the average values
				for(int x=0;x<width;x++) {
					for(int y=0;y<height;y++){
						avg_im.setf(x, y, avg_im.getf(x, y)/stackSize);
						float var = (float) (avgsq_im.getf(x, y)/stackSize-avg_im.getf(x, y)*avg_im.getf(x, y));
						if(var <= 0) {
							var = 65535; // 16bits unsigned max, for IJ 
						}
						var_im.setf(x, y, var);
					}
				}

				// save as images
				int exposure = utils.extractExposurefromFolderName(file);
				
				FileSaver avgsaver = new FileSaver(new ImagePlus("Avg_"+exposure+"ms",avg_im)); 
				avgsaver.saveAsTiff(getParentPath(file)+"/"+"Avg_"+exposure+"ms.tiff");
				
				FileSaver sdsaver = new FileSaver(new ImagePlus("Var_"+exposure+"ms",var_im)); 
				sdsaver.saveAsTiff(getParentPath(file)+"/"+"Var_"+exposure+"ms.tiff");
				
				counter ++;
			
				store.close();
				
		    } catch (IOException e) {
				e.printStackTrace();
				publish(STOP);
			}
		}
		
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
				controller.processingHasStarted();
			} else if(i == DONE) {
				controller.processingHasEnded();
			} else if(i == STOP) {
				controller.processingHasStopped();
			} else {
				int progress = i;
				controller.updateProcessorProgress(progress);
			}
		}
	}
	
	
	public static String getParentPath(String dataFolder) {
		return new File(dataFolder).getParent();
	}
	
	@Override
	public String getCurrentParentPath() {
		if(directories == null || !new File(directories[0]).exists()) {
			return "";
		}
		
		return new File(directories[0]).getParent();
	}
}
