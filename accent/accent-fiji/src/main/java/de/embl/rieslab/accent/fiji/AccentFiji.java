package de.embl.rieslab.accent.fiji;

import javax.swing.JFrame;

import org.scijava.command.Command;
import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import de.embl.rieslab.accent.common.utils.Dialogs;
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

		// check if IJ1 can grab the frames
		String[] ids = WindowManager.getImageTitles();
		if(ids.length > 2) {
			System.out.println("WindowManager found "+ids.length+" images");
			FijiController controller = new FijiController(ids, dataService, logService);
			JFrame frame = controller.getMainFrame();
			frame.setVisible(true);
		} else if(dataService.getDatasets().size() > 2) {
			System.out.println("DatasetService found "+dataService.getDatasets().size()+" images");
			FijiController controller = new FijiController(new String[0], dataService, logService);
			JFrame frame = controller.getMainFrame();
			frame.setVisible(true);
		} else {
			Dialogs.showErrorMessage("Not enough datasets open (minimum of 3).");
		}		
	}

	public static void main(final String... args) throws Exception {
        // create the ImageJ application context with all available services
        final ImageJ ijlaunch = new net.imagej.ImageJ();
        ijlaunch.ui().showUI();
        
		ij.IJ.open("D:/Accent/fiji/MMStack_Default.ome_10ms.tif");
		ij.IJ.open("D:/Accent/fiji/MMStack_Default.ome_50ms.tif");
		ij.IJ.open("D:/Accent/fiji/MMStack_Default.ome_100ms.tif");
        
		ijlaunch.command().run(AccentFiji.class, true);
    }
}
