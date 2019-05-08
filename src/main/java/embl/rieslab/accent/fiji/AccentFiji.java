package main.java.embl.rieslab.accent.fiji;


import javax.swing.JFrame;

import org.scijava.command.Command;
import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import main.java.embl.rieslab.accent.common.utils.Dialogs;
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
        final ImageJ ij = new net.imagej.ImageJ();
        ij.ui().showUI();
    }
}
