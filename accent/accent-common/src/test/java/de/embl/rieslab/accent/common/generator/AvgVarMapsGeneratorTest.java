package de.embl.rieslab.accent.common.generator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import javax.swing.JFrame;

import org.junit.Test;

import de.embl.rieslab.accent.common.data.acquisition.AcquisitionSettings;
import de.embl.rieslab.accent.common.data.calibration.Calibration;
import de.embl.rieslab.accent.common.data.calibration.CalibrationIOTest;
import de.embl.rieslab.accent.common.data.image.BareImage;
import de.embl.rieslab.accent.common.interfaces.Loader;
import de.embl.rieslab.accent.common.interfaces.PipelineController;
import de.embl.rieslab.accent.common.interfaces.ui.AcquisitionPanelInterface;
import de.embl.rieslab.accent.common.interfaces.ui.GeneratePanelInterface;
import de.embl.rieslab.accent.common.interfaces.ui.ProcessingPanelInterface;
import de.embl.rieslab.accent.common.processor.CalibrationProcessor;

public class AvgVarMapsGeneratorTest {

	@Test
	public void testGeneration() {
		Controller cont = new Controller();
		Calibration cal = CalibrationIOTest.generateCalibration();
		AvgVarMapsGenerator gen = new AvgVarMapsGenerator(cont);

		double[] expo = new double[10];
		for(int i=0;i<expo.length;i++)
			expo[i] = i*10.5;
		
		// creates temp folder
		String dir = "/temp_test/";
		File f_dir = new File(dir);
		if(!f_dir.exists()) {
			f_dir.mkdir();
		}

		assertFalse(gen.isRunning());
		gen.generate(f_dir.getAbsolutePath(), cal, expo);
		assertTrue(gen.isRunning());
		
		while(gen.isRunning()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		for(int i=0;i<cont.arr_str.size()-1;i++) {
			String s = "Exposure: "+i+"/"+expo.length;
			assertEquals(s, cont.arr_str.get(i));
		}
		assertEquals("Done.", cont.arr_str.get(cont.arr_str.size()-1));
		
		// delete files
		for(int i=0;i<expo.length;i++) {
			File s_var, s_avg;
			if(i%2==0) {
				s_avg = new File(dir+"generated_Avg_"+((int)expo[i])+"ms.tiff");
				s_var = new File(dir+"generated_Var_"+((int)expo[i])+"ms.tiff");
			} else {
				s_avg = new File(dir+"generated_Avg_"+expo[i]+"ms.tiff");
				s_var = new File(dir+"generated_Var_"+expo[i]+"ms.tiff");
			}
			
			assertTrue(s_avg.exists());
			s_avg.delete();
			
			assertTrue(s_var.exists());
			s_var.delete();
		}
		f_dir.delete();
	}
	
	@Test
	public void testIllegalArgumentsGenerations() {
		Controller cont = new Controller();
		Calibration cal = CalibrationIOTest.generateCalibration();
		AvgVarMapsGenerator gen = new AvgVarMapsGenerator(cont);

		double[] expo = new double[0];
		
		// creates temp folder
		String dir = "/temp_test/";
		File f_dir = new File(dir);
		if(!f_dir.exists()) {
			f_dir.mkdir();
		}

		assertThrows(IllegalArgumentException.class, () -> {
			gen.generate(f_dir.getAbsolutePath(), cal, expo);
		});
	}

	@Test
	public void testNullGenerations() {
		Controller cont = new Controller();
		Calibration cal = CalibrationIOTest.generateCalibration();
		AvgVarMapsGenerator gen = new AvgVarMapsGenerator(cont);

		double[] expo = new double[10];
		for(int i=0;i<expo.length;i++)
			expo[i] = i*10;
		
		// creates temp folder
		String dir = "/temp_test/";
		File f_dir = new File(dir);
		if(!f_dir.exists()) {
			f_dir.mkdir();
		}
				
		// tests null generation
		assertThrows(NullPointerException.class, () -> {
			gen.generate(null, cal, expo);
		});
		assertThrows(NullPointerException.class, () -> {
			gen.generate(f_dir.getAbsolutePath(), null, expo);
		});
		assertThrows(NullPointerException.class, () -> {
			gen.generate(f_dir.getAbsolutePath(), cal, null);
		});
	}

	@Test
	public void testPathsGeneration() {

	}
	
	@Test
	public void testNullConstructor() {
		assertThrows(NullPointerException.class, () -> {
			new AvgVarMapsGenerator(null);
		});
	}
	
	private class Controller implements PipelineController {
		public List<String> arr_str;
		
		public Controller (){
			arr_str = new ArrayList<String>();
		}
		
		@Override
		public JFrame getMainFrame() {return null;}

		@Override
		public void updateAcquisitionProgress(String message, int progress) {}

		@Override
		public void acquisitionHasStarted() {}

		@Override
		public void acquisitionHasStopped() {}

		@Override
		public void acquisitionHasEnded() {}

		@Override
		public boolean isAcquisitionDone() {return false;}

		@Override
		public boolean startAcquisition(AcquisitionSettings settings) {return false;}

		@Override
		public void stopAcquisition() {}

		@Override
		public Loader getLoader(String parameter) {return null;}

		@Override
		public CalibrationProcessor getProcessor(String path, Loader loader) {return null;}

		@Override
		public boolean isProcessorReady() {return false;}

		@Override
		public boolean startProcessor(String path) {return false;}

		@Override
		public boolean startProcessor(String path, HashMap<String, Double> openedDatasets) {return false;}

		@Override
		public boolean startProcessor(String path, ArrayList<ArrayBlockingQueue<BareImage>> queues) {return false;}

		@Override
		public void stopProcessor() {}

		@Override
		public void updateProcessorProgress(String progressString, int progress) {}

		@Override
		public void processingHasStopped() {}

		@Override
		public void processingHasStarted() {}

		@Override
		public void processingHasEnded() {}

		@Override
		public boolean isProcessingRunning() {return false;}

		@Override
		public void setProcessorPanelPath(String path) {}

		@Override
		public boolean startMapGeneration(String path, double[] exposures) {return false;}

		@Override
		public boolean isGenerationRunning() {return false;}

		@Override
		public void setGeneratorProgress(String progress) {
			arr_str.add(progress);
		}

		@Override
		public void setAcquisitionPanel(AcquisitionPanelInterface procpane) {}

		@Override
		public void setProcessingPanel(ProcessingPanelInterface procpane) {}

		@Override
		public void setGeneratePanel(GeneratePanelInterface genpane) {}

		@Override
		public boolean isReady() {return false;}		
	}
}
