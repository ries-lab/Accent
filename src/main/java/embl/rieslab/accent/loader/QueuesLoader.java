package main.java.embl.rieslab.accent.loader;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;

import main.java.embl.rieslab.accent.data.ImageExposurePair;

public class QueuesLoader implements Loader<ImageExposurePair>{

	private ArrayList<ArrayBlockingQueue<ImageExposurePair>> queues;
	private boolean done = false;
	
	public QueuesLoader(ArrayList<ArrayBlockingQueue<ImageExposurePair>> queues) {
		if(queues == null) {
			throw new NullPointerException();
		}
		
		this.queues = queues;
	}
	
	@Override
	public ImageExposurePair getNext(int q) {
		return queues.get(q).poll();
	}

	@Override
	public boolean hasNext(int q) {
		return queues.get(q).isEmpty();
	}

	@Override
	public boolean isDone() {
		return done;
	}
	
	@Override
	public void close() {
		done = true;
	}

	@Override
	public int getSize() {
		return queues.size();
	}

	@Override
	public boolean isOpen(int channel) {
		return true;
	}

	@Override
	public boolean openChannel(int channel) {
		return true;
	}

	@Override
	public int getChannelLength() {
		return 0;
	}

}
