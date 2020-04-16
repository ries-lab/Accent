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
		/*JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new java.io.File("."));
		chooser.setDialogTitle("choosertitle");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setAcceptAllFileFilterUsed(false);

		if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			String path = chooser.getSelectedFile().getPath();
			
			int nTiffs = AccentFijiUtils.getNumberTifsContainMs(path);
			int nDir = AccentFijiUtils.getNumberDirectoriesContainMs(path);
			
			// we try to load either folders content or images in the same folder
			// as independent exposure experiments (ie files with ###ms in the name)
			boolean loadStacks = nTiffs > nDir;
			
			try {
				Map<Double, String> paths = AccentFijiUtils.getExposures(path, loadStacks);
				
				// if we load folders, then remove those without tiff files inside
				if(!loadStacks) {
					ArrayList<Double> noTiffFound = new ArrayList<Double>();
					for (Entry<Double, String> e : paths.entrySet()) {
						if (AccentFijiUtils.getNumberTifs(e.getValue()) == 0) {
							noTiffFound.add(e.getKey());
						}
					}
					for (Double d : noTiffFound) {
						paths.remove(d);
					}
				}			
	
				// instantiates UI
				if(paths.size() > 2) {
					FijiController controller = new FijiController(ioservice, logService, paths, loadStacks);
					JFrame frame = controller.getMainFrame();
					frame.setVisible(true);
				} else {
					Dialogs.showErrorMessage("Not enough datasets found (minimum of 2).");
				}
			} catch(Exception e) {
				e.printStackTrace();
				Dialogs.showErrorMessage("Error, make sure only the calibration images are present in the folder.");
			}
		}*/
		
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
