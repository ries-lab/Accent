package de.embl.rieslab.accent.fiji;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

import javax.swing.JFrame;

import org.scijava.log.LogService;

import de.embl.rieslab.accent.common.AbstractController;
import de.embl.rieslab.accent.common.interfaces.pipeline.Loader;
import de.embl.rieslab.accent.common.processor.CalibrationProcessor;
import de.embl.rieslab.accent.fiji.data.image.ArrayToPlaneImg;
import de.embl.rieslab.accent.fiji.data.image.PlaneImg;
import de.embl.rieslab.accent.fiji.data.image.StackImg;
import de.embl.rieslab.accent.fiji.data.image.PlaneImgSaver;
import de.embl.rieslab.accent.fiji.loader.SingleImgLoader;
import de.embl.rieslab.accent.fiji.loader.StackLoader;
import de.embl.rieslab.accent.fiji.processor.ImgProcessor;
import de.embl.rieslab.accent.fiji.ui.MainFrame;
import io.scif.services.DatasetIOService;

public class FijiController extends AbstractController<StackImg, PlaneImg> {

	public final static String LOADER_STACK = "load stacks";
	public final static String LOADER_SINGLES = "load singles";
	
	private DatasetIOService ioservice_;
	private LogService logService_;
	private Map<Double, String> datasets_;
	
	private boolean loadStacks;
	
	public FijiController(DatasetIOService ioservice, LogService logService, Map<Double, String> datasets, boolean loadStacks) {
		if(datasets.size() < 2)
			throw new IllegalArgumentException("At least 2 datasets are required.");
		
		this.logService_ = logService;
		this.ioservice_ = ioservice;
		this.datasets_ = datasets;
		
		this.loadStacks = loadStacks;
	}

	@Override
	public boolean startProcessor(String folder) {
		processor = getProcessor(folder, getLoader(loadStacks ? LOADER_STACK:LOADER_SINGLES));
		processor.startProcess();
		return true;
	}

	@Override
	public Loader<StackImg> getLoader(String parameter) {
		if(LOADER_STACK.equals(parameter)) {
			return new StackLoader(ioservice_, datasets_); 
		} else {
			return new SingleImgLoader(ioservice_, datasets_); 
		}
	}

	@Override
	public CalibrationProcessor<StackImg, PlaneImg> getProcessor(String path, Loader<StackImg> loader) {
		return new ImgProcessor(path, this, loader);
	}

	@Override
	public JFrame getMainFrame() {
		List<String> datasets = new ArrayList<String>();
		datasets_.forEach((e,v) -> datasets.add(v));
		MainFrame frame = new MainFrame(this, datasets);
		
		return frame;
	}

	@Override
	public boolean startProcessor(String path, ArrayList<ArrayBlockingQueue<StackImg>> queues) {
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
}
