package de.embl.rieslab.accent.fiji;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.JFileChooser;

import org.scijava.command.Command;
import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import de.embl.rieslab.accent.common.utils.AccentUtils;
import de.embl.rieslab.accent.fiji.utils.AccentFijiUtils;
import io.scif.services.DatasetIOService;
import net.imagej.ImageJ;

@Plugin(type = Command.class, menuPath = "Plugins>Accent")
public class AccentFiji implements Command{
	
	@Parameter
	private DatasetIOService ioservice;
	
    @Parameter
    private LogService logService;
    
	@Override
	public void run() {
	    JFileChooser chooser = new JFileChooser();
	    chooser.setCurrentDirectory(new java.io.File("."));
	    chooser.setDialogTitle("choosertitle");
	    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	    chooser.setAcceptAllFileFilterUsed(false);

	    if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
	    	try {
	    		String path = chooser.getSelectedFile().getPath();
	    		
	    		int nTiffs = AccentFijiUtils.getNumberTifsContainMs(path);
	    		if(nTiffs > 2) {
	    			FijiController controller = new FijiController(logService);
	    		} else {
	    			int nDir = AccentFijiUtils.getNumberDirectoriesContainMs(path);
	    			if(nDir > 2) {
	    				
	    			}
	    		}
	    		
	    		
	    		
	    		
				Map<Double, String> c = Files
				.list(Paths.get(path))
				.filter(Files::isDirectory)
				.map(Path::getFileName)
				.collect(Collectors.toMap(AccentUtils::extractExposureMs, Path::toString));
				
				c.remove(0); // folders without ###ms in the name

				/*
				if(c.size()< 3) {
					FijiController controller = new FijiController(c, dataService, logService);
					JFrame frame = controller.getMainFrame();
					frame.setVisible(true);
				} else {
					Dialogs.showErrorMessage("Not enough datasets open (minimum of 3).");
				}*/
			} catch (IOException e) {
				e.printStackTrace();
			}
	    } else {
	    	return;
	    }
		
/*
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
		*/
	}

	public static void main(final String... args) throws Exception {
        // create the ImageJ application context with all available services
        final ImageJ ijlaunch = new net.imagej.ImageJ();
        ijlaunch.ui().showUI();
        
		ijlaunch.command().run(AccentFiji.class, true);
    }
}
