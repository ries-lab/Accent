package main.java.embl.rieslab.accent.loader;

import io.scif.img.ImgOpener;
import net.imglib2.img.Img;

public class SCIFIOLoader implements Loader<Img<?>>{

	private String[] directories;
	private int currentDirectory, currentPlane, currentExposure;
	private ImgOpener opener;
	
	public SCIFIOLoader(String[] directories) {
		this.directories = directories;
		currentDirectory = 0;
		currentPlane = 0;
		
		opener = new ImgOpener();
	}
	
	@Override
	public Img<?> getNext(int channel) {
        Img< ? > image = ( Img< ? > ) opener.openImgs( directories[0] ).get( 0 );

        
		return null;
	}

	@Override
	public boolean hasNext(int channel) {
        return false;
	}

	@Override
	public boolean isDone() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isOpen(int channel) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean openChannel(int channel) {
		// TODO Auto-generated method stub
		return false;
	}

}
