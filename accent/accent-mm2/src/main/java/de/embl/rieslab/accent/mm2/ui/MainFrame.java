package de.embl.rieslab.accent.mm2.ui;


import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.micromanager.Studio;

import de.embl.rieslab.accent.common.ui.GenPanel;
import de.embl.rieslab.accent.mm2.MM2Controller;

public class MainFrame extends JFrame{

	private static final long serialVersionUID = 1L;
	private AcqPanel acqpane;
	private ProcPanel procpane;
	private GenPanel genpane;
	
	/**
	 * Create the frame.
	 */
	public MainFrame(Studio studio, MM2Controller controller) {
		
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setTitle("Accent");

		JPanel content = new JPanel();
		GridBagLayout gbl_content = new GridBagLayout();
		gbl_content.columnWidths = new int[] {355};
		gbl_content.rowHeights = new int[] {50, 40, 40};
		gbl_content.columnWeights = new double[] { 0.0 };
		gbl_content.rowWeights = new double[] { 0.0, 0.0, 0.0 };
		content.setLayout(gbl_content);

		int counter = 0;
		
		acqpane = new AcqPanel(studio.getCMMCore().getCameraDevice(), controller, controller.getAcqController());
		controller.getAcqController().setAcquisitionPanel(acqpane);
		GridBagConstraints gbc_acqpane = new GridBagConstraints();
		gbc_acqpane.weighty = 0.2;
		gbc_acqpane.weightx = 0.2;
		gbc_acqpane.fill = GridBagConstraints.HORIZONTAL;
		gbc_acqpane.gridx = 0;
		gbc_acqpane.gridy = counter++;
		content.add(acqpane, gbc_acqpane);

		procpane = new ProcPanel(controller);
		controller.setProcessingPanel(procpane);
		GridBagConstraints gbc_procpane = new GridBagConstraints();
		gbc_procpane.weighty = 0.2;
		gbc_procpane.weightx = 0.2;
		gbc_procpane.fill = GridBagConstraints.HORIZONTAL;
		gbc_procpane.gridx = 0;
		gbc_procpane.gridy = counter++;
		content.add(procpane, gbc_procpane);

		genpane = new GenPanel(controller);
		controller.setGeneratePanel(genpane);
		GridBagConstraints gbc_genpane = new GridBagConstraints();
		gbc_genpane.weighty = 0.2;
		gbc_genpane.weightx = 0.2;
		gbc_genpane.fill = GridBagConstraints.HORIZONTAL;
		gbc_genpane.gridx = 0;
		gbc_genpane.gridy = counter++;
		content.add(genpane, gbc_genpane);
		
		this.setContentPane(content);
		
		this.pack();
		this.setLocationRelativeTo(null);
	}

}
