package main.java.embl.rieslab.photonfreecamcalib.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import org.micromanager.data.Datastore;

import main.java.embl.rieslab.photonfreecamcalib.PipelineController;
import main.java.embl.rieslab.photonfreecamcalib.acquisition.AcquisitionPanelInterface;
import main.java.embl.rieslab.photonfreecamcalib.acquisition.AcquisitionSettings;
import main.java.embl.rieslab.photonfreecamcalib.utils.utils;

public class AcqPanel extends JPanel implements AcquisitionPanelInterface {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5228290693013659054L;
	private boolean alternatedAcquisition;
	private boolean saveAsStacks;
	private boolean parallelProcessing;
	
	private JTextField expNameField;
	private JTextField savePathField;
	private JTextField acqExposuresField;
	private JButton savePathButton;
	private JLabel acqFeeedbackLabel;
	private JSpinner framesSpinner;
	private JToggleButton acquireButton;
	private JProgressBar acqProgressBar;
	private JButton optionsButton;

	private final static String ACQ_START = "Acquire";
	private final static String ACQ_STOP = "Stop";
	
	private PipelineController controller;

	public AcqPanel(String camera, PipelineController controller) {
		this.controller = controller;

		alternatedAcquisition = true;
		saveAsStacks = true;
		parallelProcessing = true;
		
		JPanel AcqPanel = new JPanel();
		AcqPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Acquire raw data", TitledBorder.LEFT, TitledBorder.TOP, null, new Color(0, 0, 0)));
		GridBagConstraints gbc_AcqPanel = new GridBagConstraints();
		gbc_AcqPanel.insets = new Insets(0, 0, 5, 5);
		gbc_AcqPanel.fill = GridBagConstraints.BOTH;
		gbc_AcqPanel.gridx = 0;
		gbc_AcqPanel.gridy = 0;

		GridBagLayout gbl_AcqPanel = new GridBagLayout();
		gbl_AcqPanel.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_AcqPanel.rowHeights = new int[]{0, 0, 0, 0, 0, 0};
		gbl_AcqPanel.columnWeights = new double[]{0.0, 0.0, 0.0, 1.0, 1.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_AcqPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		AcqPanel.setLayout(gbl_AcqPanel);
		
		JLabel expNameLbl = new JLabel("Name:");
		GridBagConstraints gbc_expNameLbl = new GridBagConstraints();
		gbc_expNameLbl.insets = new Insets(0, 0, 5, 5);
		gbc_expNameLbl.anchor = GridBagConstraints.EAST;
		gbc_expNameLbl.gridx = 0;
		gbc_expNameLbl.gridy = 0;
		AcqPanel.add(expNameLbl, gbc_expNameLbl);
		
		expNameField = new JTextField();
		GridBagConstraints gbc_expNameField = new GridBagConstraints();
		gbc_expNameField.gridwidth = 3;
		gbc_expNameField.insets = new Insets(0, 0, 5, 5);
		gbc_expNameField.fill = GridBagConstraints.HORIZONTAL;
		gbc_expNameField.gridx = 1;
		gbc_expNameField.gridy = 0;
		AcqPanel.add(expNameField, gbc_expNameField);
		expNameField.setColumns(10);
		
		savePathButton = new JButton("...");
		savePathButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		GridBagConstraints gbc_savePathButton = new GridBagConstraints();
		gbc_savePathButton.insets = new Insets(0, 0, 5, 0);
		gbc_savePathButton.gridx = 7;
		gbc_savePathButton.gridy = 0;
		AcqPanel.add(savePathButton, gbc_savePathButton);
		
		JLabel savePathLabel = new JLabel("Path:");
		GridBagConstraints gbc_savePathLabel = new GridBagConstraints();
		gbc_savePathLabel.insets = new Insets(0, 0, 5, 5);
		gbc_savePathLabel.anchor = GridBagConstraints.EAST;
		gbc_savePathLabel.gridx = 0;
		gbc_savePathLabel.gridy = 1;
		AcqPanel.add(savePathLabel, gbc_savePathLabel);
		
		savePathField = new JTextField();
		GridBagConstraints gbc_savePathField = new GridBagConstraints();
		gbc_savePathField.insets = new Insets(0, 0, 5, 0);
		gbc_savePathField.gridwidth = 7;
		gbc_savePathField.fill = GridBagConstraints.HORIZONTAL;
		gbc_savePathField.gridx = 1;
		gbc_savePathField.gridy = 1;
		AcqPanel.add(savePathField, gbc_savePathField);
		savePathField.setColumns(10);
		
		JLabel nFramesLabel = new JLabel("# frames");
		GridBagConstraints gbc_nFramesLabel = new GridBagConstraints();
		gbc_nFramesLabel.insets = new Insets(0, 0, 5, 5);
		gbc_nFramesLabel.gridx = 0;
		gbc_nFramesLabel.gridy = 2;
		AcqPanel.add(nFramesLabel, gbc_nFramesLabel);
		
		framesSpinner = new JSpinner();
		framesSpinner.setModel(new SpinnerNumberModel(new Integer(20000), new Integer(1), null, new Integer(1000)));
		GridBagConstraints gbc_framesSpinner = new GridBagConstraints();
		gbc_framesSpinner.fill = GridBagConstraints.HORIZONTAL;
		gbc_framesSpinner.gridwidth = 2;
		gbc_framesSpinner.insets = new Insets(0, 0, 5, 5);
		gbc_framesSpinner.gridx = 1;
		gbc_framesSpinner.gridy = 2;
		AcqPanel.add(framesSpinner, gbc_framesSpinner);
		
		acquireButton = new JToggleButton(ACQ_START);
		acquireButton.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent state) {
				if (state.getStateChange() == ItemEvent.SELECTED) {
					runAcquisition();
				} else {
					stopAcquisition();
				}
			}
		});
		acquireButton.setFont(new Font("Tahoma", Font.BOLD, 12));
		GridBagConstraints gbc_acquireButton = new GridBagConstraints();
		gbc_acquireButton.fill = GridBagConstraints.VERTICAL;
		gbc_acquireButton.gridheight = 2;
		gbc_acquireButton.insets = new Insets(8, 0, 5, 0);
		gbc_acquireButton.gridx = 7;
		gbc_acquireButton.gridy = 2;
		AcqPanel.add(acquireButton, gbc_acquireButton);
		
		JLabel acqExposuresLabel = new JLabel("Exposures:");
		GridBagConstraints gbc_acqExposuresLabel = new GridBagConstraints();
		gbc_acqExposuresLabel.insets = new Insets(0, 0, 5, 5);
		gbc_acqExposuresLabel.gridx = 0;
		gbc_acqExposuresLabel.gridy = 3;
		AcqPanel.add(acqExposuresLabel, gbc_acqExposuresLabel);
		
		acqExposuresField = new JTextField();
		acqExposuresField.setText("10,500,1000");
		GridBagConstraints gbc_acqExposuresField = new GridBagConstraints();
		gbc_acqExposuresField.gridwidth = 2;
		gbc_acqExposuresField.insets = new Insets(0, 0, 5, 5);
		gbc_acqExposuresField.fill = GridBagConstraints.HORIZONTAL;
		gbc_acqExposuresField.gridx = 1;
		gbc_acqExposuresField.gridy = 3;
		AcqPanel.add(acqExposuresField, gbc_acqExposuresField);
		acqExposuresField.setColumns(10);
		
		optionsButton = new JButton("Options");
		optionsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				showOptionFrame();
			}
		});
		GridBagConstraints gbc_optionsButton = new GridBagConstraints();
		gbc_optionsButton.insets = new Insets(0, 0, 5, 5);
		gbc_optionsButton.gridx = 6;
		gbc_optionsButton.gridy = 3;
		AcqPanel.add(optionsButton, gbc_optionsButton);
		
		acqFeeedbackLabel = new JLabel("     ");
		acqFeeedbackLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
		GridBagConstraints gbc_acqFeeedbackLabel = new GridBagConstraints();
		gbc_acqFeeedbackLabel.insets = new Insets(0, 0, 0, 4);
		gbc_acqFeeedbackLabel.anchor = GridBagConstraints.EAST;
		gbc_acqFeeedbackLabel.gridwidth = 2;
		gbc_acqFeeedbackLabel.gridx = 1;
		gbc_acqFeeedbackLabel.gridy = 4;
		AcqPanel.add(acqFeeedbackLabel, gbc_acqFeeedbackLabel);
		
		acqProgressBar = new JProgressBar();
		GridBagConstraints gbc_acqProgressBar = new GridBagConstraints();
		gbc_acqProgressBar.fill = GridBagConstraints.HORIZONTAL;
		gbc_acqProgressBar.gridwidth = 5;
		gbc_acqProgressBar.gridx = 3;
		gbc_acqProgressBar.gridy = 4;
		AcqPanel.add(acqProgressBar, gbc_acqProgressBar);
	}

	protected void showOptionFrame() {
		AcqOptionFrame opFrame = new AcqOptionFrame(this);
		opFrame.setVisible(true);
	}

	protected void showPathSelectionWindow() {
		JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(new java.io.File("."));
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = fc.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File folder = fc.getSelectedFile();
			savePathField.setText(folder.getAbsolutePath());
		}
	}

	protected String getAcqName() {
		return expNameField.getText();
	}
	
	protected String getAcqPath() {
		return savePathField.getText();
	}
	
	protected int getFrames() {
		return (int) framesSpinner.getValue();
	}
	
	protected Integer[] getExposures() {
		String[] exp = acqExposuresField.getText().split(",");
		
		ArrayList<Integer> list = new ArrayList<Integer>();
		for(int i=0;i<exp.length;i++) {
			if(utils.isInteger(exp[i])) {
				list.add(Integer.valueOf(exp[i]));
			}
		}
		return list.toArray(new Integer[0]);
	}

	private void runAcquisition() {
		
		AcquisitionSettings settings  = new AcquisitionSettings();
		
		settings.name_ = getAcqName();
		settings.folder_ = getAcqPath();
		settings.numFrames_ = getFrames();
		settings.exposures_ = getExposures();
		
		if(saveAsStacks) {
			settings.saveMode_ = Datastore.SaveMode.MULTIPAGE_TIFF;
		} else {
			settings.saveMode_ = Datastore.SaveMode.SINGLEPLANE_TIFF_SERIES;
		}
		
		settings.multiplexedAcq = alternatedAcquisition;
		settings.parallelProcessing = parallelProcessing;
				
		if(settings.folder_ != null || !settings.folder_.equals("")) {
			controller.startAcquisition(settings);
		}
	}
	
	private void stopAcquisition() {
			controller.stopAcquisition();
	}

	@Override
	public void setProgress(int currStep, int totStep, int progress) {
		if(progress>=0 && progress<=100) {
			acqProgressBar.setValue(progress);
		}
		acqFeeedbackLabel.setText(currStep+"/"+totStep);
	}

	@Override
	public void acqHasStarted() {
		acqProgressBar.setValue(0);
		acquireButton.setText(ACQ_STOP);
	}

	@Override
	public void acqHasStopped() {
		acquireButton.setText(ACQ_START);
		acquireButton.setSelected(false);
	}

	@Override
	public void acqHasEnded() {
		acqProgressBar.setValue(100);
		acquireButton.setText(ACQ_START);	
		acquireButton.setSelected(false);	
	}

	@Override
	public void setAdvancedSettings(boolean alternatedAcquisition, boolean saveAsStacks, boolean parallelProcessing) {
		this.alternatedAcquisition = alternatedAcquisition;
		this.saveAsStacks = saveAsStacks;
		this.parallelProcessing = parallelProcessing;
	}

}
