package main.java.embl.rieslab.photonfreecamcalib.ui;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.micromanager.Studio;
import org.micromanager.data.Datastore;

import ij.gui.Roi;
import ij.plugin.frame.RoiManager;
import main.java.embl.rieslab.photonfreecamcalib.acquisition.Acquisition;
import main.java.embl.rieslab.photonfreecamcalib.acquisition.AcquisitionFactory;
import main.java.embl.rieslab.photonfreecamcalib.acquisition.AcquisitionPanelInterface;
import main.java.embl.rieslab.photonfreecamcalib.acquisition.AcquisitionSettings;
import main.java.embl.rieslab.photonfreecamcalib.utils.utils;

import javax.swing.JLabel;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.awt.event.ItemEvent;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import java.awt.Dimension;

public class AcquirePanel extends JPanel implements AcquisitionPanelInterface {
	
	private JTextField nameTextField;
	private JTextField pathTextField;
	private JTextField exposuresTextField;
	private JTextField roiHField;
	private JTextField roiX0Field;
	private JTextField roiWField;
	private JTextField roiY0Field;
	private JSpinner framesSpinner;
	private JCheckBox acquireSimultaneously;
	private JToggleButton btnStart;
	private Acquisition acq_;
	
	private Studio studio_;
	
	private JProgressBar acquisitionProgress;
	private final ButtonGroup saveModeButtonGroup = new ButtonGroup();
	private final ButtonGroup acquisitionButtonGroup = new ButtonGroup();
	private JRadioButton saveSingleImg;
	private JRadioButton saveStacks;

	/**
	 * Create the panel.
	 */
	public AcquirePanel(Studio studio) {
		studio_ = studio;
		
		// figure out camera name
		String camName = studio_.getCMMCore().getCameraDevice();
		
		// get date
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		Date date = new Date();
		
		setBorder(new TitledBorder(null, "Acquire", TitledBorder.LEFT, TitledBorder.TOP, null, null));
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] {30};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{1.0};
		gridBagLayout.rowWeights = new double[]{1.0, 1.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		JPanel pathPanel = new JPanel();
		GridBagConstraints gbc_pathPanel = new GridBagConstraints();
		gbc_pathPanel.insets = new Insets(0, 0, 5, 0);
		gbc_pathPanel.fill = GridBagConstraints.BOTH;
		gbc_pathPanel.gridx = 0;
		gbc_pathPanel.gridy = 0;
		add(pathPanel, gbc_pathPanel);
		GridBagLayout gbl_pathPanel = new GridBagLayout();
		gbl_pathPanel.columnWidths = new int[] {30, 0, 0};
		gbl_pathPanel.rowHeights = new int[] {30, 30};
		gbl_pathPanel.columnWeights = new double[]{0.0, 0.0, 1.0, 0.0};
		gbl_pathPanel.rowWeights = new double[]{0.0, 0.0};
		pathPanel.setLayout(gbl_pathPanel);
		
		JLabel nameLabel = new JLabel("Name:");
		GridBagConstraints gbc_nameLabel = new GridBagConstraints();
		gbc_nameLabel.weightx = 0.1;
		gbc_nameLabel.insets = new Insets(0, 0, 5, 5);
		gbc_nameLabel.gridx = 0;
		gbc_nameLabel.gridy = 0;
		pathPanel.add(nameLabel, gbc_nameLabel);
		
		if(camName != null && camName != "") {
			nameTextField = new JTextField(camName+"_"+dateFormat.format(date));
		} else {
			nameTextField = new JTextField();
		}
		
		GridBagConstraints gbc_nameTextField = new GridBagConstraints();
		gbc_nameTextField.gridwidth = 2;
		gbc_nameTextField.weightx = 1.0;
		gbc_nameTextField.insets = new Insets(0, 0, 5, 5);
		gbc_nameTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_nameTextField.gridx = 1;
		gbc_nameTextField.gridy = 0;
		pathPanel.add(nameTextField, gbc_nameTextField);
		nameTextField.setColumns(10);
		
		JLabel pathLabel = new JLabel("Path:");
		GridBagConstraints gbc_pathLabel = new GridBagConstraints();
		gbc_pathLabel.weightx = 0.1;
		gbc_pathLabel.insets = new Insets(0, 0, 5, 5);
		gbc_pathLabel.gridx = 0;
		gbc_pathLabel.gridy = 1;
		pathPanel.add(pathLabel, gbc_pathLabel);
		
		pathTextField = new JTextField();
		GridBagConstraints gbc_pathTextField = new GridBagConstraints();
		gbc_pathTextField.gridwidth = 2;
		gbc_pathTextField.weightx = 1.0;
		gbc_pathTextField.insets = new Insets(0, 0, 5, 5);
		gbc_pathTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_pathTextField.gridx = 1;
		gbc_pathTextField.gridy = 1;
		pathPanel.add(pathTextField, gbc_pathTextField);
		pathTextField.setColumns(10);
		
		JButton pathButton = new JButton("...");
		pathButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {    	
				showPathSelectionWindow();
			}
		});
		GridBagConstraints gbc_pathButton = new GridBagConstraints();
		gbc_pathButton.insets = new Insets(0, 0, 5, 0);
		gbc_pathButton.gridx = 3;
		gbc_pathButton.gridy = 1;
		pathPanel.add(pathButton, gbc_pathButton);
		
		JPanel acqPanel = new JPanel();
		GridBagConstraints gbc_acqPanel = new GridBagConstraints();
		gbc_acqPanel.insets = new Insets(0, 0, 5, 0);
		gbc_acqPanel.fill = GridBagConstraints.BOTH;
		gbc_acqPanel.gridx = 0;
		gbc_acqPanel.gridy = 1;
		add(acqPanel, gbc_acqPanel);
		GridBagLayout gbl_acqPanel = new GridBagLayout();
		gbl_acqPanel.columnWidths = new int[] {0, 0, 0, 0, 0, 0};
		gbl_acqPanel.rowHeights = new int[] {0, 0};
		gbl_acqPanel.columnWeights = new double[]{0.0, 1.0, 1.0, 1.0, 1.0, 0.0};
		gbl_acqPanel.rowWeights = new double[]{0.0, 1.0};
		acqPanel.setLayout(gbl_acqPanel);
		
		JLabel framesLabel = new JLabel("Frames:");
		GridBagConstraints gbc_framesLabel = new GridBagConstraints();
		gbc_framesLabel.insets = new Insets(0, 0, 5, 5);
		gbc_framesLabel.gridx = 0;
		gbc_framesLabel.gridy = 0;
		acqPanel.add(framesLabel, gbc_framesLabel);
		
		framesSpinner = new JSpinner();
		framesSpinner.setModel(new SpinnerNumberModel(new Integer(20000), new Integer(1), null, new Integer(1000)));
		GridBagConstraints gbc_framesSpinner = new GridBagConstraints();
		gbc_framesSpinner.fill = GridBagConstraints.HORIZONTAL;
		gbc_framesSpinner.gridwidth = 2;
		gbc_framesSpinner.insets = new Insets(0, 0, 5, 5);
		gbc_framesSpinner.gridx = 1;
		gbc_framesSpinner.gridy = 0;
		acqPanel.add(framesSpinner, gbc_framesSpinner);
		
		JLabel exposureLabel = new JLabel("Exposures:");
		GridBagConstraints gbc_exposureLabel = new GridBagConstraints();
		gbc_exposureLabel.anchor = GridBagConstraints.EAST;
		gbc_exposureLabel.insets = new Insets(0, 0, 5, 5);
		gbc_exposureLabel.gridx = 3;
		gbc_exposureLabel.gridy = 0;
		acqPanel.add(exposureLabel, gbc_exposureLabel);
		
		exposuresTextField = new JTextField();
		exposuresTextField.setText("10,500,1000");
		GridBagConstraints gbc_exposuresTextField = new GridBagConstraints();
		gbc_exposuresTextField.gridwidth = 2;
		gbc_exposuresTextField.insets = new Insets(0, 0, 5, 0);
		gbc_exposuresTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_exposuresTextField.gridx = 4;
		gbc_exposuresTextField.gridy = 0;
		acqPanel.add(exposuresTextField, gbc_exposuresTextField);
		exposuresTextField.setColumns(10);
		
		JLabel roiLabel = new JLabel("ROI:");
		GridBagConstraints gbc_roiLabel = new GridBagConstraints();
		gbc_roiLabel.anchor = GridBagConstraints.EAST;
		gbc_roiLabel.insets = new Insets(0, 0, 0, 5);
		gbc_roiLabel.gridx = 0;
		gbc_roiLabel.gridy = 1;
		acqPanel.add(roiLabel, gbc_roiLabel);
		
		JPanel roiPanel = new JPanel();
		GridBagConstraints gbc_roiPanel = new GridBagConstraints();
		gbc_roiPanel.gridwidth = 4;
		gbc_roiPanel.insets = new Insets(0, 0, 0, 5);
		gbc_roiPanel.fill = GridBagConstraints.BOTH;
		gbc_roiPanel.gridx = 1;
		gbc_roiPanel.gridy = 1;
		acqPanel.add(roiPanel, gbc_roiPanel);
		GridBagLayout gbl_roiPanel = new GridBagLayout();
		gbl_roiPanel.columnWidths = new int[]{0, 0, 0, 0, 0};
		gbl_roiPanel.rowHeights = new int[] {0};
		gbl_roiPanel.columnWeights = new double[]{1.0, 1.0, 1.0, 1.0, Double.MIN_VALUE};
		gbl_roiPanel.rowWeights = new double[]{0.0};
		roiPanel.setLayout(gbl_roiPanel);
		
		roiX0Field = new JTextField();
		GridBagConstraints gbc_roiX0Field = new GridBagConstraints();
		gbc_roiX0Field.fill = GridBagConstraints.HORIZONTAL;
		gbc_roiX0Field.insets = new Insets(0, 0, 0, 5);
		gbc_roiX0Field.gridx = 0;
		gbc_roiX0Field.gridy = 0;
		roiPanel.add(roiX0Field, gbc_roiX0Field);
		roiX0Field.setColumns(10);
		
		roiY0Field = new JTextField();
		GridBagConstraints gbc_roiY0Field = new GridBagConstraints();
		gbc_roiY0Field.fill = GridBagConstraints.HORIZONTAL;
		gbc_roiY0Field.insets = new Insets(0, 0, 0, 5);
		gbc_roiY0Field.gridx = 1;
		gbc_roiY0Field.gridy = 0;
		roiPanel.add(roiY0Field, gbc_roiY0Field);
		roiY0Field.setColumns(10);
		
		roiWField = new JTextField();
		GridBagConstraints gbc_roiWField = new GridBagConstraints();
		gbc_roiWField.fill = GridBagConstraints.HORIZONTAL;
		gbc_roiWField.insets = new Insets(0, 0, 0, 5);
		gbc_roiWField.gridx = 2;
		gbc_roiWField.gridy = 0;
		roiPanel.add(roiWField, gbc_roiWField);
		roiWField.setColumns(10);
		
		roiHField = new JTextField();
		roiHField.setPreferredSize(new Dimension(20, 6));
		GridBagConstraints gbc_roiHField = new GridBagConstraints();
		gbc_roiHField.fill = GridBagConstraints.HORIZONTAL;
		gbc_roiHField.gridx = 3;
		gbc_roiHField.gridy = 0;
		roiPanel.add(roiHField, gbc_roiHField);
		roiHField.setColumns(10);
		
		JButton getroiButton = new JButton("Get ROI");
		getroiButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				grabRoi();
			}
		});
		GridBagConstraints gbc_getroiButton = new GridBagConstraints();
		gbc_getroiButton.gridx = 5;
		gbc_getroiButton.gridy = 1;
		acqPanel.add(getroiButton, gbc_getroiButton);
		
		JPanel optionsPanel = new JPanel();
		GridBagConstraints gbc_optionsPanel = new GridBagConstraints();
		gbc_optionsPanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_optionsPanel.gridx = 0;
		gbc_optionsPanel.gridy = 2;
		add(optionsPanel, gbc_optionsPanel);
		GridBagLayout gbl_optionsPanel = new GridBagLayout();
		gbl_optionsPanel.rowHeights = new int[] {30, 30, 30};
		gbl_optionsPanel.columnWidths = new int[] {30, 30, 30};
		gbl_optionsPanel.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
		gbl_optionsPanel.rowWeights = new double[]{0.0, 0.0, 0.0};
		optionsPanel.setLayout(gbl_optionsPanel);
		
		btnStart = new JToggleButton("Start");
		btnStart.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent state) {
				if (state.getStateChange() == ItemEvent.SELECTED) {
					runAcquisition();
				} else {
					stopAcquisition();
				}
			}
		});
		
		acquireSimultaneously = new JCheckBox("Acquire simultaneously");
		acquireSimultaneously.setSelected(true);
		acquisitionButtonGroup.add(acquireSimultaneously);
		GridBagConstraints gbc_acquireSimultaneously = new GridBagConstraints();
		gbc_acquireSimultaneously.anchor = GridBagConstraints.WEST;
		gbc_acquireSimultaneously.insets = new Insets(0, 0, 5, 5);
		gbc_acquireSimultaneously.gridx = 0;
		gbc_acquireSimultaneously.gridy = 0;
		optionsPanel.add(acquireSimultaneously, gbc_acquireSimultaneously);
		
		saveStacks = new JRadioButton("Save tiff-stacks");
		saveStacks.setSelected(true);
		saveModeButtonGroup.add(saveStacks);
		GridBagConstraints saveTiffStack = new GridBagConstraints();
		saveTiffStack.fill = GridBagConstraints.HORIZONTAL;
		saveTiffStack.insets = new Insets(0, 0, 5, 5);
		saveTiffStack.gridx = 1;
		saveTiffStack.gridy = 0;
		optionsPanel.add(saveStacks, saveTiffStack);
		GridBagConstraints gbc_btnStart = new GridBagConstraints();
		gbc_btnStart.gridheight = 3;
		gbc_btnStart.fill = GridBagConstraints.BOTH;
		gbc_btnStart.gridwidth = 4;
		gbc_btnStart.gridx = 2;
		gbc_btnStart.gridy = 0;
		optionsPanel.add(btnStart, gbc_btnStart);
		
		JCheckBox acquireSequentially = new JCheckBox("Acquire sequentially");
		acquisitionButtonGroup.add(acquireSequentially);
		GridBagConstraints gbc_acquireSequentially = new GridBagConstraints();
		gbc_acquireSequentially.fill = GridBagConstraints.HORIZONTAL;
		gbc_acquireSequentially.insets = new Insets(0, 0, 5, 5);
		gbc_acquireSequentially.gridx = 0;
		gbc_acquireSequentially.gridy = 1;
		optionsPanel.add(acquireSequentially, gbc_acquireSequentially);
		
		saveSingleImg = new JRadioButton("Save single-tiffs");
		saveModeButtonGroup.add(saveSingleImg);
		GridBagConstraints saveSingleTiffs = new GridBagConstraints();
		saveSingleTiffs.fill = GridBagConstraints.HORIZONTAL;
		saveSingleTiffs.insets = new Insets(0, 0, 5, 5);
		saveSingleTiffs.gridx = 1;
		saveSingleTiffs.gridy = 1;
		optionsPanel.add(saveSingleImg, saveSingleTiffs);
		
		acquisitionProgress = new JProgressBar();
		GridBagConstraints gbc_acquisitionProgress = new GridBagConstraints();
		gbc_acquisitionProgress.fill = GridBagConstraints.HORIZONTAL;
		gbc_acquisitionProgress.gridwidth = 2;
		gbc_acquisitionProgress.insets = new Insets(0, 0, 0, 5);
		gbc_acquisitionProgress.gridx = 0;
		gbc_acquisitionProgress.gridy = 2;
		optionsPanel.add(acquisitionProgress, gbc_acquisitionProgress);
	}

	protected void grabRoi() {
	      final RoiManager roiManager = RoiManager.getInstance();
	      if(roiManager.getCount() > 0) {
	    	  Roi roi = roiManager.getRoi(0);
	    	  roiX0Field.setText(String.valueOf((int) roi.getXBase()));
	    	  roiY0Field.setText(String.valueOf((int) roi.getYBase()));
	    	  roiWField.setText(String.valueOf((int) roi.getFloatWidth()));
	    	  roiHField.setText(String.valueOf((int) roi.getFloatHeight()));
	      }
	}

	protected void showPathSelectionWindow() {
		JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(new java.io.File("."));
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = fc.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File folder = fc.getSelectedFile();
			pathTextField.setText(folder.getAbsolutePath());
		}
	}

	protected String getAcqName() {
		return nameTextField.getText();
	}
	
	protected String getAcqPath() {
		return pathTextField.getText();
	}
	
	protected int getFrames() {
		return (int) framesSpinner.getValue();
	}
	
	protected Integer[] getExposures() {
		String[] exp = exposuresTextField.getText().split(",");
		
		ArrayList<Integer> list = new ArrayList<Integer>();
		for(int i=0;i<exp.length;i++) {
			if(utils.isInteger(exp[i])) {
				list.add(Integer.valueOf(exp[i]));
			}
		}
		return list.toArray(new Integer[0]);
	}
	
	protected int getRoiX0() {
		String s = roiX0Field.getText();
		if(utils.isInteger(s)) {
			return Integer.parseInt(s);
		}
		return -1;
	}
	
	protected int getRoiY0() {
		String s = roiY0Field.getText();
		if(utils.isInteger(s)) {
			return Integer.parseInt(s);
		}
		return -1;
	}
	
	protected int getRoiW() {
		String s = roiWField.getText();
		if(utils.isInteger(s)) {
			return Integer.parseInt(s);
		}
		return -1;
	}
	
	protected int getRoiH() {
		String s = roiHField.getText();
		if(utils.isInteger(s)) {
			return Integer.parseInt(s);
		}
		return -1;
	}
	
	private Roi getRoi() {
		int x,y,w,h;
		x = getRoiX0();
		y = getRoiY0();
		w = getRoiW();
		h = getRoiH();
		
		if(x == -1 || y == -1 || h ==-1 || w == -1) {
			return null;
		}
		
		return new Roi(x,y,w,h);
	}
	
	protected boolean acquireSimultaneoulsy() {
		return acquireSimultaneously.isSelected();
	}
	
	protected boolean acquireStacks() {
		return saveStacks.isSelected();
	}
		
	private void runAcquisition() {
		
		AcquisitionSettings settings  = new AcquisitionSettings();
		
		settings.name_ = getAcqName();
		settings.path_ = getAcqPath();
		settings.numFrames_ = getFrames();
		settings.exposures_ = getExposures();
		settings.roi_ = getRoi();
		
		if(acquireStacks()) {
			settings.saveMode_ = Datastore.SaveMode.MULTIPAGE_TIFF;
		} else {
			settings.saveMode_ = Datastore.SaveMode.SINGLEPLANE_TIFF_SERIES;
		}
		
		settings.simultaneousAcq = acquireSimultaneoulsy();
		
		acq_ = AcquisitionFactory.getFactory().getAcquisition(studio_, settings, this);
				
		if(settings.path_ != null || !settings.path_.equals("")) {
			acq_.start();
		}
	}
	
	private void stopAcquisition() {
		if(acq_ != null) {
			acq_.stop();
		}
	}
	
	public boolean isRunning() {
		return acq_.isRunning();
	}

	protected JRadioButton getRdbtnMultistacks() {
		return saveSingleImg;
	}
	protected JRadioButton getSaveModeRadioButton() {
		return saveStacks;
	}

	@Override
	public void setProgress(int progress) {
		System.out.println("Progress is "+progress);
		if(progress>=0 && progress<=100) {
			acquisitionProgress.setValue(progress);
		}
	}

	@Override
	public void acqHasStarted() {
		acquisitionProgress.setValue(0);
		btnStart.setText("Stop");
	}

	@Override
	public void acqHasStopped() {
		System.out.println("Acq has stopped");
		btnStart.setText("Start");
		btnStart.setSelected(false);
	}

	@Override
	public void acqHasEnded() {
		System.out.println("Acq has ended");
		acquisitionProgress.setValue(100);
		btnStart.setText("Start");	
		btnStart.setSelected(false);	
	}
}
