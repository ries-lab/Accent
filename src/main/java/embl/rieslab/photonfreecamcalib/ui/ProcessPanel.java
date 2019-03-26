package main.java.embl.rieslab.photonfreecamcalib.ui;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import main.java.embl.rieslab.photonfreecamcalib.PipelineController;
import main.java.embl.rieslab.photonfreecamcalib.processing.ProcessingPanelInterface;

import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JProgressBar;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

public class ProcessPanel extends JPanel implements ProcessingPanelInterface {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5981823617164261234L;
	private JTextField textField;
	private JProgressBar progressBar;
	private JToggleButton btnProcess;
	private PipelineController controller;

	/**
	 * Create the panel.
	 */
	public ProcessPanel(PipelineController controller) {
		this.controller = controller;
		
		setBorder(new TitledBorder(null, "Process", TitledBorder.LEFT, TitledBorder.TOP, null, null));
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
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.insets = new Insets(0, 0, 5, 5);
		gbc_textField.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField.gridx = 1;
		gbc_textField.gridy = 0;
		add(textField, gbc_textField);
		textField.setColumns(10);
		
		JButton button = new JButton("...");
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
		
		progressBar = new JProgressBar();
		GridBagConstraints gbc_progressBar = new GridBagConstraints();
		gbc_progressBar.fill = GridBagConstraints.HORIZONTAL;
		gbc_progressBar.gridwidth = 2;
		gbc_progressBar.insets = new Insets(0, 5, 0, 5);
		gbc_progressBar.gridx = 0;
		gbc_progressBar.gridy = 1;
		add(progressBar, gbc_progressBar);
		
		btnProcess = new JToggleButton("Start");
		btnProcess.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent state) {
				if (state.getStateChange() == ItemEvent.SELECTED) {
					startProcessing();
				} else {
					stopProcessing();
				}
			}
		});
		
		GridBagConstraints gbc_btnProcess = new GridBagConstraints();
		gbc_btnProcess.fill = GridBagConstraints.BOTH;
		gbc_btnProcess.gridx = 2;
		gbc_btnProcess.gridy = 1;
		add(btnProcess, gbc_btnProcess);

	}

	
	protected void stopProcessing() {
		controller.stopProcessor();
	}


	protected void startProcessing() {
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
	public void setProgress(int progress) {
		progressBar.setValue(progress);
	}


	@Override
	public void procHasStarted() {
		progressBar.setValue(0);
		btnProcess.setText("Stop");
	}


	@Override
	public void procHasStopped() {
		btnProcess.setText("Start");
		btnProcess.setSelected(false);
	}


	@Override
	public void procHasEnded() {
		progressBar.setValue(100);
		btnProcess.setText("Start");
		btnProcess.setSelected(false);
	}


	@Override
	public void setDataPath(String path) {
		textField.setText(path);
	}
}
