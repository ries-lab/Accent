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
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.border.TitledBorder;

import main.java.embl.rieslab.photonfreecamcalib.PipelineController;
import main.java.embl.rieslab.photonfreecamcalib.analysis.AnalysisPanelInterface;


public class AnalysisPanel extends JPanel  implements AnalysisPanelInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8617197911429586911L;
	private JTextField textField;
	private JProgressBar progressBar;
	private JToggleButton btnAnalyze;
	private PipelineController controller;

	/**
	 * Create the panel.
	 */
	public AnalysisPanel(PipelineController controller) {
		this.controller = controller;
		
		setBorder(new TitledBorder(null, "Analyze", TitledBorder.LEFT, TitledBorder.TOP, null, null));
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
		
		btnAnalyze = new JToggleButton("Start");
		btnAnalyze.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent state) {
				if (state.getStateChange() == ItemEvent.SELECTED) {
					startAnalysis();
				} else {
					stopAnalysis();
				}
			}
		});
		
		GridBagConstraints gbc_btnProcess = new GridBagConstraints();
		gbc_btnProcess.fill = GridBagConstraints.BOTH;
		gbc_btnProcess.gridx = 2;
		gbc_btnProcess.gridy = 1;
		add(btnAnalyze, gbc_btnProcess);

	}

	
	protected void stopAnalysis() {
		controller.stopAnalyzer();
	}


	protected void startAnalysis() {
		controller.startAnalyzer(textField.getText());
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
	public void analysisHasStarted() {
		progressBar.setValue(0);
		btnAnalyze.setText("Stop");
	}


	@Override
	public void analysisHasStopped() {
		btnAnalyze.setText("Start");
		btnAnalyze.setSelected(false);
	}


	@Override
	public void analysisHasEnded() {
		progressBar.setValue(100);
		btnAnalyze.setText("Start");
		btnAnalyze.setSelected(false);
	}


	@Override
	public void setDataPath(String path) {
		textField.setText(path);
	}
}
