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
import main.java.embl.rieslab.photonfreecamcalib.PipelineController;

public class AvgAndStdProcessor extends SwingWorker<Integer, Integer> implements Processor{
	
	private Studio studio;
	private String[] directories;
	private PipelineController controller;
	private boolean stop = false;
	private boolean running = false;
	
	public AvgAndStdProcessor(Studio studio, String[] directories, PipelineController controller) {
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
		
		publish(0);
		int counter = 0;
		double percentile = 100/directories.length;
		
		for(String file: directories) {
						
		    try {
				Datastore store = studio.data().loadData(file, true);

				float size = (float) store.getNumImages();
				
	 			Coords.CoordsBuilder builder = new DefaultCoords.Builder();
				builder.channel(0).z(0).stagePosition(0).time(0);
				
				FloatProcessor avg_im = studio.data().ij().createProcessor(store.getImage(builder.build())).convertToFloatProcessor();
				FloatProcessor sqravg_im = (FloatProcessor) avg_im.clone();
						
				int height = avg_im.getHeight();
				int width = avg_im.getWidth();


				FloatProcessor improc = new FloatProcessor(width, height);
				FloatProcessor sd_im = new FloatProcessor(width, height);
				
				for(int x=0;x<width;x++) {
					for(int y=0;y<height;y++){						
						float val =  avg_im.getf(x, y)/size;
						avg_im.setf(x, y, val);
						sqravg_im.setf(x, y, avg_im.getf(x, y)*val);
					}
				}

				for(int z=1;z<size;z++) {
					builder.time(z);
					improc = studio.data().ij().createProcessor(store.getImage(builder.build())).convertToFloatProcessor();
					
					publish((int) (percentile*counter+percentile*z/size));
					
					for(int x=0;x<width;x++) {

						if(stop) {
							break;
						}
						
						for(int y=0;y<height;y++){
							
							if(stop) {
								break;
							}
							
							float val = improc.getf(x, y)/size;
							avg_im.setf(x, y, val);
							sqravg_im.setf(x, y, val*improc.getf(x, y));	
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

				
				for(int x=0;x<width;x++) {
					for(int y=0;y<height;y++){
						sd_im.setf(x, y, (float) (Math.sqrt(sqravg_im.getf(x, y)-avg_im.getf(x, y)*avg_im.getf(x, y)) ));
					}
				}
				
				int exposure = extractExposure(file);
				System.out.println(getParentPath(file)+"/"+"Avg_"+exposure+"ms.tiff");
				
				FileSaver avgsaver = new FileSaver(new ImagePlus("Avg_"+exposure+"ms",avg_im)); 
				avgsaver.saveAsTiff(getParentPath(file)+"/"+"Avg_"+exposure+"ms.tiff");
				
				FileSaver sdsaver = new FileSaver(new ImagePlus("Sd_"+exposure+"ms",sd_im)); 
				sdsaver.saveAsTiff(getParentPath(file)+"/"+"Sd_"+exposure+"ms.tiff");
				
				counter ++;
			
				store.close();
				
		    } catch (IOException e) {
				e.printStackTrace();
				publish(-2);
			}
		}
		
		if(stop) {
			publish(-2);
		} else {
			publish(-1);
		}
		
		return 0;
	}

	@Override
	protected void process(List<Integer> chunks) {
		for(Integer i:chunks) {
			if(i == 0) {
				controller.processingHasStarted();
			} else if(i == -1) {
				controller.processingHasEnded();;
			} else if(i == -2) {
				controller.processingHasStopped();
			} else {
				int progress = i;
				controller.updateProcessorProgress(progress);
			}
		}
	}
	
	public static int extractExposure(String dataFolder) {
		
		if(dataFolder.substring(dataFolder.length()-2).equals("ms")) {
			int length = 0;
			int index  = dataFolder.length()-3;

			while(Character.isDigit(dataFolder.charAt(index))) {
				length ++;
				index --;
			}

			return Integer.parseInt(dataFolder.substring(index+1, index+1+length));
		}
		
		return 0;
	}
	
	public static String getParentPath(String dataFolder) {
		return new File(dataFolder).getParent();
	}
}
