package main.java.embl.rieslab.accent;

import org.micromanager.Studio;
import org.scijava.plugin.Plugin;
import org.scijava.plugin.SciJavaPlugin;

import main.java.embl.rieslab.accent.ui.MainFrame;

@Plugin(type = MenuPlugin.class)
public class AccentMM2 implements MenuPlugin, SciJavaPlugin {

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
		MainFrame frame = new MainFrame(studio, false, null);
		frame.setVisible(true);
		//BenchmarkDataTypes.testPerformancesImageToFloatImage(studio);
		//BenchmarkDataTypes.testPerformancesBytesArrayToFloatImage();
	}

}
