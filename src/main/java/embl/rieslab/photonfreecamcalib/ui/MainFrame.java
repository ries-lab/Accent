package main.java.embl.rieslab.photonfreecamcalib.ui;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.micromanager.Studio;

import main.java.embl.rieslab.photonfreecamcalib.PipelineController;

public class MainFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -903001376907979987L;

	/**
	 * Create the frame.
	 */
	public MainFrame(Studio studio) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel content = new JPanel();
		content.setLayout(new BoxLayout(content, BoxLayout.PAGE_AXIS));
		
		PipelineController controller = new PipelineController(studio);
		AcquirePanel acqpane = new AcquirePanel(studio, controller); 
		ProcessPanel procpane = new ProcessPanel(controller);
		controller.addAcquisitionPanel(acqpane);
		controller.addProcessingPanel(procpane);
		content.add(acqpane);
		content.add(procpane);
		
		this.setContentPane(content);
		this.pack();
		this.setLocationRelativeTo(null);
		this.setResizable(false);
	}

}
