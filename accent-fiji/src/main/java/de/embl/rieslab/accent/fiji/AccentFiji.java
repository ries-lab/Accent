package de.embl.rieslab.accent.fiji;


import javax.swing.JFrame;

import org.scijava.command.Command;
import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import io.scif.services.DatasetIOService;
import net.imagej.ImageJ;

@Plugin(type = Command.class, menuPath = "Plugins>ACCéNT")
public class AccentFiji implements Command{
	
	@Parameter
	private DatasetIOService ioservice;
	
    @Parameter
    private LogService logService;
    
	@Override
	public void run() {
		FijiController controller = new FijiController(ioservice, logService);
		JFrame frame = controller.getMainFrame();
		frame.setVisible(true);
	}

	public static void main(final String... args) throws Exception {
        // create the ImageJ application context with all available services
        final ImageJ ijlaunch = new net.imagej.ImageJ();
        ijlaunch.ui().showUI();
        
		ijlaunch.command().run(AccentFiji.class, true);
    }
}
