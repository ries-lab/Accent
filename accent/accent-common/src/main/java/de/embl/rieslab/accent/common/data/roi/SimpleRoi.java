
package de.embl.rieslab.accent.common.data.roi;

public class SimpleRoi {

	public int x0;
	public int y0;
	public int width;
	public int height;
	
	public SimpleRoi() {}
	
	public SimpleRoi(int x, int y, int w, int h) {
		x0 = x;
		y0 = y;
		width = w;
		height = h;
	}
}
