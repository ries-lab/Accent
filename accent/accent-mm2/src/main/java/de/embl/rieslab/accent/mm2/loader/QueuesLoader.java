package de.embl.rieslab.accent.mm2.loader;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;

import de.embl.rieslab.accent.common.data.image.BareImage;
import de.embl.rieslab.accent.common.interfaces.pipeline.Loader;

public class QueuesLoader implements Loader<BareImage>{

	private ArrayList<ArrayBlockingQueue<BareImage>> queues;
	
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
	public int getNumberOfChannels() {
		return queues.size();
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
