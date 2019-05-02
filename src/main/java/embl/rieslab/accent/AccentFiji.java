package main.java.embl.rieslab.accent;

import org.scijava.command.Command;
import org.scijava.plugin.Plugin;

import main.java.embl.rieslab.accent.ui.MainFrame;
import net.imagej.ImageJ;

@Plugin(type = Command.class, menuPath = "Plugins>Accent")
public class AccentFiji implements Command{

	@Override
	public void run() {
		MainFrame frame = new MainFrame(null, true);
		frame.setVisible(true);
	}

	public static void main(final String... args) throws Exception {
        // create the ImageJ application context with all available services
        final ImageJ ij = new net.imagej.ImageJ();
        ij.ui().showUI();
        ij.command().run(AccentFiji.class, true);
    }
}
