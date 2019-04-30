package main.java.embl.rieslab.accent.loader;

import java.util.concurrent.ArrayBlockingQueue;

import main.java.embl.rieslab.accent.data.ImageExposurePair;

public class ImageQueueLoader implements Loader<ImageExposurePair>{

	private ArrayBlockingQueue<ImageExposurePair> queue;
	
	public ImageQueueLoader(ArrayBlockingQueue<ImageExposurePair> queue) {
		if(queue == null) {
			throw new NullPointerException();
		}
		
		this.queue = queue;
	}
	
	@Override
	public ImageExposurePair getNext() {
		return queue.poll();
	}

	@Override
	public boolean hasNext() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isDone() {
		// TODO Auto-generated method stub
		return false;
	}

}
