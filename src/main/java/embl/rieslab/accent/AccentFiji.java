package main.java.embl.rieslab.accent;


import org.scijava.command.Command;
import org.scijava.plugin.Plugin;

import io.scif.img.ImgOpener;
import net.imagej.ImageJ;
import net.imglib2.img.Img;

@Plugin(type = Command.class, menuPath = "Plugins>Accent")
public class AccentFiji implements Command{

	@Override
	public void run() {
		//MainFrame frame = new MainFrame(null, true);
		//frame.setVisible(true);
		

		ImgOpener imgOpener = new ImgOpener();
		
		//List<SCIFIOImgPlus<?>>  images = imgOpener.openImgs( "D:\\Micromanager\\Micro-Manager-2.0gamma\\TestImgLib2\\test_1\\Default\\img_channel000_position000_time000000000_z000.tif");

		Img< ? > image = ( Img< ? > ) imgOpener.openImgs( "D:\\ImgLib2\\TestImages\\Default.tif").get(0);

		System.out.println(image.numDimensions());
		
		
	
	}

	public static void main(final String... args) throws Exception {
        // create the ImageJ application context with all available services
        final ImageJ ij = new net.imagej.ImageJ();
        ij.ui().showUI();
        ij.command().run(AccentFiji.class, true);
    }
}
