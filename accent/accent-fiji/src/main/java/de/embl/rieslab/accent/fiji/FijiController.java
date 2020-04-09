package de.embl.rieslab.accent.fiji;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.scijava.log.LogService;

import de.embl.rieslab.accent.common.AbstractController;
import de.embl.rieslab.accent.common.data.image.BareImage;
import de.embl.rieslab.accent.common.interfaces.pipeline.Loader;
import de.embl.rieslab.accent.common.processor.CalibrationProcessor;
import de.embl.rieslab.accent.common.processor.StacksProcessor;
import de.embl.rieslab.accent.common.utils.Dialogs;
import de.embl.rieslab.accent.fiji.data.image.FijiDataset;
import de.embl.rieslab.accent.fiji.data.image.ImagePlusDataset;
import de.embl.rieslab.accent.fiji.data.image.ImgCalibrationImage;
import de.embl.rieslab.accent.fiji.loader.CurrentImgsLoader;
import de.embl.rieslab.accent.fiji.loader.ImagePlusLoader;
import de.embl.rieslab.accent.fiji.ui.MainFrame;
import ij.ImagePlus;
import ij.WindowManager;
import io.scif.services.DatasetIOService;
import net.imagej.Dataset;
import net.imagej.DatasetService;

public class FijiController extends AbstractController<ImgCalibrationImage> {

	public final static String LOADER_STACK = "load stacks";
	public final static String LOADER_SINGLES = "load singles";
	
	private DatasetIOService ioservice_;
	private LogService logService_;
	private Map<Double, String> datasets_;
	
	public FijiController(DatasetIOService ioservice, LogService logService, Map<Double, String> datasets) {
		this.logService_ = logService;
		this.ioservice_ = ioservice;
		this.datasets_ = datasets;
	}

	@Override
	public boolean startProcessor(String path, HashMap<String, Double> openedDatasets) {

		// sanity check on the datasets
		List<String> smallDatasets = new ArrayList<String>();
		List<String> hyperDimensionDatasets = new ArrayList<String>();
		List<String> noTimeAxisDatasets = new ArrayList<String>();
		List<String> uncompatibleTypeDatasets = new ArrayList<String>();
		
		datasetsToProcess = new ArrayList<FijiDataset>();
		ipdatasetsToProcess = new ArrayList<ImagePlusDataset>();
		
		if(!ij1) {
	
			List<Dataset> datasets = dataService.getDatasets();
	
			Iterator<Dataset> it = datasets.iterator();
			while (it.hasNext()) {
				Dataset dataset = it.next();
	
				if (openedDatasets.containsKey(dataset.getName())) {
					if (dataset.numDimensions() > 3) {
						hyperDimensionDatasets.add(dataset.getName());
					} else if (dataset.getFrames() == 1) {
						noTimeAxisDatasets.add(dataset.getName());
					} else if(!dataset.getTypeLabelShort().equals("8-bit uint")
							&&!dataset.getTypeLabelShort().equals("16-bit uint") 
							 && !dataset.getTypeLabelShort().equals("32-bit uint") ){
						logService.error(dataset.getName()+" is of the wrong type: "+dataset.getTypeLabelShort());
						uncompatibleTypeDatasets.add(dataset.getName());			
					} else {
						datasetsToProcess.add(new FijiDataset(dataset, openedDatasets.get(dataset.getName())));
					}
	
					if (dataset.getFrames() < 1000) {
						smallDatasets.add(dataset.getName());
					}
				}
			}
	
		} else {	

			for (String id: windowIDs) {
				
				if (openedDatasets.containsKey(id)) {
					ImagePlus ip = WindowManager.getImage(id);
					
					if (ip.getNDimensions() > 3) {
						hyperDimensionDatasets.add(id);
					} else if (ip.getNFrames() == 1) {
						noTimeAxisDatasets.add(id);
					} else if(ip.getType() != ImagePlus.GRAY8
							&& ip.getType() != ImagePlus.GRAY16
							 && ip.getType() != ImagePlus.GRAY32 ){
						logService.error(id+" is of the wrong type: "+ip.getType());
						uncompatibleTypeDatasets.add(id);			
					} else {
						ipdatasetsToProcess.add(new ImagePlusDataset(ip, openedDatasets.get(id)));
					}
	
					if (ip.getNFrames() < 1000) {
						smallDatasets.add(id);
					}
				}
			}
		}
		
		// informs user that datasets were removed due to wrong dimensions
		if (hyperDimensionDatasets.size() > 0) {
			String bad = "";
			for (String s : hyperDimensionDatasets) {
				bad += s + "\n";
			}

			JOptionPane.showMessageDialog(new JFrame(),
					"The following datasets had the wrong number of dimensions and were removed: \n" + bad,
					"Invalid datasets", JOptionPane.INFORMATION_MESSAGE);
		}

		// informs user that some datasets don't have the right types
		if (uncompatibleTypeDatasets.size() > 0) {
			String wrongType = "";
			for (String s : uncompatibleTypeDatasets) {
				wrongType += s + "\n";
			}

			JOptionPane.showMessageDialog(new JFrame(),
					"The following datasets don't have the right datatype (8-bit, 16-bit or 32-bit uint) and have been removed: \n" + wrongType,
					"Small datasets", JOptionPane.INFORMATION_MESSAGE);
		}

		// informs user that some datasets don't have the time dimension and were removed
		if (noTimeAxisDatasets.size() > 0) {
			String noTime = "";
			for (String s : noTimeAxisDatasets) {
				noTime += s + "\n";
			}

			JOptionPane.showMessageDialog(new JFrame(),
					"The following datasets do not have a time axis and have been removed: \n" + noTime,
					"Small datasets", JOptionPane.INFORMATION_MESSAGE);
		}
		
		if(datasetsToProcess.size() > 2 || ipdatasetsToProcess.size() > 2) {
			// informs user that some datasets are too small
			if (smallDatasets.size() > 0) {
				String small = "";
				for (String s : smallDatasets) {
					small += s + "\n";
				}

				JOptionPane.showMessageDialog(new JFrame(),
						"The following datasets are small and might lead to an inaccurate calibration: \n" + small,
						"Small datasets", JOptionPane.INFORMATION_MESSAGE);
			}
			
			return startProcessor(path);
		} else {
			Dialogs.showErrorMessage("Not enough dataset to proceed (minimum of 3).");
		}
		
		return false;
	}

	@Override
	public Loader<ImgCalibrationImage> getLoader(String parameter) {
		if() {
			return new CurrentImgsLoader(datasetsToProcess); 
		} else if() {
			return new ImagePlusLoader(ipdatasetsToProcess); 
		}
		return null;
	}

	@Override
	public CalibrationProcessor<ImgCalibrationImage> getProcessor(String path, Loader<ImgCalibrationImage> loader) {
		if(loader != null) {
			return new StacksProcessor(path, this, loader);
		}
		return null;
	}

	@Override
	public JFrame getMainFrame() {
		List<String> datasets = new ArrayList<String>();
		
		if(ij1) {
			for(String ids: windowIDs) {
				datasets.add(ids);
			}
		} else {
			Iterator<Dataset> it = dataService.getDatasets().iterator();
			while(it.hasNext()) {
				datasets.add(it.next().getName());
			}			
		}

		MainFrame frame = new MainFrame(this, datasets);
		
		return frame;
	}

	@Override
	public boolean startProcessor(String path, ArrayList<ArrayBlockingQueue<ImgCalibrationImage>> queues) {
		// do nothing
		return false;
	}
}
