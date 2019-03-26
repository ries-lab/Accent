package main.java.embl.rieslab.photonfreecamcalib;

import org.micromanager.MenuPlugin;
import org.micromanager.Studio;
import org.scijava.plugin.Plugin;
import org.scijava.plugin.SciJavaPlugin;

import main.java.embl.rieslab.photonfreecamcalib.ui.MainFrame;
import test.ProcessImage;

@Plugin(type = MenuPlugin.class)
public class PhotonFreeCamCalibPlugin implements MenuPlugin, SciJavaPlugin {

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
		return "Photon-free calibration";
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
		MainFrame frame = new MainFrame(studio);
		frame.setVisible(true);
		//ProcessImage.main(studio);
	}

}
