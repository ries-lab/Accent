package de.embl.rieslab.accent.common.data.roi;



import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import de.embl.rieslab.accent.common.data.roi.SimpleRoi;

public class RoiTest {

	
	@Test
	public void testROI() {
		int imwidth = 512;
		int imheight = 1024;
		int x, y, w, h, c;
		SimpleRoi r;

		assertTrue((new SimpleRoi(0,10,50,100,imwidth, imheight)).isSane());
		
		// full frame
		assertTrue((new SimpleRoi(0,0,imwidth, imheight, imwidth, imheight)).isSane());
		
		// negative or too big x,y
		assertFalse((new SimpleRoi(-1,0,50,100,imwidth, imheight)).isSane());
		assertFalse((new SimpleRoi(0,-10,50,100,imwidth, imheight)).isSane());
		assertFalse((new SimpleRoi(0,2*imheight,50,100,imwidth, imheight)).isSane());
		assertFalse((new SimpleRoi(2*imwidth,0,50,100,imwidth, imheight)).isSane());
		assertFalse((new SimpleRoi(0,imheight,50,100,imwidth, imheight)).isSane());
		assertFalse((new SimpleRoi(imwidth,0,50,100,imwidth, imheight)).isSane());
		assertFalse((new SimpleRoi(10,10,0,0,imwidth, imheight)).isSane());

		// x+w too big
		w = 256;
		c = 100; 
		x = imwidth-100;
		y = 100;
		h = 20;
		r = new SimpleRoi(x,y,w,h,imwidth, imheight);
		assertTrue(r.isSane());
		assertEquals(c, r.width);
		
		// x+w negative
		w = -256;
		c = 100; 
		x = c;
		r = new SimpleRoi(x,y,w,h,imwidth, imheight);
		assertTrue(r.isSane());
		assertEquals(0, r.x0);
		assertEquals(c, r.width);
		
		// w negative
		w = -256;
		c = 350; 
		x = c;
		r = new SimpleRoi(x,y,w,h,imwidth, imheight);
		assertTrue(r.isSane());
		assertEquals(c+w, r.x0);
		assertEquals(-w, r.width);

		// y+h too big
		h = 256;
		c = 100; 
		y = imheight-100;
		x = 100;
		w = 20;
		r = new SimpleRoi(x,y,w,h,imwidth, imheight);
		assertTrue(r.isSane());
		assertEquals(c, r.height);
		
		// y+h negative
		h = -256;
		c = 100; 
		y = c;
		r = new SimpleRoi(x,y,w,h,imwidth, imheight);
		assertTrue(r.isSane());
		assertEquals(0, r.y0);
		assertEquals(c, r.height);
		
		// h negative
		h = -256;
		c = 350; 
		y = c;
		r = new SimpleRoi(x,y,w,h,imwidth, imheight);
		assertTrue(r.isSane());
		assertEquals(c+h, r.y0);
		assertEquals(-h, r.height);
				
		
	}
}
