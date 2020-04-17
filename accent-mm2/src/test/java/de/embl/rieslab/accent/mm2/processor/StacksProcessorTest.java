package de.embl.rieslab.accent.mm2.processor;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.embl.rieslab.accent.common.data.image.AvgVarStacks;
import de.embl.rieslab.accent.mm2.data.image.FloatImage;
import de.embl.rieslab.accent.mm2.dummys.DummyMM2Controller;
import de.embl.rieslab.accent.mm2.dummys.DummyMM2Loader;

public class StacksProcessorTest {


	@Test
	public void testByteAvgVarStackProcessor() {
		int width = 10;
		int height = 20;
		int numFrames = 10000;
		double[] exps = {0.1, 1, 5, 10};
				
		DummyMM2Loader load = new DummyMM2Loader(1, width, height, numFrames, exps);
		DummyMM2Controller cont =  new DummyMM2Controller();
		String dir = "temp_proc";
		
		// computes avg and var
		StacksProcessor proc = new StacksProcessor(dir, cont, load);
		AvgVarStacks<FloatImage> a = proc.computeAvgAndVar();

		assertEquals(exps.length, a.getAvgs().length);
		assertEquals(exps.length, a.getVars().length);
		
		for(int i=0;i<exps.length;i++) {
			for(int y=0;y<a.getAvgs()[0].getHeight();y++) {
				for(int x=0;x<a.getAvgs()[0].getWidth();x++) {
					
					if (x % 10 == 0 && y % 20 == 0) {
						assertEquals(DummyMM2Loader.getLowPixAverage(exps[i]), a.getAvgs()[i].getPixelValue(x, y), 0.01*DummyMM2Loader.getLowPixAverage(exps[i]));
						assertEquals(DummyMM2Loader.getLowPixVariance(exps[i]), a.getVars()[i].getPixelValue(x, y), 0.05*DummyMM2Loader.getLowPixVariance(exps[i]));
					} else {
						assertEquals(DummyMM2Loader.getDimPixAverage(exps[i]), a.getAvgs()[i].getPixelValue(x, y), 0.01*DummyMM2Loader.getDimPixAverage(exps[i]));
						assertEquals(DummyMM2Loader.getDimPixVariance(exps[i]), a.getVars()[i].getPixelValue(x, y), 0.1*DummyMM2Loader.getDimPixVariance(exps[i]));
					}
				}
			}
		}
	}

	@Test
	public void testShortAvgVarStackProcessor() {
		int width = 10;
		int height = 20;
		int numFrames = 10000;
		double[] exps = {0.1, 1, 5, 10};
				
		DummyMM2Loader load = new DummyMM2Loader(2, width, height, numFrames, exps);
		DummyMM2Controller cont =  new DummyMM2Controller();
		String dir = "temp_proc";
		
		// computes avg and var
		StacksProcessor proc = new StacksProcessor(dir, cont, load);
		AvgVarStacks<FloatImage> a = proc.computeAvgAndVar();

		assertEquals(exps.length, a.getAvgs().length);
		assertEquals(exps.length, a.getVars().length);
		
		for(int i=0;i<exps.length;i++) {
			for(int y=0;y<a.getAvgs()[0].getHeight();y++) {
				for(int x=0;x<a.getAvgs()[0].getWidth();x++) {
					
					if (x % 10 == 0 && y % 20 == 0) {
						assertEquals(DummyMM2Loader.getHotPixAverage(exps[i]), a.getAvgs()[i].getPixelValue(x, y), 0.01*DummyMM2Loader.getHotPixAverage(exps[i]));
						assertEquals(DummyMM2Loader.getHotPixVariance(exps[i]), a.getVars()[i].getPixelValue(x, y), 0.05*DummyMM2Loader.getHotPixVariance(exps[i]));
					} else {
						assertEquals(DummyMM2Loader.getLowPixAverage(exps[i]), a.getAvgs()[i].getPixelValue(x, y), 0.01*DummyMM2Loader.getLowPixAverage(exps[i]));
						assertEquals(DummyMM2Loader.getLowPixVariance(exps[i]), a.getVars()[i].getPixelValue(x, y), 0.06*DummyMM2Loader.getLowPixVariance(exps[i]));
					}
				}
			}
		}
	}

	@Test
	public void testIntAvgVarStackProcessor() {
		int width = 10;
		int height = 20;
		int numFrames = 10000;
		double[] exps = {0.1, 1, 5, 10};
				
		DummyMM2Loader load = new DummyMM2Loader(4, width, height, numFrames, exps);
		DummyMM2Controller cont =  new DummyMM2Controller();
		String dir = "temp_proc";
		
		// computes avg and var
		StacksProcessor proc = new StacksProcessor(dir, cont, load);
		AvgVarStacks<FloatImage> a = proc.computeAvgAndVar();

		assertEquals(exps.length, a.getAvgs().length);
		assertEquals(exps.length, a.getVars().length);
		
		for(int i=0;i<exps.length;i++) {
			for(int y=0;y<a.getAvgs()[0].getHeight();y++) {
				for(int x=0;x<a.getAvgs()[0].getWidth();x++) {
					
					if (x % 10 == 0 && y % 20 == 0) {
						assertEquals(DummyMM2Loader.getHotPixAverage(exps[i]), a.getAvgs()[i].getPixelValue(x, y), 0.01*DummyMM2Loader.getHotPixAverage(exps[i]));
						assertEquals(DummyMM2Loader.getHotPixVariance(exps[i]), a.getVars()[i].getPixelValue(x, y), 0.05*DummyMM2Loader.getHotPixVariance(exps[i]));
					} else {
						assertEquals(DummyMM2Loader.getLowPixAverage(exps[i]), a.getAvgs()[i].getPixelValue(x, y), 0.01*DummyMM2Loader.getLowPixAverage(exps[i]));
						assertEquals(DummyMM2Loader.getLowPixVariance(exps[i]), a.getVars()[i].getPixelValue(x, y), 0.06*DummyMM2Loader.getLowPixVariance(exps[i]));
					}
				}
			}
		}
	}
}
