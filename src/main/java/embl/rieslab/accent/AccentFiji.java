package main.java.embl.rieslab.accent;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.scijava.command.Command;
import org.scijava.plugin.Plugin;

import net.imagej.ImageJ;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.numeric.real.FloatType;


@Plugin(type = Command.class, menuPath = "Plugins>Accent")
public class AccentFiji implements Command{

	@Override
	public void run() {
		JOptionPane.showMessageDialog(new JFrame(), "Hello", "Dialog",
		        JOptionPane.ERROR_MESSAGE);
		
		final ImgFactory< FloatType > imgFactory = new ArrayImgFactory<>(new FloatType());

	}

	public static void main(final String... args) throws Exception {
        // create the ImageJ application context with all available services
        final ImageJ ij = new net.imagej.ImageJ();
        ij.ui().showUI();
        ij.command().run(AccentFiji.class, true);
 
    }
}
