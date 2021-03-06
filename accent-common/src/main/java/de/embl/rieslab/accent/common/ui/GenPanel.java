package de.embl.rieslab.accent.common.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import de.embl.rieslab.accent.common.data.calibration.CalibrationIO;
import de.embl.rieslab.accent.common.interfaces.data.CalibrationImage;
import de.embl.rieslab.accent.common.interfaces.data.RawImage;
import de.embl.rieslab.accent.common.interfaces.pipeline.PipelineController;
import de.embl.rieslab.accent.common.interfaces.ui.GeneratorPanelInterface;
import de.embl.rieslab.accent.common.utils.AccentUtils;
import de.embl.rieslab.accent.common.utils.Dialogs;

public class GenPanel<U extends RawImage, T extends CalibrationImage> extends JPanel implements GeneratorPanelInterface{

	private static final long serialVersionUID = 1L;

	private PipelineController<U, T> controller;
	
	private JTextField calibField;
	private JTextField genExposuresField;
	private JButton generateButton;
	private JButton calibButton;
	private JLabel feedbackLabel;
	
	public GenPanel(PipelineController<U,T> controller) {
		this.controller = controller;
		
		this.setBorder(new TitledBorder(null, "Generate exposure library", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagConstraints gbc_GeneratePanel = new GridBagConstraints();
		gbc_GeneratePanel.insets = new Insets(0, 0, 0, 5);
		gbc_GeneratePanel.fill = GridBagConstraints.BOTH;
		gbc_GeneratePanel.gridx = 0;
		gbc_GeneratePanel.gridy = 2;
		GridBagLayout gbl_GeneratePanel = new GridBagLayout();
		gbl_GeneratePanel.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
		gbl_GeneratePanel.rowHeights = new int[]{0, 0, 0, 0};
		gbl_GeneratePanel.columnWeights = new double[]{0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_GeneratePanel.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		this.setLayout(gbl_GeneratePanel);
		
		JLabel calibLabel = new JLabel("Calibration:");
		GridBagConstraints gbc_calibLabel = new GridBagConstraints();
		gbc_calibLabel.insets = new Insets(0, 0, 5, 5);
		gbc_calibLabel.anchor = GridBagConstraints.EAST;
		gbc_calibLabel.gridx = 0;
		gbc_calibLabel.gridy = 0;
		this.add(calibLabel, gbc_calibLabel);
		
		calibField = new JTextField();
		GridBagConstraints gbc_calibField = new GridBagConstraints();
		gbc_calibField.gridwidth = 4;
		gbc_calibField.insets = new Insets(0, 0, 5, 5);
		gbc_calibField.fill = GridBagConstraints.HORIZONTAL;
		gbc_calibField.gridx = 1;
		gbc_calibField.gridy = 0;
		this.add(calibField, gbc_calibField);
		calibField.setColumns(10);
		
		calibField.setBackground(Color.WHITE);
		
		calibButton = new JButton("...");
		calibButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showPathSelectionWindow();
			}
		});
		GridBagConstraints gbc_calibButton = new GridBagConstraints();
		gbc_calibButton.insets = new Insets(0, 0, 5, 0);
		gbc_calibButton.gridx = 6;
		gbc_calibButton.gridy = 0;
		this.add(calibButton, gbc_calibButton);
		
		JLabel genExposuresLabel = new JLabel("Exposures (ms):");
		GridBagConstraints gbc_genExposuresLabel = new GridBagConstraints();
		gbc_genExposuresLabel.anchor = GridBagConstraints.EAST;
		gbc_genExposuresLabel.insets = new Insets(0, 0, 5, 5);
		gbc_genExposuresLabel.gridx = 0;
		gbc_genExposuresLabel.gridy = 1;
		this.add(genExposuresLabel, gbc_genExposuresLabel);
		
		genExposuresField = new JTextField();
		genExposuresField.setText("15,20,30,50,100");
		GridBagConstraints gbc_genExposuresField = new GridBagConstraints();
		gbc_genExposuresField.gridwidth = 4;
		gbc_genExposuresField.insets = new Insets(0, 0, 5, 5);
		gbc_genExposuresField.fill = GridBagConstraints.HORIZONTAL;
		gbc_genExposuresField.gridx = 1;
		gbc_genExposuresField.gridy = 1;
		this.add(genExposuresField, gbc_genExposuresField);
		genExposuresField.setColumns(10);
		
		genExposuresField.setBackground(Color.WHITE);
		
		generateButton = new JButton("Generate");
		generateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				startMapGeneration();
			}
		});
		generateButton.setFont(new Font("Tahoma", Font.PLAIN, 12));
		GridBagConstraints gbc_generateButton = new GridBagConstraints();
		gbc_generateButton.insets = new Insets(0, 0, 2, 0);
		gbc_generateButton.fill = GridBagConstraints.VERTICAL;
		gbc_generateButton.gridheight = 2;
		gbc_generateButton.gridx = 6;
		gbc_generateButton.gridy = 1;
		this.add(generateButton, gbc_generateButton);
		
		feedbackLabel = new JLabel(" ");
		feedbackLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
		GridBagConstraints gbc_feedbackLabel = new GridBagConstraints();
		gbc_feedbackLabel.insets = new Insets(0, 0, 0, 5);
		gbc_feedbackLabel.gridx = 3;
		gbc_feedbackLabel.gridy = 2;
		this.add(feedbackLabel, gbc_feedbackLabel);
	}
	
	public double[] getExposures() {
		String[] exp = genExposuresField.getText().split(",");
		
		boolean badInput = false;
		StringBuilder sb = new StringBuilder("The following exposures are invalid and will not be generated:\n");
		ArrayList<Double> list = new ArrayList<Double>();
		for(int i=0;i<exp.length;i++) {
			if(AccentUtils.isNumeric(exp[i])) {
				list.add(Double.valueOf(exp[i]));
			} else {
				sb.append("<");
				sb.append(exp[i]);
				sb.append(">\n");
				badInput = true;
			}
		}
		
		if(badInput) { // maybe better to give a choice to continue or not...
			Dialogs.showErrorMessage(sb.toString());
		}
		
		double[] vals = new double[list.size()];
		for(int i=0;i<list.size();i++) {
			vals[i] = list.get(i);
		}
		
		return vals;
	}
	
	private void startMapGeneration() {
		if(!calibField.getText().isEmpty()) {
			controller.startGenerator(calibField.getText(), getExposures());
		} else {
			Dialogs.showWarningMessage("Select a path.");
		}
	}

	protected void showPathSelectionWindow() {
		JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(new java.io.File("."));
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Calibration files", CalibrationIO.CALIB_EXT);
		fc.setFileFilter(filter);
		int returnVal = fc.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File folder = fc.getSelectedFile();
			calibField.setText(folder.getAbsolutePath());
		}
	}

	@Override
	public void setProgress(String progress) {
		feedbackLabel.setText(progress);
	}

	@Override
	public void setCalibrationPath(String path) {
		calibField.setText(path);
	}

}
