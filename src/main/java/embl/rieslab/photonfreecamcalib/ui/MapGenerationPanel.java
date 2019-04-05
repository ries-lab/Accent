package main.java.embl.rieslab.photonfreecamcalib.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.border.TitledBorder;

import main.java.embl.rieslab.photonfreecamcalib.PipelineController;
import main.java.embl.rieslab.photonfreecamcalib.generator.GeneratorPanelInterface;

public class MapGenerationPanel extends JPanel  implements GeneratorPanelInterface {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -7526823187498218297L;
	private JTextField textField;
	private JToggleButton btnGenerate;
	private PipelineController controller;
	private JTextField exposureTextfield;

	/**
	 * Create the panel.
	 */
	public MapGenerationPanel(PipelineController controller) {
		this.controller = controller;
		
		setBorder(new TitledBorder(null, "Generate", TitledBorder.LEFT, TitledBorder.TOP, null, null));
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] {0, 0, 0};
		gridBagLayout.rowHeights = new int[] {30, 40};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, 0.0};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0};
		setLayout(gridBagLayout);
		
		JLabel lblPath = new JLabel("Path:");
		GridBagConstraints gbc_lblPath = new GridBagConstraints();
		gbc_lblPath.insets = new Insets(0, 0, 5, 5);
		gbc_lblPath.anchor = GridBagConstraints.EAST;
		gbc_lblPath.gridx = 0;
		gbc_lblPath.gridy = 0;
		add(lblPath, gbc_lblPath);
		
		textField = new JTextField();
		textField.setToolTipText("Path to the calibration file (.calb)");
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.insets = new Insets(0, 0, 5, 5);
		gbc_textField.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField.gridx = 1;
		gbc_textField.gridy = 0;
		add(textField, gbc_textField);
		textField.setColumns(10);
		
		JButton button = new JButton("...");
		button.setToolTipText("Press to select path to the calibration file (.calb).");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {    	
				showPathSelectionWindow();
			}
		});
		GridBagConstraints gbc_button = new GridBagConstraints();
		gbc_button.insets = new Insets(0, 0, 5, 0);
		gbc_button.gridx = 2;
		gbc_button.gridy = 0;
		add(button, gbc_button);

		
		btnGenerate = new JToggleButton("Generate Maps");
		btnGenerate.setToolTipText("Press to generate Avg and Var maps for the different exposures.");
		btnGenerate.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent state) {
				if (state.getStateChange() == ItemEvent.SELECTED) {
					startProcessing();
				} else {
					stopProcessing();
				}
			}
		});
		
		JLabel lblExposures = new JLabel("Exposures:");
		GridBagConstraints gbc_lblExposures = new GridBagConstraints();
		gbc_lblExposures.anchor = GridBagConstraints.EAST;
		gbc_lblExposures.insets = new Insets(0, 0, 0, 5);
		gbc_lblExposures.gridx = 0;
		gbc_lblExposures.gridy = 1;
		add(lblExposures, gbc_lblExposures);
		
		exposureTextfield = new JTextField();
		exposureTextfield.setToolTipText("Enter the exposures (comma separated) for which you want to generate the maps.");
		GridBagConstraints gbc_exposureTextfield = new GridBagConstraints();
		gbc_exposureTextfield.insets = new Insets(0, 0, 0, 5);
		gbc_exposureTextfield.fill = GridBagConstraints.HORIZONTAL;
		gbc_exposureTextfield.gridx = 1;
		gbc_exposureTextfield.gridy = 1;
		add(exposureTextfield, gbc_exposureTextfield);
		exposureTextfield.setColumns(10);
		
		GridBagConstraints gbc_btnProcess = new GridBagConstraints();
		gbc_btnProcess.fill = GridBagConstraints.BOTH;
		gbc_btnProcess.gridx = 2;
		gbc_btnProcess.gridy = 1;
		add(btnGenerate, gbc_btnProcess);

	}

	
	protected void stopProcessing() {
		controller.stopProcessor();
	}


	protected void startProcessing() {
		System.out.println("Processing demanded by panel");
		controller.startProcessor(textField.getText());
	}


	protected void showPathSelectionWindow() {
		JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(new java.io.File("."));
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = fc.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File folder = fc.getSelectedFile();
			textField.setText(folder.getAbsolutePath());
		}
	}


	@Override
	public void processingHasStarted() {
		btnGenerate.setText("Stop");
	}


	@Override
	public void processingHasStopped() {
		btnGenerate.setText("Generate Maps");
		btnGenerate.setSelected(false);
	}


	@Override
	public void processingHasEnded() {
		btnGenerate.setText("Generate Maps");
		btnGenerate.setSelected(false);
	}
}
