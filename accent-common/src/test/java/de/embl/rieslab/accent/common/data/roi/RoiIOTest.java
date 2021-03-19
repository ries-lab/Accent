package de.embl.rieslab.accent.common.data.roi;

import java.io.File;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RoiIOTest {

	@Test
	public void testIO() {
		int x = 12;
		int y = 85;
		int h = 45;
		int w = 26;
		int imwidth = 512;
		
		SimpleRoi roi = new SimpleRoi(x,y,w,h,imwidth,imwidth);
		
		File f_roi = new File("myroi");
		
		// write calibration
		SimpleRoiIO.write(f_roi, roi);
		assertTrue(f_roi.exists());
		
		// read calibration
		SimpleRoi roi2 = SimpleRoiIO.read(f_roi);
		assertEquals(roi.height, roi2.height);
		assertEquals(roi.width, roi2.width);
		assertEquals(roi.x0, roi2.x0);
		assertEquals(roi.y0, roi2.y0);
		
		// delete temp
		assertTrue(f_roi.delete());
	}
}
