package de.embl.rieslab.accent.common.interfaces.pipeline;

import de.embl.rieslab.accent.common.interfaces.data.RawImage;

/**
 * Interface for an image loader. A loader has a certain number of channels (= image stream at a certain exposure). 
 * Each channel can be open to receive the next images.  
 * @author Joran Deschamps
 *
 */
public interface Loader<T extends RawImage> {

	/**
	 * Returns the next CalibrationImage in the specified channel.
	 * @param channel Channel to poll from.
	 * @return Next image
	 */
	public T getNext(int channel);
	/**
	 * Checks if there is a next image in the specified channel.
	 * @param channel Channel to poll from.
	 * @return True if there is a next image, false otherwise.
	 */
	public boolean hasNext(int channel);
	
	/**
	 * Returns the number of channels.
	 * 
	 * @return Number of channels.
	 */
	public int getNumberOfChannels();
	
	
	/**
	 * Opens the specified channel
	 * @param channel Channel to open
	 * @return True if it was successfully opened, false otherwise.
	 */
	public boolean openChannel(int channel);
	
	/**
	 * Returns the known number of images in the channel.
	 * @return
	 */
	public int getChannelLength();
}
