package de.embl.rieslab.accent.mm2.loader;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;

import de.embl.rieslab.accent.common.data.image.BareImage;
import de.embl.rieslab.accent.common.interfaces.Loader;

public class QueuesLoader implements Loader{

	private ArrayList<ArrayBlockingQueue<BareImage>> queues;
	private boolean done = false;
	
	public QueuesLoader(ArrayList<ArrayBlockingQueue<BareImage>> queues) {
		if(queues == null) {
			throw new NullPointerException();
		}
		
		this.queues = queues;
	}
	
	@Override
	public BareImage getNext(int q) {
		return queues.get(q).poll();
	}

	@Override
	public boolean hasNext(int q) {
		return !queues.get(q).isEmpty();
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
	public int getNumberOfChannels() {
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
