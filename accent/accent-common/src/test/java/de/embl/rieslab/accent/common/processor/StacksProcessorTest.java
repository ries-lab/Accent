package de.embl.rieslab.accent.common.processor;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.embl.rieslab.accent.common.data.image.AvgVarStacks;
import de.embl.rieslab.accent.common.dummys.DummyController;
import de.embl.rieslab.accent.common.dummys.DummyLoader;

public class StacksProcessorTest {

	@Test
	public void testAvgVarStackProcessor() {
		int num_exp = 10;
		DummyLoader load = new DummyLoader(num_exp);
		DummyController cont =  new DummyController();
		String dir = "/temp_proc/";
		StacksProcessor proc = new StacksProcessor(dir, cont, load);
		
		AvgVarStacks a = proc.computeAvgAndVar();
		assertEquals(num_exp, a.getAvgs().length);
		assertEquals(num_exp, a.getVars().length);
		
		for(int i=0;i<num_exp;i++) {
			for(int y=0;y<a.getAvgs()[0].getHeight();y++) {
				for(int x=0;x<a.getAvgs()[0].getWidth();x++) {
					assertEquals(load.avgs[i].getPixelValue(x, y),a.getAvgs()[i].getPixelValue(x, y),0.0001);
					assertEquals(load.vars[i].getPixelValue(x, y),a.getVars()[i].getPixelValue(x, y),0.0001);
				}
			}
		}
	}
}
