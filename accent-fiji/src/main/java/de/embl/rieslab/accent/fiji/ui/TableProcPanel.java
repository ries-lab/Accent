package de.embl.rieslab.accent.fiji.ui;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import java.awt.GridBagLayout;
import javax.swing.JTable;
import javax.swing.JToggleButton;

import java.awt.GridBagConstraints;
import javax.swing.table.DefaultTableModel;

import org.joml.Math;

import de.embl.rieslab.accent.common.data.roi.SimpleRoi;
import de.embl.rieslab.accent.common.data.roi.SimpleRoiIO;
import de.embl.rieslab.accent.common.interfaces.pipeline.PipelineController;
import de.embl.rieslab.accent.common.interfaces.ui.ProcessorPanelInterface;
import de.embl.rieslab.accent.common.processor.CalibrationProcessor;
import de.embl.rieslab.accent.common.utils.AccentUtils;
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
import java.io.File;
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
	private JTextField tfX0;
	private JTextField tfY0;
	private JTextField tfW;
	private JTextField tfH;
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
		
		GridBagConstraints gbcMain = new GridBagConstraints();
		//scrollPane.setMaximumSize(new Dimension(280, 100));
		gbcMain.gridheight = 2;
		gbcMain.gridwidth = 3;
		gbcMain.insets = new Insets(0, 0, 5, 0);
		gbcMain.fill = GridBagConstraints.HORIZONTAL;
		gbcMain.gridx = 0;
		gbcMain.gridy = 0;
		gbcMain.weighty = 0.5;
		add(table, gbcMain);		
		
		lblPath = new JLabel("Path:");
		gbcMain.gridheight = 1;
		gbcMain.gridwidth = 1;
		gbcMain.insets = new Insets(0, 0, 5, 5);
		gbcMain.gridx = 0;
		gbcMain.gridy = 3;
		gbcMain.weightx = 0;
		add(lblPath, gbcMain);
		
		textFieldPath = new JTextField();
		gbcMain.insets = new Insets(0, 0, 0, 0);
		gbcMain.anchor = GridBagConstraints.CENTER;
		gbcMain.fill = GridBagConstraints.HORIZONTAL;
		gbcMain.gridx = 1;
		gbcMain.gridy = 3;
		gbcMain.weightx = 0.5;
		add(textFieldPath, gbcMain);
		textFieldPath.setColumns(10);
		
		button = new JButton("...");
		gbcMain.insets = new Insets(0, 0, 0, 0);
		gbcMain.fill = GridBagConstraints.HORIZONTAL;
		gbcMain.gridx = 2;
		gbcMain.gridy = 3;
		gbcMain.weightx = 0;
		add(button, gbcMain);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {    	
				selectFolder();
			}
		});
		
		JPanel roipane = new JPanel(new GridBagLayout());
		JLabel x0 = new JLabel("X0:");
		GridBagConstraints gbc_roi = new GridBagConstraints();
		gbc_roi.insets = new Insets(0, 0, 5, 0);
		gbc_roi.fill = GridBagConstraints.NONE;
		gbc_roi.gridx = 0;
		gbc_roi.gridy = 0;
		gbc_roi.gridwidth = 1;
		roipane.add(x0, gbc_roi);
		
		tfX0 = new JTextField(" ");
		gbc_roi.fill = GridBagConstraints.HORIZONTAL;
		gbc_roi.gridx = 1;
		gbc_roi.gridwidth = 2;
		gbc_roi.weightx = 0.5;
		roipane.add(tfX0, gbc_roi);
		
		JLabel y0 = new JLabel("Y0:");
		gbc_roi.fill = GridBagConstraints.NONE;
		gbc_roi.gridx = 3;
		gbc_roi.gridwidth = 1;
		gbc_roi.weightx = 0;
		roipane.add(y0, gbc_roi);

		tfY0 = new JTextField(" ");
		gbc_roi.fill = GridBagConstraints.HORIZONTAL;
		gbc_roi.gridx = 4;
		gbc_roi.gridwidth = 2;
		gbc_roi.weightx = 0.5;
		roipane.add(tfY0, gbc_roi);

		JLabel w = new JLabel("W:");
		gbc_roi.fill = GridBagConstraints.NONE;
		gbc_roi.gridx = 6;
		gbc_roi.gridwidth = 1;
		gbc_roi.weightx = 0;
		roipane.add(w, gbc_roi);

		tfW = new JTextField(" ");
		gbc_roi.fill = GridBagConstraints.HORIZONTAL;
		gbc_roi.gridx = 7;
		gbc_roi.gridwidth = 2;
		gbc_roi.weightx = 0.5;
		roipane.add(tfW, gbc_roi);

		JLabel h = new JLabel("H:");
		gbc_roi.fill = GridBagConstraints.NONE;
		gbc_roi.gridx = 9;
		gbc_roi.gridwidth = 1;
		gbc_roi.weightx = 0;
		roipane.add(h, gbc_roi);

		tfH = new JTextField(" ");
		gbc_roi.fill = GridBagConstraints.HORIZONTAL;
		gbc_roi.gridx = 10;
		gbc_roi.gridwidth = 2;
		gbc_roi.weightx = 0.5;
		roipane.add(tfH, gbc_roi);
		
		gbcMain.insets = new Insets(0, 0, 0, 0);
		gbcMain.fill = GridBagConstraints.HORIZONTAL;
		gbcMain.gridwidth = 3;
		gbcMain.gridx = 0;
		gbcMain.gridy = 4;
		add(roipane, gbcMain);
		
		lblNewLabel = new JLabel(" ");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
		gbcMain.gridwidth = 1;
		gbcMain.insets = new Insets(0, 0, 5, 5);
		gbcMain.gridx = 1;
		gbcMain.gridy = 5;
		add(lblNewLabel, gbcMain);
		
		btnProcess = new JToggleButton(START);
		gbcMain.fill = GridBagConstraints.VERTICAL;
		gbcMain.gridheight = 2;
		gbcMain.insets = new Insets(5, 0, 5, 0);
		gbcMain.gridx = 2;
		gbcMain.gridy = 5;
		gbcMain.weighty = 0.3;
		add(btnProcess, gbcMain);
		
		progressBar = new JProgressBar();
		gbcMain.insets = new Insets(0, 5, 5, 0);
		gbcMain.gridwidth = 2;
		gbcMain.fill = GridBagConstraints.HORIZONTAL;
		gbcMain.gridx = 0;
		gbcMain.gridy = 6;
		gbcMain.weighty = 0;
		add(progressBar, gbcMain);
		
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
					
					// checks if a SimpleRoi is present
					File roi_f = new File(path+"/"+CalibrationProcessor.DEFAULT_ROI);
					if(roi_f.exists() && SimpleRoiIO.read(roi_f) != null) {
						SimpleRoi roi = SimpleRoiIO.read(roi_f);
						tfX0.setText(String.valueOf(roi.x0));
						tfY0.setText(String.valueOf(roi.y0));
						tfW.setText(String.valueOf(roi.width));
						tfH.setText(String.valueOf(roi.height));
					} else {
						tfX0.setText("0");
						tfY0.setText("0");
						tfW.setText("0");
						tfH.setText("0");
					}
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
			String path  = textFieldPath.getText();
			SimpleRoi roi = getRoi();

			// HashMap<String, Double> list = extractDatasets();
			boolean b = controller.startProcessor(path, roi);
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
	
	private SimpleRoi getRoi() {
		String x0 = tfX0.getText();
		String y0 = tfY0.getText();
		String w = tfW.getText();
		String h = tfH.getText();
		
		if(AccentUtils.isInteger(x0) &&
				AccentUtils.isInteger(y0) &&
				AccentUtils.isInteger(w) &&
				AccentUtils.isInteger(h)) {
			return new SimpleRoi(Integer.parseInt(x0),
					Integer.parseInt(y0),
					Integer.parseInt(w),
					Integer.parseInt(h));
		} else {
			return new SimpleRoi(0,0,0,0);
		}
	}
	
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.add(new TableProcPanel(null));
		
		frame.pack();
		frame.setVisible(true);
	}
	
}

