package main.java.embl.rieslab.photonfreecamcalib.ui;


import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.micromanager.Studio;

import main.java.embl.rieslab.photonfreecamcalib.PipelineController;

public class MainFrame extends JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = -903001376907979987L;

	/**
	 * Create the frame.
	 */
	public MainFrame(Studio studio) {

		// controller 
		PipelineController controller = new PipelineController(studio);
		
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		JPanel content = new JPanel();
		GridBagLayout gbl_content = new GridBagLayout();
		gbl_content.columnWidths = new int[] {355};
		gbl_content.rowHeights = new int[] {50, 40, 40};
		gbl_content.columnWeights = new double[] { 0.0 };
		gbl_content.rowWeights = new double[] { 0.0, 0.0, 0.0 };
		content.setLayout(gbl_content);

		AcqPanel acqpane = new AcqPanel(studio.getCMMCore().getCameraDevice(), controller);

		controller.setAcquisitionPanel(acqpane);

		GridBagConstraints gbc_acqpane = new GridBagConstraints();
		gbc_acqpane.weighty = 0.2;
		gbc_acqpane.weightx = 0.2;
		gbc_acqpane.fill = GridBagConstraints.HORIZONTAL;
		gbc_acqpane.gridx = 0;
		gbc_acqpane.gridy = 0;
		content.add(acqpane, gbc_acqpane);
		ProcPanel procpane = new ProcPanel(controller);
		controller.setProcessingPanel(procpane);
		GridBagConstraints gbc_procpane = new GridBagConstraints();
		gbc_procpane.weighty = 0.2;
		gbc_procpane.weightx = 0.2;
		gbc_procpane.fill = GridBagConstraints.HORIZONTAL;
		gbc_procpane.gridx = 0;
		gbc_procpane.gridy = 1;
		content.add(procpane, gbc_procpane);
		GenPanel genpane = new GenPanel(controller);
		controller.setGeneratePanel(genpane);
		GridBagConstraints gbc_genpane = new GridBagConstraints();
		gbc_genpane.weighty = 0.2;
		gbc_genpane.weightx = 0.2;
		gbc_genpane.fill = GridBagConstraints.HORIZONTAL;
		gbc_genpane.gridx = 0;
		gbc_genpane.gridy = 2;
		content.add(genpane, gbc_genpane);
		
		this.setContentPane(content);
		
		this.pack();
		this.setLocationRelativeTo(null);
	}

}
