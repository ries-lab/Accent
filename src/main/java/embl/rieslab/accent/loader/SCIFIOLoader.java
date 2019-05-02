package main.java.embl.rieslab.accent.loader;

import io.scif.config.SCIFIOConfig;
import io.scif.config.SCIFIOConfig.ImgMode;
import io.scif.img.ImgIOException;
import io.scif.img.ImgOpener;
import net.imglib2.Interval;
import net.imglib2.img.Img;
import net.imglib2.img.planar.PlanarImg;

public class SCIFIOLoader implements Loader<Img<?>>{

	private String[] directories;
	private int currentDirectory, currentPlane, currentExposure;
	private ImgOpener opener;
	private Img< ? > image;
	
	public SCIFIOLoader(String[] directories) {
		this.directories = directories;
		currentDirectory = 0;
		currentPlane = 0;
		
		opener = new ImgOpener();
	}
	
	@Override
	public Img<?> getNext(int channel) {
        try {

			Img<?> plane = ((PlanarImg<?,?>) image).getPlane(currentPlane++);
			
			
		} catch (ImgIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        
		return null;
	}

	@Override
	public boolean hasNext(int channel) {
		if(channel == currentDirectory) {
			return (currentPlane < image.dimension(2));
		}
		return false;
	}

	@Override
	public boolean isDone() {
		return (currentDirectory == directories.length-1 && currentPlane == image.dimension(2));
	}

	@Override
	public void close() {
		// do nothing
	}

	@Override
	public int getSize() {
		return directories.length;
	}

	@Override
	public boolean isOpen(int channel) {
		if(channel == currentDirectory) {
			return true;
		}
		return false;
	}

	@Override
	public boolean openChannel(int channel) {
		if(channel < directories.length && channel == currentDirectory+1) {

	        SCIFIOConfig config = new SCIFIOConfig();
	        config.imgOpenerSetImgModes( ImgMode.PLANAR );
	        
			image = ( Img< ? > ) opener.openImgs(directories[channel], config).get(0);
			currentDirectory = channel;
			currentPlane = 0;
			return true;
		}
		return false;
	}

}
