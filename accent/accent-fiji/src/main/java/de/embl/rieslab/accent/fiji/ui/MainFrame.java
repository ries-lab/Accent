package de.embl.rieslab.accent.fiji.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import de.embl.rieslab.accent.common.interfaces.pipeline.PipelineController;
import de.embl.rieslab.accent.common.interfaces.ui.GeneratePanelInterface;
import de.embl.rieslab.accent.common.interfaces.ui.ProcessingPanelInterface;
import de.embl.rieslab.accent.common.ui.GenPanel;
import de.embl.rieslab.accent.fiji.data.image.PlaneImg;
import de.embl.rieslab.accent.fiji.data.image.StackImg;

public class MainFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private TableProcPanel procpane;
	private GenPanel<StackImg,PlaneImg> genpane;
	
	public MainFrame(PipelineController<StackImg,PlaneImg> controller, List<String> datasets) {

		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setTitle("Accent");

		JPanel content = new JPanel();
		GridBagLayout gbl_content = new GridBagLayout();
		gbl_content.columnWidths = new int[] { 355 };
		gbl_content.rowHeights = new int[] { 50, 40, 40 };
		gbl_content.columnWeights = new double[] { 0.0 };
		gbl_content.rowWeights = new double[] { 0.0, 0.0, 0.0 };
		content.setLayout(gbl_content);

		int counter = 0;
	
		procpane = new TableProcPanel(controller, datasets);
		controller.setProcessingPanel(procpane);
		GridBagConstraints gbc_procpane = new GridBagConstraints();
		gbc_procpane.weighty = 0.2;
		gbc_procpane.weightx = 0.2;
		gbc_procpane.fill = GridBagConstraints.HORIZONTAL;
		gbc_procpane.gridx = 0;
		gbc_procpane.gridy = counter++;
		content.add(procpane, gbc_procpane);

		genpane = new GenPanel<StackImg,PlaneImg>(controller);
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

	public ProcessingPanelInterface getProcessingPanel() {
		return procpane;
	}

	public GeneratePanelInterface getGeneratePanel() {
		return genpane;
	}

}
