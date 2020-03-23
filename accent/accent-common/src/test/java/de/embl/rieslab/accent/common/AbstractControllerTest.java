package de.embl.rieslab.accent.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;

import javax.swing.JFrame;

import de.embl.rieslab.accent.common.data.image.BareImage;
import de.embl.rieslab.accent.common.interfaces.Loader;
import de.embl.rieslab.accent.common.processor.CalibrationProcessor;

public class AbstractControllerTest {

	
	
	
	
	
	private class Controller extends AbstractController{
		@Override
		public JFrame getMainFrame() {return null;}

		@Override
		public Loader getLoader(String parameter) {return null;}

		@Override
		public CalibrationProcessor getProcessor(String path, Loader loader) {return null;}

		@Override
		public boolean startProcessor(String path, HashMap<String, Double> openedDatasets) {return false;}

		@Override
		public boolean startProcessor(String path, ArrayList<ArrayBlockingQueue<BareImage>> queues) {return false;}
	}
	
	private class Proc implements CalibrationProcessor{
		
	}
}
