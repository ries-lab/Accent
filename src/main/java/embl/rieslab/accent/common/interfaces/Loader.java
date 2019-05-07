package main.java.embl.rieslab.accent.common.interfaces;


public interface Loader<T> {

	public T getNext(int channel);
	
	public boolean hasNext(int channel);
	
	public boolean isDone();
	
	public void close();
	
	public int getSize();
	
	public boolean isOpen(int channel);
	
	public boolean openChannel(int channel);
	
	public int getChannelLength();
}
