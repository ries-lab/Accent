package de.embl.rieslab.accent.fiji.ui;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import java.awt.GridBagLayout;
import javax.swing.JTable;
import javax.swing.JToggleButton;

import java.awt.GridBagConstraints;
import javax.swing.table.DefaultTableModel;

import de.embl.rieslab.accent.common.interfaces.pipeline.PipelineController;
import de.embl.rieslab.accent.common.interfaces.ui.ProcessingPanelInterface;
import de.embl.rieslab.accent.common.utils.AccentUtils;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.border.LineBorder;
import java.awt.Color;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import java.awt.Dimension;
import java.awt.Font;

public class TableProcPanel extends JPanel implements ProcessingPanelInterface  {
	
	private JTable table;
	private JToggleButton btnProcess;
	private de.embl.rieslab.accent.common.interfaces.pipeline.PipelineController controller;
	private JProgressBar progressBar;
	
	private final static String START = "Process";
	private final static String STOP = "Stop";
	private JLabel lblPath;
	private JTextField textField;
	private JButton button;
	private JLabel lblNewLabel;
	
	private List<String> datasets;

	/**
	 * Create the panel.
	 * @param dataservice 
	 * @param controller 
	 */
	public TableProcPanel(PipelineController controller, List<String> datasets) {
		
		if(datasets == null) {
			throw new NullPointerException();
		}
		
		
		this.controller = controller;
		this.datasets = datasets;
		
		setBorder(new TitledBorder(null, "Process", TitledBorder.LEFT, TitledBorder.TOP, null, null));
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 1.0, 1.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		JLabel lblLeaveTheExposure = new JLabel("Leave the exposure field blank to ignore it");
		GridBagConstraints gbc_lblLeaveTheExposure = new GridBagConstraints();
		gbc_lblLeaveTheExposure.gridwidth = 2;
		gbc_lblLeaveTheExposure.anchor = GridBagConstraints.WEST;
		gbc_lblLeaveTheExposure.insets = new Insets(0, 0, 5, 5);
		gbc_lblLeaveTheExposure.gridx = 0;
		gbc_lblLeaveTheExposure.gridy = 0;
		add(lblLeaveTheExposure, gbc_lblLeaveTheExposure);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setMaximumSize(new Dimension(32767, 50));
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.gridheight = 2;
		gbc_scrollPane.gridwidth = 3;
		gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 1;
		add(scrollPane, gbc_scrollPane);

		
		table = new JTable();
		scrollPane.setViewportView(table);
		table.setBorder(new LineBorder(new Color(0, 0, 0)));
		table.setModel(new DefaultTableModel(buildList(), new String[] { "Dataset", "Exposure (ms)" }) { // expects an array, not a map

			boolean[] columnEditables = new boolean[] { false, true };

			public boolean isCellEditable(int row, int column) {
				return columnEditables[column];
			}
		});
		table.getColumnModel().getColumn(0).setPreferredWidth(200);
		table.getColumnModel().getColumn(1).setPreferredWidth(80);
		
		lblPath = new JLabel("Path:");
		GridBagConstraints gbc_lblPath = new GridBagConstraints();
		gbc_lblPath.anchor = GridBagConstraints.EAST;
		gbc_lblPath.insets = new Insets(0, 0, 5, 5);
		gbc_lblPath.gridx = 0;
		gbc_lblPath.gridy = 3;
		add(lblPath, gbc_lblPath);
		
		textField = new JTextField();
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.insets = new Insets(0, 0, 5, 5);
		gbc_textField.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField.gridx = 1;
		gbc_textField.gridy = 3;
		add(textField, gbc_textField);
		textField.setColumns(10);
		
		button = new JButton("...");
		GridBagConstraints gbc_button = new GridBagConstraints();
		gbc_button.insets = new Insets(0, 0, 5, 0);
		gbc_button.gridx = 2;
		gbc_button.gridy = 3;
		add(button, gbc_button);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {    	
				showPathSelectionWindow();
			}
		});
		
		lblNewLabel = new JLabel(" ");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.gridx = 1;
		gbc_lblNewLabel.gridy = 4;
		add(lblNewLabel, gbc_lblNewLabel);
		
		btnProcess = new JToggleButton(START);
		GridBagConstraints gbc_btnProcess = new GridBagConstraints();
		gbc_btnProcess.fill = GridBagConstraints.VERTICAL;
		gbc_btnProcess.gridheight = 2;
		gbc_btnProcess.insets = new Insets(5, 0, 0, 0);
		gbc_btnProcess.gridx = 2;
		gbc_btnProcess.gridy = 4;
		add(btnProcess, gbc_btnProcess);
		
		progressBar = new JProgressBar();
		GridBagConstraints gbc_progressBar = new GridBagConstraints();
		gbc_progressBar.insets = new Insets(0, 5, 5, 5);
		gbc_progressBar.gridwidth = 2;
		gbc_progressBar.fill = GridBagConstraints.HORIZONTAL;
		gbc_progressBar.gridx = 0;
		gbc_progressBar.gridy = 5;
		add(progressBar, gbc_progressBar);
		btnProcess.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				AbstractButton abstractButton = (AbstractButton) actionEvent.getSource();
				boolean selected = abstractButton.getModel().isSelected();
				if (selected) {
					startProcessing();
				} else {
					stopProcessing();
				}
			}
		});
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
	
	protected void stopProcessing() {
		controller.stopProcessor();
	}

	protected void startProcessing() {
		if(!controller.isProcessingRunning()) { // avoid trigger from setSelected(true) in processingHasStarted()
			String s  = textField.getText();
			HashMap<String, Double> list = extractDatasets();
			boolean b = controller.startProcessor(s, list);
			if(!b) {
				btnProcess.setText(START);
				btnProcess.setSelected(false);
			}
		} else {
			btnProcess.setText(STOP);
			btnProcess.setSelected(false);
		}
	}
	
	protected HashMap<String, Double> extractDatasets(){ 
		HashMap<String, Double> map = new HashMap<String, Double>();
		int n = table.getRowCount();
		
		for(int i=0;i<n;i++) {
			String ms = (String) table.getValueAt(i, 1);
			if(!ms.equals("") && AccentUtils.isNumeric(ms)) {
				Double exposure = Double.parseDouble(ms);
				
				map.put(datasets.get(i),exposure);
			}
		}
		
		return map;
	}

	@Override
	public void setDataPath(String path) {
		// Do nothing
	}

	@Override
	public void setProgress(String progress, int percentage) {
		lblNewLabel.setText(progress);
		progressBar.setValue(percentage);
	}


	@Override
	public void processingHasStarted() {
		progressBar.setValue(0);
		btnProcess.setText(STOP);
		if(!btnProcess.isSelected()) {
			btnProcess.setSelected(true);
		}
	}


	@Override
	public void processingHasStopped() {
		btnProcess.setText(START);
		btnProcess.setSelected(false);
	}


	@Override
	public void processingHasEnded() {
		progressBar.setValue(100);
		btnProcess.setText(START);
		btnProcess.setSelected(false);
	}


	private String[][] buildList(){
		Map<String,Double> map = new LinkedHashMap<String,Double>();
		
		for(int i=0;i<datasets.size();i++) {
			// check if name contains "ms"
			double expo = AccentUtils.extractExposureMs(datasets.get(i));
			if(Double.compare(expo, 0) != 0)
				map.put(datasets.get(i), expo);
		}
		
		String[][] vals = new String[map.size()][2];
		int i=0;
		for(Entry<String, Double> e: map.entrySet()) {
			vals[i][0] = e.getKey();
			vals[i][1] = String.valueOf(e.getValue());
			i++;
		}
		
		return vals;
	}
}
