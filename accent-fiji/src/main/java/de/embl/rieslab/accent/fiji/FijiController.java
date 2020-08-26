package de.embl.rieslab.accent.fiji;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ArrayBlockingQueue;

import javax.swing.JFrame;

import org.scijava.log.LogService;

import de.embl.rieslab.accent.common.AbstractController;
import de.embl.rieslab.accent.common.data.roi.SimpleRoi;
import de.embl.rieslab.accent.common.interfaces.pipeline.Loader;
import de.embl.rieslab.accent.common.processor.CalibrationProcessor;
import de.embl.rieslab.accent.common.utils.Dialogs;
import de.embl.rieslab.accent.fiji.data.image.ArrayToPlaneImg;
import de.embl.rieslab.accent.fiji.data.image.PlaneImg;
import de.embl.rieslab.accent.fiji.data.image.StackImg;
import de.embl.rieslab.accent.fiji.data.image.PlaneImgSaver;
import de.embl.rieslab.accent.fiji.loader.SingleImgLoader;
import de.embl.rieslab.accent.fiji.loader.StackLoader;
import de.embl.rieslab.accent.fiji.processor.ImgProcessor;
import de.embl.rieslab.accent.fiji.ui.MainFrame;
import de.embl.rieslab.accent.fiji.utils.AccentFijiUtils;
import io.scif.services.DatasetIOService;

public class FijiController extends AbstractController<StackImg, PlaneImg> {

	public final static String LOADER_STACK = "load stacks";
	public final static String LOADER_SINGLES = "load singles";
	
	private DatasetIOService ioservice_;
	private LogService logService_;
	private Map<Double, String> datasets_;
	
	private boolean loadStacks_;
	
	public FijiController(DatasetIOService ioservice, LogService logService) {
		//if(datasets.size() < 2)
			//throw new IllegalArgumentException("At least 2 datasets are required.");
		
		this.logService_ = logService;
		this.ioservice_ = ioservice;
	}

	@Override
	public boolean startProcessor(String path, SimpleRoi roi) {
		if(path == null)
			throw new NullPointerException("Path can't be null.");
		
		if(!path.isEmpty()) {
			// it is a bit silly to do the same thing that was done in the tablproc panel		
			int nTiffs = AccentFijiUtils.getNumberTifsContainMs(path);
			int nDir = AccentFijiUtils.getNumberDirectoriesContainMs(path);
			
			// we try to load either folders content or images in the same folder
			// as independent exposure experiments (ie files with ###ms in the name)
			loadStacks_ = nTiffs > nDir;
			
			try {
				datasets_ = AccentFijiUtils.getExposures(path, loadStacks_);
				
				// if we load folders, then remove those without tiff files inside
				if(!loadStacks_) {
					ArrayList<Double> noTiffFound = new ArrayList<Double>();
					for (Entry<Double, String> e : datasets_.entrySet()) {
						if (AccentFijiUtils.getNumberTifs(e.getValue()) == 0) {
							noTiffFound.add(e.getKey());
						}
					}
					for (Double d : noTiffFound) {
						datasets_.remove(d);
					}
				}			
	
				// start proc
				if(datasets_.size() >= 2) {
					processor = getProcessor(path, roi, getLoader(loadStacks_ ? LOADER_STACK:LOADER_SINGLES));
					processor.startProcess();
					return true;
				} else {
					Dialogs.showErrorMessage("Not enough datasets found (minimum of 2).");
				}
			} catch(Exception e) {
				e.printStackTrace();
				Dialogs.showErrorMessage("Error, make sure only the calibration images are present in the folder.");
			}
		} else {
			Dialogs.showWarningMessage("Path not set.");
		}
		return false;
	}

	@Override
	public Loader<StackImg> getLoader(String parameter) {
		if(LOADER_STACK.equals(parameter)) {
			return new StackLoader(ioservice_, logService_, datasets_); 
		} else {
			return new SingleImgLoader(ioservice_, logService_, datasets_); 
		}
	}

	@Override
	public CalibrationProcessor<StackImg, PlaneImg> getProcessor(String path, SimpleRoi roi, Loader<StackImg> loader) {
		return new ImgProcessor(path, roi, this, loader);
	}

	@Override
	public JFrame getMainFrame() {
		//List<String> datasets = new ArrayList<String>();
		//datasets_.forEach((e,v) -> datasets.add(v));
		MainFrame frame = new MainFrame(this);
		
		return frame;
	}

	@Override
	public boolean startProcessor(String path, SimpleRoi roi, ArrayList<ArrayBlockingQueue<StackImg>> queues) {
		// do nothing
		return false;
	}

	@Override
	public ArrayToPlaneImg getArrayToImageConverter() {
		return new ArrayToPlaneImg();
	}

	@Override
	public PlaneImgSaver getImageSaver() {
		return new PlaneImgSaver();
	}

	@Override
	public void stopAll() {
		if(isProcessorRunning()) {
			stopProcessor();
		}
	}

	@Override
	public void logMessage(String message) {
		logService_.warn(message);
	}
}
