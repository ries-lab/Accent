package main.java.embl.rieslab.photonfreecamcalib;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.scijava.command.Command;
import org.scijava.plugin.Plugin;

import net.imagej.ImageJ;


@Plugin(type = Command.class, menuPath = "Plugins>PhotonFreeCalib")
public class PhotonFreeCalibFiji implements Command{

	@Override
	public void run() {
		JOptionPane.showMessageDialog(new JFrame(), "Hello", "Dialog",
		        JOptionPane.ERROR_MESSAGE);
	}

	public static void main(final String... args) throws Exception {
        // create the ImageJ application context with all available services
        final ImageJ ij = new net.imagej.ImageJ();
        ij.ui().showUI();
        ij.command().run(PhotonFreeCalibFiji.class, true);
 
    }
}
