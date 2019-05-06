package main.java.embl.rieslab.accent;


import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import ij.IJ;
import main.java.embl.rieslab.accent.ui.MainFrame;
import net.imagej.DatasetService;
import net.imagej.ImageJ;

@Plugin(type = Command.class, menuPath = "Plugins>Accent")
public class AccentFiji implements Command{

    @Parameter
    private DatasetService dataService;
	
	@Override
	public void run() {
		
		MainFrame frame = new MainFrame(null, true, dataService);
		frame.setVisible(true);

		//System.out.println(dataService.getDatasets().size());

		
	}

	public static void main(final String... args) throws Exception {
        // create the ImageJ application context with all available services
        final ImageJ ij = new net.imagej.ImageJ();
        ij.ui().showUI();
        //ij.command().run(AccentFiji.class, true);
    }
}
