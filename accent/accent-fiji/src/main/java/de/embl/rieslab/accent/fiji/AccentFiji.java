package de.embl.rieslab.accent.fiji;

import javax.swing.JFrame;

import org.scijava.command.Command;
import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import de.embl.rieslab.accent.common.utils.Dialogs;
import ij.ImagePlus;
import ij.WindowManager;
import net.imagej.DatasetService;
import net.imagej.ImageJ;

@Plugin(type = Command.class, menuPath = "Plugins>Accent")
public class AccentFiji implements Command{

    @Parameter
    private DatasetService dataService;
	
    @Parameter
    private LogService logService;
    
	@Override
	public void run() {
		ij.IJ.open("D:/Accent/fiji/MMStack_Default.ome_10ms.tif");
		ij.IJ.open("D:/Accent/fiji/MMStack_Default.ome_50ms.tif");
		ij.IJ.open("D:/Accent/fiji/MMStack_Default.ome_100ms.tif");
		
		String[] ids = WindowManager.getImageTitles();
		for(String i: ids) {
			logService.error(i);
		}

		ImagePlus imp = WindowManager.getImage(ids[0]);
		logService.error(imp.getStackSize());

		logService.error("--------------------------------------------------------------");
		System.out.println("Dataservice: "+dataService.getDatasets().size());
	
		
		if(dataService.getDatasets().size() < 3) {
			Dialogs.showErrorMessage("Not enough datasets open (minimum of 3).");
		} else {
			FijiController controller = new FijiController(dataService, logService);
			JFrame frame = controller.getMainFrame();
			frame.setVisible(true);
		}
		if(dataService.getDatasets().size() < 3) {
			Dialogs.showErrorMessage("Not enough datasets open (minimum of 3).");
		} else {
			FijiController controller = new FijiController(dataService, logService);
			JFrame frame = controller.getMainFrame();
			frame.setVisible(true);
		}
		
	}

	public static void main(final String... args) throws Exception {
        // create the ImageJ application context with all available services
        final ImageJ ijlaunch = new net.imagej.ImageJ();
        ijlaunch.ui().showUI();
        
        
		ijlaunch.command().run(AccentFiji.class, true);
    }
}
