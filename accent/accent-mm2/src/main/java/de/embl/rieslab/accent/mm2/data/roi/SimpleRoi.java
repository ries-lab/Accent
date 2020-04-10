
package de.embl.rieslab.accent.mm2.data.roi;

/**
 * A simple roi representation with a sanity check during instantiation.
 * 
 * @author Joran Deschamps
 *
 */
public class SimpleRoi {

	public int x0;
	public int y0;
	public int width;
	public int height;
	private boolean sane;
	
	/**
	 * Constructor used to build a SimpleRoi object using JacksonJSON
	 */
	public SimpleRoi() {}
	
	/**
	 * Constructor that checks if x0 and y0 are out of bounds or the width and height equal to 0. 
	 * If that is the case, the roi is declared not sane. In addition, if the roi is out of bound,
	 * the x0/y0 and width/height values are corrected to have width/height positive.
	 * @param x Upper left corner x position
	 * @param y Upper left corner y position
	 * @param w Roi width
	 * @param h Roi height
	 * @param im_width Image width
	 * @param im_height Image height
	 */
	public SimpleRoi(int x, int y, int w, int h, int im_width, int im_height) {
		if(im_width <= 0 || im_height <= 0)
			throw new IllegalArgumentException("Image width/height cannot be 0 or less.");
		
		x0 = x;
		y0 = y;
		width = w;
		height = h;
		
		// sanity check on the roi
		sane = true;
		
		if(x0 < 0 || x0 >= im_width
				|| y0 < 0 || y0 >= im_height
				|| w == 0 || h == 0) {
			sane = false;
		}
		
		if(x0+width >= im_width) {
			width=im_width-x0;
		} else if(x0+width < 0) {
			width = x0;
			x0 = 0;
		} else if(width<0) {
			width = -width;
			x0 = x0-width;
		}
		
		if(y0+height >= im_height) {
			height = im_height-y0;
		} else if(y0+height < 0) {
			height = y0;
			y0 = 0;
		} else if(height < 0) {
			height = -height;
			y0 = y0-height;
		}
		
	}
	/**
	 * Returns the sanity status of the Roi. A roi is unsane if the x0 and y0 are out of bounds or the w/h 0.
	 * @return True if the roi is sane, false otherwise.
	 */
	public boolean isSane() {
		return sane;
	}
}
