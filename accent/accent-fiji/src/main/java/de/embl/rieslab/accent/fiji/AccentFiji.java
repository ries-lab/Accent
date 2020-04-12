package de.embl.rieslab.accent.fiji;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

import org.scijava.command.Command;
import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import de.embl.rieslab.accent.common.utils.Dialogs;
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

			String path = chooser.getSelectedFile().getPath();

			boolean loadStacks = false;
			Map<Double, String> paths = null;
			int nTiffs = AccentFijiUtils.getNumberTifsContainMs(path);
			if (nTiffs >= 2) {
				paths = AccentFijiUtils.getExposures(path);
				paths.remove(new Double(0));
				loadStacks = true;
			} else {
				int nDir = AccentFijiUtils.getNumberDirectoriesContainMs(path);
				if (nDir >= 2) {
					paths = AccentFijiUtils.getExposures(path);
					paths.remove(new Double(0));
					loadStacks = false;

					// removes folders with no tiff inside
					ArrayList<Double> noTiffFound = new ArrayList<Double>();
					for (Entry<Double, String> e : paths.entrySet()) {
						if (AccentFijiUtils.getNumberTifs(e.getValue()) == 0) {
							noTiffFound.add(e.getKey());
						}
					}
					for (Double d : noTiffFound) {
						paths.remove(d);
					}
				} else {
					logService.log(LogService.WARN, "Not enough datasets found. Make sure the folder either "
							+ "contains >1 folders or tiff files with the exposure time (and the mention \"ms\") in the name.");
				}
			}

			if (path != null && paths.size() >= 2) {
				FijiController controller = new FijiController(ioservice, logService, paths, loadStacks);
				JFrame frame = controller.getMainFrame();
				frame.setVisible(true);
			} else {
				Dialogs.showErrorMessage("Not enough datasets open (minimum of 2).");
			}
		}
	}

	public static void main(final String... args) throws Exception {
        // create the ImageJ application context with all available services
        final ImageJ ijlaunch = new net.imagej.ImageJ();
        ijlaunch.ui().showUI();
        
		ijlaunch.command().run(AccentFiji.class, true);
    }
}
