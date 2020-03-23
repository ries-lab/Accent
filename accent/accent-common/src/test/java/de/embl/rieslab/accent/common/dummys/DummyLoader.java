package de.embl.rieslab.accent.common.dummys;

import de.embl.rieslab.accent.common.data.image.BareImage;
import de.embl.rieslab.accent.common.interfaces.Loader;

public class DummyLoader implements Loader {

	public int nChannels;
	
	public DummyLoader(int nChannels) {
		this.nChannels = nChannels;
	}
	
	@Override
	public BareImage getNext(int channel) {
		return null;
	}

	@Override
	public boolean hasNext(int channel) {
		return false;
	}

	@Override
	public boolean isDone() {
		return false;
	}

	@Override
	public void close() {}

	@Override
	public int getNumberOfChannels() {
		return nChannels;
	}

	@Override
	public boolean isOpen(int channel) {
		return false;
	}

	@Override
	public boolean openChannel(int channel) {
		return false;
	}

	@Override
	public int getChannelLength() {
		return 0;
	}

}
