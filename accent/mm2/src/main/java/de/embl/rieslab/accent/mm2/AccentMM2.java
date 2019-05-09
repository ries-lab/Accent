package de.embl.rieslab.accent.mm2;

import javax.swing.JFrame;

import org.micromanager.Studio;
import org.scijava.plugin.Plugin;
import org.scijava.plugin.SciJavaPlugin;

@Plugin(type = org.micromanager.MenuPlugin.class)
public class AccentMM2 implements org.micromanager.MenuPlugin, SciJavaPlugin {

	
	private Studio studio;
	
	@Override
	public String getCopyright() {
		// TODO Auto-generated method stub
		return "LGPL";
	}

	@Override
	public String getHelpText() {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Accent";
	}

	@Override
	public String getVersion() {
		// TODO Auto-generated method stub
		return "1";
	}

	@Override
	public void setContext(Studio studio) {
		this.studio = studio;
	}

	@Override
	public String getSubMenu() {
		return "Calibration";
	}

	@Override
	public void onPluginSelected() {
		MM2Controller controller = new MM2Controller(studio);
		JFrame frame = controller.getMainFrame();
		frame.setVisible(true);
	}

}
