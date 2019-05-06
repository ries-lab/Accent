package main.java.embl.rieslab.accent.ui;

import javax.swing.JPanel;

import main.java.embl.rieslab.accent.PipelineController;
import main.java.embl.rieslab.accent.data.DatasetExposurePair;
import main.java.embl.rieslab.accent.ui.interfaces.ProcessingPanelInterface;
import main.java.embl.rieslab.accent.utils.utils;
import net.imagej.Dataset;
import net.imagej.DatasetService;

import javax.swing.border.TitledBorder;
import java.awt.GridBagLayout;
import javax.swing.JTable;
import javax.swing.JToggleButton;

import java.awt.GridBagConstraints;
import javax.swing.table.DefaultTableModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.border.LineBorder;
import java.awt.Color;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import java.awt.Dimension;

public class TableProcPanel extends JPanel implements ProcessingPanelInterface  {


	private static final long serialVersionUID = 7399274328796332915L;
	
	private JTable table;
	private JToggleButton btnProcess;
	private PipelineController controller;
	private JProgressBar progressBar;
	private DatasetService dataservice;
	
	private final static String START = "Process";
	private final static String STOP = "Stop";
	private JLabel lblPath;
	private JTextField textField;
	private JButton button;

	/**
	 * Create the panel.
	 * @param dataservice 
	 * @param controller 
	 */
	public TableProcPanel(PipelineController controller, DatasetService dataservice) {
		this.controller = controller;
		this.dataservice = dataservice;
		
		setBorder(new TitledBorder(null, "Process", TitledBorder.LEFT, TitledBorder.TOP, null, null));
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 1.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
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
		table.setModel(new DefaultTableModel(
				buildList(),
			new String[] {
				"Dataset", "Exposure (ms)"
			}
		) {

			private static final long serialVersionUID = -8002387977444423007L;
			
			boolean[] columnEditables = new boolean[] {
				false, true
			};
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
		
		progressBar = new JProgressBar();
		GridBagConstraints gbc_progressBar = new GridBagConstraints();
		gbc_progressBar.gridwidth = 2;
		gbc_progressBar.fill = GridBagConstraints.HORIZONTAL;
		gbc_progressBar.insets = new Insets(0, 0, 0, 5);
		gbc_progressBar.gridx = 0;
		gbc_progressBar.gridy = 4;
		add(progressBar, gbc_progressBar);
		
		btnProcess = new JToggleButton(START);
		GridBagConstraints gbc_btnProcess = new GridBagConstraints();
		gbc_btnProcess.gridx = 2;
		gbc_btnProcess.gridy = 4;
		add(btnProcess, gbc_btnProcess);
		btnProcess.addItemListener(new ItemListener()  {
			public void itemStateChanged(ItemEvent state) {
				if (state.getStateChange() == ItemEvent.SELECTED) {
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
			boolean b = controller.startProcessor(textField.getText(), extractDatasets());
			if(!b) {
				btnProcess.setText(STOP);
				btnProcess.setSelected(true);
			}
		} else {
			btnProcess.setSelected(false);
		}
	}
	
	protected List<DatasetExposurePair> extractDatasets(){
		List<DatasetExposurePair> list = new ArrayList<DatasetExposurePair>();
		int n = table.getRowCount();
		
		for(int i=0;i<n;i++) {
			String ms = (String) table.getValueAt(i, 1);
			if(!ms.equals("") && utils.isInteger(ms)) {
				Integer exposure = Integer.parseInt(ms);
				
				list.add(new DatasetExposurePair(dataservice.getDatasets().get(i),exposure));
			}
		}
		
		return list;
	}

	@Override
	public void setDataPath(String path) {
		// Do nothing
	}

	@Override
	public void setProgress(String progress, int percentage) {
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
		btnProcess.setText(STOP);
		btnProcess.setSelected(false);
	}


	@Override
	public void processingHasEnded() {
		progressBar.setValue(100);
		btnProcess.setText(START);
		btnProcess.setSelected(false);
	}


	private String[][] buildList(){
		List<Dataset> datasets = dataservice.getDatasets();
		String[][] data = new String[datasets.size()][2];
		
		for(int i=0;i<datasets.size();i++) {
			// check if name contains "ms"
			String ms = extractMs(datasets.get(i).getName());
					
			data[i][0] = datasets.get(i).getName();
			data[i][1] = ms;
		}
		
		return data;
	}
	
	private String extractMs(String name) {
		String s = "";

		int ind = name.lastIndexOf("ms");
		if(ind != -1) {
			int i = 1;
			while(utils.isInteger(name.substring(ind-i,ind))) {
				s = name.substring(ind-i,ind);
				i ++;
			}
		}
		return s;
	}
}
