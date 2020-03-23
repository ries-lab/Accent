package de.embl.rieslab.accent.common.generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;

import javax.swing.JFrame;

import de.embl.rieslab.accent.common.data.acquisition.AcquisitionSettings;
import de.embl.rieslab.accent.common.data.image.BareImage;
import de.embl.rieslab.accent.common.interfaces.Loader;
import de.embl.rieslab.accent.common.interfaces.PipelineController;
import de.embl.rieslab.accent.common.interfaces.ui.AcquisitionPanelInterface;
import de.embl.rieslab.accent.common.interfaces.ui.GeneratePanelInterface;
import de.embl.rieslab.accent.common.interfaces.ui.ProcessingPanelInterface;
import de.embl.rieslab.accent.common.processor.CalibrationProcessor;

public class AvgVarMapsGeneratorTest {

	
	
	
	
	private class Controller implements PipelineController {

		@Override
		public JFrame getMainFrame() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void updateAcquisitionProgress(String message, int progress) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void acquisitionHasStarted() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void acquisitionHasStopped() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void acquisitionHasEnded() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean isAcquisitionDone() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean startAcquisition(AcquisitionSettings settings) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void stopAcquisition() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Loader getLoader(String parameter) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public CalibrationProcessor getProcessor(String path, Loader loader) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean isProcessorReady() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean startProcessor(String path) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean startProcessor(String path, HashMap<String, Integer> openedDatasets) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean startProcessor(String path, ArrayList<ArrayBlockingQueue<BareImage>> queues) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void stopProcessor() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void updateProcessorProgress(String progressString, int progress) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void processingHasStopped() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void processingHasStarted() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void processingHasEnded() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean isProcessingRunning() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void setProcessorPanelPath(String path) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean startMapGeneration(String path, Integer[] exposures) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isGenerationRunning() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void setGeneratorProgress(String progress) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void setAcquisitionPanel(AcquisitionPanelInterface procpane) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void setProcessingPanel(ProcessingPanelInterface procpane) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void setGeneratePanel(GeneratePanelInterface genpane) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean isReady() {
			// TODO Auto-generated method stub
			return false;
		}
		
	}
}
