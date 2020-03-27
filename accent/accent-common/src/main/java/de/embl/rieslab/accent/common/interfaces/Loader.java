package de.embl.rieslab.accent.common.interfaces;

import de.embl.rieslab.accent.common.data.image.BareImage;

/**
 * Interface for an image loader. A loader has a certain number of channels (= image stream at a certain exposure). 
 * Each channel can be open to receive the next images.  
 * @author Joran Deschamps
 *
 */
public interface Loader {

	/**
	 * Returns the next BareImage in the specified channel.
	 * @param channel Channel to poll from.
	 * @return Next image
	 */
	public BareImage getNext(int channel);
	/**
	 * Checks if there is a next image in the specified channel.
	 * @param channel Channel to poll from.
	 * @return True if there is a next image, false otherwise.
	 */
	public boolean hasNext(int channel);
	
	/**
	 * Checks if all images from all channels have been polled.
	 * 
	 * @return True if there is no more image, false otherwise.
	 */
	public boolean isDone();
	
	/**
	 * Closes the currently opened channel.
	 */
	public void close();
	
	/**
	 * Returns the number of channels.
	 * 
	 * @return Number of channels.
	 */
	public int getNumberOfChannels();
	
	/**
	 * Checks if the specified channel is opened.
	 * @param channel Channel to probe.
	 * @return True if the channel is open, false otherwise.
	 */
	public boolean isOpen(int channel);
	
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
