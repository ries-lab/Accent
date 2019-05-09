package de.embl.rieslab.accent.common.interfaces;

import de.embl.rieslab.accent.common.data.image.BareImage;

public interface Loader {

	public BareImage getNext(int channel);
	
	public boolean hasNext(int channel);
	
	public boolean isDone();
	
	public void close();
	
	public int getSize();
	
	public boolean isOpen(int channel);
	
	public boolean openChannel(int channel);
	
	public int getChannelLength();
}
