package de.embl.rieslab.accent.mm2.processor;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;

import org.junit.Test;

import de.embl.rieslab.accent.common.data.image.AvgVarStacks;
import de.embl.rieslab.accent.mm2.data.image.BareImage;
import de.embl.rieslab.accent.mm2.data.image.FloatImage;
import de.embl.rieslab.accent.mm2.dummys.DummyAcquisitionController;
import de.embl.rieslab.accent.mm2.dummys.DummyMM2Controller;
import de.embl.rieslab.accent.mm2.dummys.DummyMM2Loader;
import de.embl.rieslab.accent.mm2.loader.QueuesLoader;

public class QueueProcessorTest {
	
	@Test
	public void testAvgVarStackProcessor() {
		int num_exp = 10;
		DummyMM2Loader load = new DummyMM2Loader(num_exp);
		DummyMM2Controller cont =  new DummyMM2Controller();
		DummyAcquisitionController acq = new DummyAcquisitionController();
		String dir = "temp_proc";
		
		int numframes = 100;
		ArrayList<ArrayBlockingQueue<BareImage>> queue_arr = new ArrayList<ArrayBlockingQueue<BareImage>>();
		for(int i=0;i<num_exp;i++) {
			ArrayBlockingQueue<BareImage> queue = new ArrayBlockingQueue<BareImage>(numframes);
			
			for(int j=0;j<numframes;j++) {
				// fill in the queue from the loader
			}
		}
		
		QueuesLoader queue_loader = new QueuesLoader(queue_arr);
		QueuesProcessor proc = new QueuesProcessor(dir, cont, queue_loader, acq);
		
		AvgVarStacks<FloatImage> a = proc.computeAvgAndVar();
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
