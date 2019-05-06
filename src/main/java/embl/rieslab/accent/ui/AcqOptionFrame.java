package main.java.embl.rieslab.accent.ui;


import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import java.awt.Font;
import javax.swing.JComboBox;
import java.awt.Insets;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import ij.gui.Roi;
import ij.plugin.frame.RoiManager;
import main.java.embl.rieslab.accent.data.SimpleRoi;
import main.java.embl.rieslab.accent.ui.interfaces.AcquisitionPanelInterface;
import main.java.embl.rieslab.accent.utils.utils;

import javax.swing.UIManager;
import java.awt.Component;
import javax.swing.Box;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

@SuppressWarnings("serial")
public class AcqOptionFrame extends JFrame {

	private JPanel contentPane;
	private JComboBox<String> processCombo;
	private JComboBox<String> saveCombo;

	private JTextField x0Field;
	private JTextField y0Field;
	private JTextField widthField;
	private JTextField heightField;
	
	private JSpinner prerunsSpinner;
	
	private AcquisitionPanelInterface owner;

	private final static String SAV_STACK = "tiff stacks";
	private final static String SAV_SINGLE = "single tiffs";
	private final static String PROC_PAR = "in parallel";
	private final static String PROC_SEP = "separately";
	
	
	/**
	 * Create the frame.
	 */
	public AcqOptionFrame(AcquisitionPanelInterface owner, int preRun, boolean multiStacks, boolean parallelProcessing, SimpleRoi roi) {
		
		this.owner = owner;
		setUpFrame();

		prerunsSpinner.setValue(preRun);
		
		if(!multiStacks) {
			saveCombo.setSelectedIndex(1);
		}

		if(!parallelProcessing) {
			processCombo.setSelectedIndex(1);
		}
		
		if(roi != null) {
			x0Field.setText(String.valueOf(roi.x0));
			y0Field.setText(String.valueOf(roi.y0));
			widthField.setText(String.valueOf(roi.height));
			heightField.setText(String.valueOf(roi.width));
		}
	}
	
	private void setUpFrame() {
		
		setBounds(100, 100, 158, 447);
		contentPane = new JPanel();
		contentPane.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Options", TitledBorder.CENTER, TitledBorder.TOP, null, new Color(0, 0, 0)));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{0, 0};
		gbl_contentPane.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_contentPane.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);

		Component horizontalStrut = Box.createHorizontalStrut(20);
		GridBagConstraints gbc_horizontalStrut = new GridBagConstraints();
		gbc_horizontalStrut.insets = new Insets(0, 0, 5, 0);
		gbc_horizontalStrut.gridx = 0;
		gbc_horizontalStrut.gridy = 0;
		contentPane.add(horizontalStrut, gbc_horizontalStrut);
		
		JLabel preWarmLabel = new JLabel("Pre-run (min)...");
		preWarmLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
		GridBagConstraints gbc_preWarmLabel = new GridBagConstraints();
		gbc_preWarmLabel.anchor = GridBagConstraints.WEST;
		gbc_preWarmLabel.insets = new Insets(0, 0, 5, 0);
		gbc_preWarmLabel.gridx = 0;
		gbc_preWarmLabel.gridy = 1;
		contentPane.add(preWarmLabel, gbc_preWarmLabel);
		
		prerunsSpinner = new JSpinner();
		prerunsSpinner.setModel(new SpinnerNumberModel(0, null, 500, 1));
		GridBagConstraints gbc_spinner = new GridBagConstraints();
		gbc_spinner.fill = GridBagConstraints.HORIZONTAL;
		gbc_spinner.insets = new Insets(0, 20, 5, 20);
		gbc_spinner.gridx = 0;
		gbc_spinner.gridy = 2;
		contentPane.add(prerunsSpinner, gbc_spinner);


		JSeparator separator = new JSeparator();
		separator.setBackground(Color.LIGHT_GRAY);
		separator.setPreferredSize(new Dimension(120, 2));
		separator.setForeground(Color.GRAY);
		GridBagConstraints gbc_separator = new GridBagConstraints();
		gbc_separator.insets = new Insets(0, 0, 5, 0);
		gbc_separator.gridx = 0;
		gbc_separator.gridy = 3;
		contentPane.add(separator, gbc_separator);

		JLabel saveLabel = new JLabel("Save frames as ...");
		saveLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
		GridBagConstraints gbc_saveLabel = new GridBagConstraints();
		gbc_saveLabel.insets = new Insets(0, 0, 5, 0);
		gbc_saveLabel.anchor = GridBagConstraints.WEST;
		gbc_saveLabel.gridx = 0;
		gbc_saveLabel.gridy = 4;
		contentPane.add(saveLabel, gbc_saveLabel);

		saveCombo = new JComboBox<String>();
		saveCombo.setModel(new DefaultComboBoxModel<String>(new String[] { SAV_STACK, SAV_SINGLE }));
		GridBagConstraints gbc_saveCombo = new GridBagConstraints();
		gbc_saveCombo.insets = new Insets(0, 8, 5, 8);
		gbc_saveCombo.fill = GridBagConstraints.HORIZONTAL;
		gbc_saveCombo.gridx = 0;
		gbc_saveCombo.gridy = 5;
		contentPane.add(saveCombo, gbc_saveCombo);

		JSeparator separator_1 = new JSeparator();
		separator_1.setPreferredSize(new Dimension(120, 2));
		separator_1.setForeground(Color.GRAY);
		separator_1.setBackground(Color.LIGHT_GRAY);
		GridBagConstraints gbc_separator_1 = new GridBagConstraints();
		gbc_separator_1.insets = new Insets(0, 0, 5, 0);
		gbc_separator_1.gridx = 0;
		gbc_separator_1.gridy = 6;
		contentPane.add(separator_1, gbc_separator_1);

		JLabel processLabel = new JLabel("Process data ...");
		processLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
		GridBagConstraints gbc_processLabel = new GridBagConstraints();
		gbc_processLabel.insets = new Insets(0, 0, 5, 0);
		gbc_processLabel.anchor = GridBagConstraints.WEST;
		gbc_processLabel.gridx = 0;
		gbc_processLabel.gridy = 7;
		contentPane.add(processLabel, gbc_processLabel);

		processCombo = new JComboBox<String>();
		processCombo.setModel(new DefaultComboBoxModel<String>(new String[] { PROC_PAR, PROC_SEP }));
		GridBagConstraints gbc_processCombo = new GridBagConstraints();
		gbc_processCombo.insets = new Insets(0, 8, 5, 8);
		gbc_processCombo.fill = GridBagConstraints.HORIZONTAL;
		gbc_processCombo.gridx = 0;
		gbc_processCombo.gridy = 8;
		contentPane.add(processCombo, gbc_processCombo);
		
		JSeparator separator_3 = new JSeparator();
		separator_3.setPreferredSize(new Dimension(120, 2));
		separator_3.setForeground(Color.GRAY);
		separator_3.setBackground(Color.LIGHT_GRAY);
		GridBagConstraints gbc_separator_3 = new GridBagConstraints();
		gbc_separator_3.insets = new Insets(0, 0, 5, 0);
		gbc_separator_3.gridx = 0;
		gbc_separator_3.gridy = 9;
		contentPane.add(separator_3, gbc_separator_3);
		
		JLabel roiLabel = new JLabel("Roi");
		roiLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
		GridBagConstraints gbc_roiLabel = new GridBagConstraints();
		gbc_roiLabel.anchor = GridBagConstraints.SOUTHWEST;
		gbc_roiLabel.insets = new Insets(0, 0, 5, 0);
		gbc_roiLabel.gridx = 0;
		gbc_roiLabel.gridy = 10;
		contentPane.add(roiLabel, gbc_roiLabel);
		
		JPanel roiPanel = new JPanel();
		roiPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		GridBagConstraints gbc_roiPanel = new GridBagConstraints();
		gbc_roiPanel.insets = new Insets(0, 0, 5, 0);
		gbc_roiPanel.fill = GridBagConstraints.BOTH;
		gbc_roiPanel.gridx = 0;
		gbc_roiPanel.gridy = 11;
		contentPane.add(roiPanel, gbc_roiPanel);
		GridBagLayout gbl_roiPanel = new GridBagLayout();
		gbl_roiPanel.columnWidths = new int[]{0, 0, 0};
		gbl_roiPanel.rowHeights = new int[]{0, 0, 0, 0, 0, 0};
		gbl_roiPanel.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gbl_roiPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		roiPanel.setLayout(gbl_roiPanel);
		
		JButton btnGetRoi = new JButton("Get Roi");
		btnGetRoi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				grabRoi();
			}
		});
		
		GridBagConstraints gbc_btnGetRoi = new GridBagConstraints();
		gbc_btnGetRoi.insets = new Insets(0, 0, 5, 0);
		gbc_btnGetRoi.gridx = 1;
		gbc_btnGetRoi.gridy = 0;
		roiPanel.add(btnGetRoi, gbc_btnGetRoi);
		
		JLabel lblX = new JLabel("X0 anchor");
		GridBagConstraints gbc_lblX = new GridBagConstraints();
		gbc_lblX.insets = new Insets(0, 5, 5, 5);
		gbc_lblX.gridx = 0;
		gbc_lblX.gridy = 1;
		roiPanel.add(lblX, gbc_lblX);
		
		x0Field = new JTextField();
		GridBagConstraints gbc_x0Field = new GridBagConstraints();
		gbc_x0Field.insets = new Insets(0, 0, 5, 0);
		gbc_x0Field.fill = GridBagConstraints.HORIZONTAL;
		gbc_x0Field.gridx = 1;
		gbc_x0Field.gridy = 1;
		roiPanel.add(x0Field, gbc_x0Field);
		x0Field.setColumns(10);
		
		JLabel lblY = new JLabel("Y0 anchor");
		GridBagConstraints gbc_lblY = new GridBagConstraints();
		gbc_lblY.insets = new Insets(0, 5, 5, 5);
		gbc_lblY.gridx = 0;
		gbc_lblY.gridy = 2;
		roiPanel.add(lblY, gbc_lblY);
		
		y0Field = new JTextField();
		y0Field.setColumns(10);
		GridBagConstraints gbc_y0Field = new GridBagConstraints();
		gbc_y0Field.insets = new Insets(0, 0, 5, 0);
		gbc_y0Field.fill = GridBagConstraints.HORIZONTAL;
		gbc_y0Field.gridx = 1;
		gbc_y0Field.gridy = 2;
		roiPanel.add(y0Field, gbc_y0Field);
		
		JLabel lblWidth = new JLabel("Width");
		GridBagConstraints gbc_lblWidth = new GridBagConstraints();
		gbc_lblWidth.insets = new Insets(0, 5, 5, 5);
		gbc_lblWidth.gridx = 0;
		gbc_lblWidth.gridy = 3;
		roiPanel.add(lblWidth, gbc_lblWidth);
		
		widthField = new JTextField();
		widthField.setColumns(10);
		GridBagConstraints gbc_widthField = new GridBagConstraints();
		gbc_widthField.insets = new Insets(0, 0, 5, 0);
		gbc_widthField.fill = GridBagConstraints.HORIZONTAL;
		gbc_widthField.gridx = 1;
		gbc_widthField.gridy = 3;
		roiPanel.add(widthField, gbc_widthField);
		
		JLabel lblHeight = new JLabel("Height");
		GridBagConstraints gbc_lblHeight = new GridBagConstraints();
		gbc_lblHeight.insets = new Insets(0, 5, 0, 5);
		gbc_lblHeight.gridx = 0;
		gbc_lblHeight.gridy = 4;
		roiPanel.add(lblHeight, gbc_lblHeight);
		
		heightField = new JTextField();
		heightField.setColumns(10);
		GridBagConstraints gbc_heightField = new GridBagConstraints();
		gbc_heightField.fill = GridBagConstraints.HORIZONTAL;
		gbc_heightField.gridx = 1;
		gbc_heightField.gridy = 4;
		roiPanel.add(heightField, gbc_heightField);
		
		x0Field.setBackground(Color.WHITE);
		y0Field.setBackground(Color.WHITE);
		widthField.setBackground(Color.WHITE);
		heightField.setBackground(Color.WHITE);
		
		JSeparator separator_2 = new JSeparator();
		separator_2.setForeground(Color.GRAY);
		separator_2.setBackground(Color.LIGHT_GRAY);
		GridBagConstraints gbc_separator_2 = new GridBagConstraints();
		gbc_separator_2.insets = new Insets(0, 0, 5, 0);
		gbc_separator_2.gridx = 0;
		gbc_separator_2.gridy = 12;
		contentPane.add(separator_2, gbc_separator_2);
		
		JButton btnSave = new JButton("Save");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				notifyOwner();
			}
		});
		GridBagConstraints gbc_btnSave = new GridBagConstraints();
		gbc_btnSave.gridx = 0;
		gbc_btnSave.gridy = 13;
		contentPane.add(btnSave, gbc_btnSave);
	}
	
	
	protected int getRoiX0() {
		String s = x0Field.getText();
		if(utils.isInteger(s)) {
			return Integer.parseInt(s);
		}
		return -1;
	}
	
	protected int getRoiY0() {
		String s = y0Field.getText();
		if(utils.isInteger(s)) {
			return Integer.parseInt(s);
		}
		return -1;
	}
	
	protected int getRoiW() {
		String s = widthField.getText();
		if(utils.isInteger(s)) {
			return Integer.parseInt(s);
		}
		return -1;
	}
	
	protected int getRoiH() {
		String s = heightField.getText();
		if(utils.isInteger(s)) {
			return Integer.parseInt(s);
		}
		return -1;
	}
	
	private SimpleRoi getRoi() {
		int x,y,w,h;
		x = getRoiX0();
		y = getRoiY0();
		w = getRoiW();
		h = getRoiH();
		
		if(x == -1 || y == -1 || h ==-1 || w == -1) {
			return null;
		}
		
		return new SimpleRoi(x,y,w,h);
	}	
	
	protected void grabRoi() {
	      final RoiManager roiManager = RoiManager.getInstance();
	      if(roiManager != null && roiManager.getCount() > 0) {
	    	  Roi roi = roiManager.getRoi(0);
	    	  x0Field.setText(String.valueOf((int) roi.getXBase()));
	    	  y0Field.setText(String.valueOf((int) roi.getYBase()));
	    	  widthField.setText(String.valueOf((int) roi.getFloatWidth()));
	    	  heightField.setText(String.valueOf((int) roi.getFloatHeight()));
	      }
	}

	public void notifyOwner() {
		boolean saveAsStacks, parallelProcessing;
		
		if(((String) saveCombo.getSelectedItem()).equals(SAV_STACK)) {
			saveAsStacks = true;
		} else {
			saveAsStacks = false;
		}
		
		if(((String) processCombo.getSelectedItem()).equals(PROC_PAR)) {
			parallelProcessing = true;
		} else {
			parallelProcessing = false;
		}
		
		SimpleRoi roi = getRoi();
		
		owner.setAdvancedSettings((int) prerunsSpinner.getValue(), saveAsStacks, parallelProcessing, roi);
		this.setVisible(false);
		this.dispose();
	}
}
