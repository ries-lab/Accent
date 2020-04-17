package de.embl.rieslab.accent.mm2.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import de.embl.rieslab.accent.common.interfaces.pipeline.PipelineController;
import de.embl.rieslab.accent.common.interfaces.ui.ProcessorPanelInterface;
import de.embl.rieslab.accent.common.utils.Dialogs;
import de.embl.rieslab.accent.mm2.data.image.BareImage;
import de.embl.rieslab.accent.mm2.data.image.FloatImage;

public class ProcPanel extends JPanel implements ProcessorPanelInterface {
	
	private static final long serialVersionUID = 1L;
	private JTextField folderField;
	private JProgressBar procProgressBar;
	private JButton folderButton;
	private JLabel procFeedbackLabel;
	private JToggleButton processButton;
	
	private final static String START = "Process";
	private final static String STOP = "Stop";
		
	private PipelineController<BareImage, FloatImage> controller;
	private boolean preventTrigger = false;

	/**
	 * Create the panel.
	 */
	public ProcPanel(PipelineController<BareImage, FloatImage> controller) {
		this.controller = controller;
		
		this.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Process raw data", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		GridBagConstraints gbc_ProcessPanel = new GridBagConstraints();
		gbc_ProcessPanel.insets = new Insets(0, 0, 5, 5);
		gbc_ProcessPanel.fill = GridBagConstraints.BOTH;
		gbc_ProcessPanel.gridx = 0;
		gbc_ProcessPanel.gridy = 1;
		GridBagLayout gbl_ProcessPanel = new GridBagLayout();
		gbl_ProcessPanel.columnWidths = new int[]{0, 0, 0, 0, 0};
		gbl_ProcessPanel.rowHeights = new int[]{0, 0, 0, 0};
		gbl_ProcessPanel.columnWeights = new double[]{0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
		gbl_ProcessPanel.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		this.setLayout(gbl_ProcessPanel);  
		
		JLabel folderLabel = new JLabel("Folder:");
		GridBagConstraints gbc_folderLabel = new GridBagConstraints();
		gbc_folderLabel.insets = new Insets(0, 0, 5, 5);
		gbc_folderLabel.anchor = GridBagConstraints.EAST;
		gbc_folderLabel.gridx = 0;
		gbc_folderLabel.gridy = 0;
		this.add(folderLabel, gbc_folderLabel);
		
		folderField = new JTextField();
		GridBagConstraints gbc_folderField = new GridBagConstraints();
		gbc_folderField.gridwidth = 2;
		gbc_folderField.insets = new Insets(0, 0, 5, 5);
		gbc_folderField.fill = GridBagConstraints.HORIZONTAL;
		gbc_folderField.gridx = 1;
		gbc_folderField.gridy = 0;
		this.add(folderField, gbc_folderField);
		folderField.setColumns(10);
		
		folderField.setBackground(Color.WHITE);
		
		folderButton = new JButton("...");
		folderButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {    	
				showPathSelectionWindow();
			}
		});
		GridBagConstraints gbc_folderButton = new GridBagConstraints();
		gbc_folderButton.insets = new Insets(0, 0, 5, 0);
		gbc_folderButton.gridx = 3;
		gbc_folderButton.gridy = 0;
		this.add(folderButton, gbc_folderButton);
		
		procFeedbackLabel = new JLabel("    ");
		procFeedbackLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
		GridBagConstraints gbc_procFeedbackLabel = new GridBagConstraints();
		gbc_procFeedbackLabel.insets = new Insets(0, 0, 5, 5);
		gbc_procFeedbackLabel.gridx = 2;
		gbc_procFeedbackLabel.gridy = 1;
		this.add(procFeedbackLabel, gbc_procFeedbackLabel);
		
		processButton = new JToggleButton(START);
		processButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				AbstractButton abstractButton = (AbstractButton) actionEvent.getSource();
				boolean selected = abstractButton.getModel().isSelected();
				if(!preventTrigger) {
					if (selected) {
						startProcessing();
					} else {
						stopProcessing();
					}
				}
			}
		});
		
		processButton.setFont(new Font("Tahoma", Font.PLAIN, 12));
		GridBagConstraints gbc_processButton = new GridBagConstraints();
		gbc_processButton.fill = GridBagConstraints.VERTICAL;
		gbc_processButton.gridheight = 2;
		gbc_processButton.gridx = 3;
		gbc_processButton.gridy = 1;
		this.add(processButton, gbc_processButton);
		
		procProgressBar = new JProgressBar();
		GridBagConstraints gbc_procProgressBar = new GridBagConstraints();
		gbc_procProgressBar.fill = GridBagConstraints.HORIZONTAL;
		gbc_procProgressBar.insets = new Insets(0, 0, 0, 5);
		gbc_procProgressBar.gridx = 2;
		gbc_procProgressBar.gridy = 2;
		this.add(procProgressBar, gbc_procProgressBar);
	}
	
	protected void stopProcessing() {
		controller.stopProcessor();	
	}

	private void selectProcessToggle(boolean b) {
		preventTrigger = true;
		processButton.setSelected(b);
		preventTrigger = false;
	}
	
	protected void startProcessing() {
		if(!controller.isProcessorRunning()) { // avoid trigger from setSelected(true) in processingHasStarted()
			String path = folderField.getText();
			if(!path.isEmpty()) {
				boolean b = controller.startProcessor(path);
				if(b) {
					processButton.setText(STOP);
					selectProcessToggle(true);
				}	
			} else {
				selectProcessToggle(false);
				Dialogs.showWarningMessage("Select a path.");
			}
		} else {
			selectProcessToggle(false);
			Dialogs.showWarningMessage("Processor already running..");
		}
	}

	protected void showPathSelectionWindow() {
		JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(new java.io.File("."));
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = fc.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File folder = fc.getSelectedFile();
			folderField.setText(folder.getAbsolutePath());
		}
	}


	@Override
	public void setProgress(String progress, int percentage) {
		procProgressBar.setValue(percentage);
		procFeedbackLabel.setText(progress);
	}


	@Override
	public void processingHasStarted() {
		procProgressBar.setValue(0);
		processButton.setText(STOP);
		if(!processButton.isSelected()) {
			processButton.setSelected(true);
		}
	}


	@Override
	public void processingHasStopped() {
		processButton.setText(START);
		processButton.setSelected(false);
	}


	@Override
	public void processingHasEnded() {
		procProgressBar.setValue(100);
		processButton.setText(START);
		processButton.setSelected(false);
	}


	@Override
	public void setDataPath(String path) {
		folderField.setText(path);
	}

}
