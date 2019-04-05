package main.java.embl.rieslab.photonfreecamcalib.loader;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;

import main.java.embl.rieslab.photonfreecamcalib.data.FloatImage;

public class QueueLoader implements Loader {

	private ArrayList<ArrayBlockingQueue<FloatImage>> queues;
	
	public QueueLoader(ArrayList<ArrayBlockingQueue<FloatImage>> queues) {
		this.queues = queues;
	}
	
	
	
	@Override
	public boolean hasNext() {
		return !queues.isEmpty();
	}

	@Override
	public FloatImage getNext() {
		return queues.get(0).poll();
	}

}
