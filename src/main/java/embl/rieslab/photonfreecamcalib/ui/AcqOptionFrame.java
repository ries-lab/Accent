package main.java.embl.rieslab.photonfreecamcalib.ui;


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
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.border.TitledBorder;

import main.java.embl.rieslab.photonfreecamcalib.acquisition.AcquisitionPanelInterface;

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
	private JComboBox<String> acqCombo;
	private JComboBox<String> saveCombo;
	private AcquisitionPanelInterface owner;

	private final static String ACQ_ALT = "alternately";
	private final static String ACQ_SEQ = "sequentially";
	private final static String SAV_STACK = "tiff stacks";
	private final static String SAV_SINGLE = "single tiffs";
	private final static String PROC_PAR = "in parallel";
	private final static String PROC_SEP = "separately";
	
	/**
	 * Create the frame.
	 */
	public AcqOptionFrame(AcquisitionPanelInterface owner) {
		this.owner = owner;
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 158, 257);
		contentPane = new JPanel();
		contentPane.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Options",
				TitledBorder.CENTER, TitledBorder.TOP, null, new Color(0, 0, 0)));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[] { 0, 0 };
		gbl_contentPane.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_contentPane.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_contentPane.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				Double.MIN_VALUE };
		contentPane.setLayout(gbl_contentPane);

		Component horizontalStrut = Box.createHorizontalStrut(20);
		GridBagConstraints gbc_horizontalStrut = new GridBagConstraints();
		gbc_horizontalStrut.insets = new Insets(0, 0, 5, 0);
		gbc_horizontalStrut.gridx = 0;
		gbc_horizontalStrut.gridy = 0;
		contentPane.add(horizontalStrut, gbc_horizontalStrut);

		JLabel lblNewLabel = new JLabel("Acquire exposures...");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
		GridBagConstraints acqlabel = new GridBagConstraints();
		acqlabel.anchor = GridBagConstraints.WEST;
		acqlabel.insets = new Insets(0, 0, 5, 0);
		acqlabel.gridx = 0;
		acqlabel.gridy = 1;
		contentPane.add(lblNewLabel, acqlabel);

		acqCombo = new JComboBox<String>();
		acqCombo.setModel(new DefaultComboBoxModel<String>(new String[] { ACQ_ALT, ACQ_SEQ }));
		GridBagConstraints gbc_acqCombo = new GridBagConstraints();
		gbc_acqCombo.insets = new Insets(0, 8, 5, 8);
		gbc_acqCombo.fill = GridBagConstraints.HORIZONTAL;
		gbc_acqCombo.gridx = 0;
		gbc_acqCombo.gridy = 2;
		contentPane.add(acqCombo, gbc_acqCombo);

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

		JSeparator separator_2 = new JSeparator();
		separator_2.setForeground(Color.GRAY);
		separator_2.setBackground(Color.LIGHT_GRAY);
		GridBagConstraints gbc_separator_2 = new GridBagConstraints();
		gbc_separator_2.insets = new Insets(0, 0, 5, 0);
		gbc_separator_2.gridx = 0;
		gbc_separator_2.gridy = 9;
		contentPane.add(separator_2, gbc_separator_2);

		JButton btnSave = new JButton("Save");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				notifyOwner();
			}
		});
		GridBagConstraints gbc_btnSave = new GridBagConstraints();
		gbc_btnSave.gridx = 0;
		gbc_btnSave.gridy = 10;
		contentPane.add(btnSave, gbc_btnSave);
	}

	public void notifyOwner() {
		boolean alternatedAcquisition, saveAsStacks, parallelProcessing;
		
		if(((String) acqCombo.getSelectedItem()).equals(ACQ_ALT)) {
			alternatedAcquisition = true;
		} else {
			alternatedAcquisition = false;
		}
		
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
		
		owner.setAdvancedSettings(alternatedAcquisition, saveAsStacks, parallelProcessing);
		this.setVisible(false);
		this.dispose();
	}
}
