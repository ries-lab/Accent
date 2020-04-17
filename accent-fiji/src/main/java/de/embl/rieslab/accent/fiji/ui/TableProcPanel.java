package de.embl.rieslab.accent.fiji.ui;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import java.awt.GridBagLayout;
import javax.swing.JTable;
import javax.swing.JToggleButton;

import java.awt.GridBagConstraints;
import javax.swing.table.DefaultTableModel;

import org.joml.Math;

import de.embl.rieslab.accent.common.interfaces.pipeline.PipelineController;
import de.embl.rieslab.accent.common.interfaces.ui.ProcessorPanelInterface;
import de.embl.rieslab.accent.common.utils.Dialogs;
import de.embl.rieslab.accent.fiji.data.image.PlaneImg;
import de.embl.rieslab.accent.fiji.data.image.StackImg;
import de.embl.rieslab.accent.fiji.utils.AccentFijiUtils;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.border.LineBorder;
import java.awt.Color;
import java.awt.Container;

import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import java.awt.Font;

public class TableProcPanel extends JPanel implements ProcessorPanelInterface  {
	
	private static final long serialVersionUID = 1L;
	private JTable table;
	private JToggleButton btnProcess;
	private PipelineController<StackImg,PlaneImg> controller;
	private JProgressBar progressBar;
	
	private final static String START = "Process";
	private final static String STOP = "Stop";
	private JLabel lblPath;
	private JTextField textFieldPath;
	private JButton button;
	private JLabel lblNewLabel;
	

	/**
	 * Create the panel.
	 * @param dataservice 
	 * @param controller 
	 */
	public TableProcPanel(PipelineController<StackImg,PlaneImg> controller) {
		this.controller = controller;
		
		setBorder(new TitledBorder(null, "Process", TitledBorder.LEFT, TitledBorder.TOP, null, null));
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 1.0, 1.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		/*
		JLabel lblLeaveTheExposure = new JLabel("Leave the exposure field blank to ignore it");
		GridBagConstraints gbc_lblLeaveTheExposure = new GridBagConstraints();
		gbc_lblLeaveTheExposure.gridwidth = 2;
		gbc_lblLeaveTheExposure.anchor = GridBagConstraints.WEST;
		gbc_lblLeaveTheExposure.insets = new Insets(0, 0, 5, 5);
		gbc_lblLeaveTheExposure.gridx = 0;
		gbc_lblLeaveTheExposure.gridy = 0;
		add(lblLeaveTheExposure, gbc_lblLeaveTheExposure);*/
		
		//JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		//scrollPane.setMaximumSize(new Dimension(280, 100));
		gbc_scrollPane.gridheight = 2;
		gbc_scrollPane.gridwidth = 3;
		gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane.fill = GridBagConstraints.HORIZONTAL;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 0;
		//add(scrollPane, gbc_scrollPane);


		table = new JTable();
		//scrollPane.setViewportView(table);
		table.setBorder(new LineBorder(new Color(0, 0, 0)));
		
		// empty array to avoid weird overlays when changing the table
		String[][] arr = {
	            {"", ""},
	            {"", ""},
	            {"", ""},
	            {"", ""}
		};
		table.setModel(new DefaultTableModel(arr, new String[] { "Dataset", "Exposure (ms)" }) { 

			private static final long serialVersionUID = 1L;
			boolean[] columnEditables = new boolean[] { false, false };

			public boolean isCellEditable(int row, int column) {
				return columnEditables[column];
			}
		});
		table.getColumnModel().getColumn(0).setPreferredWidth(200);
		table.getColumnModel().getColumn(1).setPreferredWidth(80);
		add(table, gbc_scrollPane);		
		
		lblPath = new JLabel("Path:");
		GridBagConstraints gbc_lblPath = new GridBagConstraints();
		gbc_lblPath.anchor = GridBagConstraints.EAST;
		gbc_lblPath.insets = new Insets(0, 0, 5, 5);
		gbc_lblPath.gridx = 0;
		gbc_lblPath.gridy = 3;
		add(lblPath, gbc_lblPath);
		
		textFieldPath = new JTextField();
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.insets = new Insets(0, 0, 5, 5);
		gbc_textField.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField.gridx = 1;
		gbc_textField.gridy = 3;
		add(textFieldPath, gbc_textField);
		textFieldPath.setColumns(10);
		
		button = new JButton("...");
		GridBagConstraints gbc_button = new GridBagConstraints();
		gbc_button.insets = new Insets(0, 0, 5, 0);
		gbc_button.gridx = 2;
		gbc_button.gridy = 3;
		add(button, gbc_button);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {    	
				selectFolder();
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

	protected void selectFolder() {
		String curr = textFieldPath.getText();
		if(curr.equals("")) {
			curr = ".";
		}
		
		JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(new java.io.File(curr));
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = fc.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			String path = fc.getSelectedFile().getAbsolutePath();
			
			int nTiffs = AccentFijiUtils.getNumberTifsContainMs(path);
			int nDir = AccentFijiUtils.getNumberDirectoriesContainMs(path);
			
			// we try to load either folders content or images in the same folder
			// as independent exposure experiments (ie files with ###ms in the name)
			boolean loadStacks = nTiffs > nDir;
			
			try {
				Map<Double, String> datasets = AccentFijiUtils.getExposures(path, loadStacks);
				
				// if we load folders, then remove those without tiff files inside
				if(!loadStacks) {
					ArrayList<Double> noTiffFound = new ArrayList<Double>();
					for (Entry<Double, String> e : datasets.entrySet()) {
						if (AccentFijiUtils.getNumberTifs(e.getValue()) == 0) {
							noTiffFound.add(e.getKey());
						}
					}
					for (Double d : noTiffFound) {
						datasets.remove(d);
					}
				}	
				
				if(datasets.size() > 0) {
					textFieldPath.setText(path);
											
					// would be better to interact with the DefaultTableModel removing and adding rows, instead of creating it new... 
		/*			table.setModel(new DefaultTableModel(buildList(datasets), new String[] { "Dataset", "Exposure (ms)" }) { 
	
						private static final long serialVersionUID = 1L;
						boolean[] columnEditables = new boolean[] { false, false };
	
						public boolean isCellEditable(int row, int column) {
							return columnEditables[column];
						}
					});
					table.getColumnModel().getColumn(0).setPreferredWidth(200);
					table.getColumnModel().getColumn(1).setPreferredWidth(40);
					*/
					String[][] list = buildList(datasets);
					DefaultTableModel dtm = (DefaultTableModel) table.getModel();
					int nrow = dtm.getRowCount();
					for(int i=0;i<Math.min(nrow,list.length);i++) {
						dtm.setValueAt(list[i][0], i, 0);
						dtm.setValueAt(list[i][1], i, 1);
					}
					
					if(list.length>nrow) {
						for(int i=0;i<list.length-nrow;i++) {
							int pos = nrow+i;
							dtm.addRow(new String[] {list[pos][0],list[pos][1]});
						} 
					} else if(nrow>list.length) {
						for(int i=0;i<nrow-list.length;i++) {
							dtm.removeRow(list.length);
						}
					}
					
					// hack to resize the window to accommodate a large number of table rows
					Container cont = this.getParent();
					while(!(cont instanceof JFrame)) {
						cont = cont.getParent();
					}
					//((JFrame) cont).repaint();
					((JFrame) cont).pack();
				} else {
					Dialogs.showErrorMessage("No dataset found.");
				}
			} catch(Exception e) {
				e.printStackTrace();
				Dialogs.showErrorMessage("Error, make sure only the calibration images are present in the folder.");
			}		
		}
	}
	
	protected void stopProcessing() {
		controller.stopProcessor();
	}

	protected void startProcessing() {
		if(!controller.isProcessorRunning()) { // avoid trigger from setSelected(true) in processingHasStarted()
			String s  = textFieldPath.getText();
			// HashMap<String, Double> list = extractDatasets();
			boolean b = controller.startProcessor(s);
			if(!b) {
				btnProcess.setText(START);
				btnProcess.setSelected(false);
			}
		} else {
			btnProcess.setText(STOP);
			btnProcess.setSelected(false);
		}
	}
/*	
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
*/
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
		lblNewLabel.setText("Stopped.");
		btnProcess.setText(START);
		btnProcess.setSelected(false);
		progressBar.setValue(100);
	}

	@Override
	public void processingHasEnded() {
		progressBar.setValue(100);
		btnProcess.setText(START);
		btnProcess.setSelected(false);
	}

	private String[][] buildList(Map<Double, String> datasets){
		String[][] vals = new String[datasets.size()][2];
		int i=0;
		for(Entry<Double, String> e: datasets.entrySet()) {
			vals[i][0] = e.getValue();
			vals[i][1] = String.valueOf(e.getKey());
			i++;
		}
		
		return vals;
	}
}
