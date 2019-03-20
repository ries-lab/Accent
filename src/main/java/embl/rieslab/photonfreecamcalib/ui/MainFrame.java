package main.java.embl.rieslab.photonfreecamcalib.ui;

import java.awt.Dimension;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.micromanager.Studio;

public class MainFrame extends JFrame {

	private JPanel contentPane;
	private Studio studio;

	/**
	 * Create the frame.
	 */
	public MainFrame(Studio studio) {
		this.studio = studio;
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		this.add(new AcquirePanel(studio));
		//this.pack();
		this.setLocationRelativeTo(null);
		this.setPreferredSize(new Dimension(360,265));
		this.setSize(new Dimension(360,265));
		this.setResizable(false);
	}

}
