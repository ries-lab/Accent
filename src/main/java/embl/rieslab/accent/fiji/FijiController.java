package main.java.embl.rieslab.accent.fiji;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.scijava.log.LogService;

import main.java.embl.rieslab.accent.common.Controller;
import main.java.embl.rieslab.accent.common.data.image.BareImage;
import main.java.embl.rieslab.accent.common.interfaces.Loader;
import main.java.embl.rieslab.accent.common.processor.CalibrationProcessor;
import main.java.embl.rieslab.accent.common.processor.StacksProcessor;
import main.java.embl.rieslab.accent.common.utils.Dialogs;
import main.java.embl.rieslab.accent.fiji.data.image.FijiDataset;
import main.java.embl.rieslab.accent.fiji.loader.CurrentImgsLoader;
import main.java.embl.rieslab.accent.fiji.ui.MainFrame;
import net.imagej.Dataset;
import net.imagej.DatasetService;

public class FijiController extends Controller {

	private DatasetService dataService;
	private LogService logService;
	private List<FijiDataset> datasetsToProcess;
	
	public FijiController(DatasetService dataService, LogService logService) {
		this.dataService = dataService;
		this.logService = logService;
	}
	
	@Override
	public boolean startProcessor(String path, HashMap<String, Integer> openedDatasets) {
		// sanity check on the datasets
		List<String> smallDatasets = new ArrayList<String>();
		List<String> hyperDimensionDatasets = new ArrayList<String>();
		List<String> noTimeAxisDatasets = new ArrayList<String>();
		List<String> uncompatibleTypeDatasets = new ArrayList<String>();

		List<Dataset> datasets = dataService.getDatasets();
		datasetsToProcess = new ArrayList<FijiDataset>();

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
		
		if(datasetsToProcess.size() > 2) {
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
	public Loader getLoader(String parameter) {
		if(datasetsToProcess != null && datasetsToProcess.size() > 2) {
			return new CurrentImgsLoader(datasetsToProcess); 
		}
		return null;
	}

	@Override
	public CalibrationProcessor getProcessor(String path, Loader loader) {
		if(loader != null) {
			return new StacksProcessor(path, this, loader);
		}
		return null;
	}

	@Override
	public JFrame getMainFrame() {
		List<String> datasets = new ArrayList<String>();
		Iterator<Dataset> it = dataService.getDatasets().iterator();
		while(it.hasNext()) {
			datasets.add(it.next().getName());
		}
		
		MainFrame frame = new MainFrame(this, datasets);
		
		return frame;
	}

	@Override
	public boolean startProcessor(String path, ArrayList<ArrayBlockingQueue<BareImage>> queues) {
		// Do nothing
		return false;
	}
}
