package main.java.embl.rieslab.accent.loader;


public interface Loader<T> {

	public T getNext();
	
	public boolean hasNext();
	
	public boolean isDone();
}
