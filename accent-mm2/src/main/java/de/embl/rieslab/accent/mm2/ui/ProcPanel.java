package de.embl.rieslab.accent.mm2.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import de.embl.rieslab.accent.common.data.roi.SimpleRoi;
import de.embl.rieslab.accent.common.data.roi.SimpleRoiIO;
import de.embl.rieslab.accent.common.interfaces.pipeline.PipelineController;
import de.embl.rieslab.accent.common.interfaces.ui.ProcessorPanelInterface;
import de.embl.rieslab.accent.common.processor.CalibrationProcessor;
import de.embl.rieslab.accent.common.utils.AccentUtils;
import de.embl.rieslab.accent.common.utils.Dialogs;
import de.embl.rieslab.accent.mm2.data.image.BareImage;
import de.embl.rieslab.accent.mm2.data.image.FloatImage;

public class ProcPanel extends JPanel implements ProcessorPanelInterface {
	
	private static final long serialVersionUID = 1L;
	private JTextField folderField;
	private JProgressBar procProgressBar;
	private JButton folderButton;
	private JLabel procFeedbackLabel;
	private JToggleButton processButton;
	private JTextField tfX0;
	private JTextField tfY0;
	
	private final static String START = "Process";
	private final static String STOP = "Stop";
		
	private PipelineController<BareImage, FloatImage> controller;
	private boolean preventTrigger = false;

	/**
	 * Create the panel.
	 */
	public ProcPanel(PipelineController<BareImage, FloatImage> controller) {
		this.controller = controller;
		
		this.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Process raw data", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		this.setLayout(new GridBagLayout());  
		
		JLabel folderLabel = new JLabel("Folder:");
		GridBagConstraints gbcMain = new GridBagConstraints();
		gbcMain.fill = GridBagConstraints.HORIZONTAL;
		gbcMain.gridx = 0;
		gbcMain.gridy = 0;
		gbcMain.weightx = 0;
		this.add(folderLabel, gbcMain);
		
		folderField = new JTextField(" ");		
		gbcMain.gridx = 1;
		gbcMain.weightx = 1;
		this.add(folderField, gbcMain);
		
		folderButton = new JButton("...");
		folderButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {    	
				showPathSelectionWindow();
			}
		});		
		gbcMain.gridx = 2;
		gbcMain.weightx = 0;
		this.add(folderButton, gbcMain);
		
		
		JPanel roipane = new JPanel(new GridBagLayout());
		GridBagConstraints gbc_roi = new GridBagConstraints();
		gbc_roi.fill = GridBagConstraints.HORIZONTAL;
		gbc_roi.gridx = 0;
		gbc_roi.gridy = 0;
		gbc_roi.weightx = 0.6;
		gbc_roi.gridwidth = 2;
		roipane.add(new JLabel(""), gbc_roi);
		
		JLabel x0 = new JLabel("X0:");
		gbc_roi.insets = new Insets(0, 2, 5, 2);
		gbc_roi.fill = GridBagConstraints.NONE;
		gbc_roi.gridx = 2;
		gbc_roi.gridy = 0;
		gbc_roi.gridwidth = 1;
		gbc_roi.weightx = 0.1;
		roipane.add(x0, gbc_roi);
		
		tfX0 = new JTextField(" ");
		gbc_roi.fill = GridBagConstraints.HORIZONTAL;
		gbc_roi.gridx = 3;
		gbc_roi.gridwidth = 1;
		gbc_roi.weightx = 0.5;
		roipane.add(tfX0, gbc_roi);
		
		JLabel y0 = new JLabel("Y0:");
		gbc_roi.fill = GridBagConstraints.NONE;
		gbc_roi.gridx = 5;
		gbc_roi.gridwidth = 1;
		gbc_roi.weightx = 0.1;
		roipane.add(y0, gbc_roi);

		tfY0 = new JTextField(" ");
		gbc_roi.fill = GridBagConstraints.HORIZONTAL;
		gbc_roi.gridx = 6;
		gbc_roi.gridwidth = 2;
		gbc_roi.weightx = 0.5;
		roipane.add(tfY0, gbc_roi);
		
		gbcMain.insets = new Insets(0, 0, 0, 0);
		gbcMain.fill = GridBagConstraints.HORIZONTAL;
		gbcMain.gridwidth = 3;
		gbcMain.gridx = 0;
		gbcMain.gridy = 1;
		add(roipane, gbcMain);
		
		procFeedbackLabel = new JLabel("    ");
		procFeedbackLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
		gbcMain.gridwidth = 1;
		gbcMain.insets = new Insets(0, 0, 5, 5);
		gbcMain.gridy = 2;
		this.add(procFeedbackLabel, gbcMain);
		
		processButton = new JToggleButton(START);		
		processButton.setFont(new Font("Tahoma", Font.PLAIN, 12));
		gbcMain.fill = GridBagConstraints.VERTICAL;
		gbcMain.gridheight = 2;
		gbcMain.gridx = 2;
		gbcMain.gridy = 2;
		this.add(processButton, gbcMain);
		
		procProgressBar = new JProgressBar();
		gbcMain.fill = GridBagConstraints.HORIZONTAL;
		gbcMain.gridheight = 1;
		gbcMain.gridwidth = 2;
		gbcMain.gridx = 0;
		gbcMain.gridy = 3;
		this.add(procProgressBar, gbcMain);
		
		processButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				AbstractButton abstractButton = (AbstractButton) actionEvent.getSource();
				boolean selected = abstractButton.getModel().isSelected();
				if(!preventTrigger) {
					if (selected) {
						startProcessing();
					} else {
						stopProcessing();
					}
				}
			}
		});
	}
	
	protected void stopProcessing() {
		controller.stopProcessor();	
	}

	private void selectProcessToggle(boolean b) {
		preventTrigger = true;
		processButton.setSelected(b);
		preventTrigger = false;
	}
	
	protected void startProcessing() {
		if(!controller.isProcessorRunning()) { // avoid trigger from setSelected(true) in processingHasStarted()
			String path = folderField.getText();
			if(!path.isEmpty()) {
				SimpleRoi roi = getRoi();
				boolean b = controller.startProcessor(path, roi);
				if(b) {
					processButton.setText(STOP);
					selectProcessToggle(true);
				}	
			} else {
				selectProcessToggle(false);
				Dialogs.showWarningMessage("Select a path.");
			}
		} else {
			selectProcessToggle(false);
			Dialogs.showWarningMessage("Processor already running..");
		}
	}

	protected void showPathSelectionWindow() {
		JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(new java.io.File("."));
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = fc.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File folder = fc.getSelectedFile();
			folderField.setText(folder.getAbsolutePath());
			
			// checks if a SimpleRoi is present
			File roi_f = new File(folder.getAbsolutePath()+"/"+CalibrationProcessor.DEFAULT_ROI);
			if(roi_f.exists() && SimpleRoiIO.read(roi_f) != null) {
				SimpleRoi roi = SimpleRoiIO.read(roi_f);
				setRoi(roi);
			} else {
				setRoi(new SimpleRoi(0,0,0,0));
			}
		}
	}


	@Override
	public void setProgress(String progress, int percentage) {
		procProgressBar.setValue(percentage);
		procFeedbackLabel.setText(progress);
	}


	@Override
	public void processingHasStarted() {
		procProgressBar.setValue(0);
		processButton.setText(STOP);
		if(!processButton.isSelected()) {
			processButton.setSelected(true);
		}
	}


	@Override
	public void processingHasStopped() {
		processButton.setText(START);
		processButton.setSelected(false);
	}


	@Override
	public void processingHasEnded() {
		procProgressBar.setValue(100);
		processButton.setText(START);
		processButton.setSelected(false);
	}


	@Override
	public void setDataPath(String path) {
		folderField.setText(path);
	}
	
	private SimpleRoi getRoi() {
		String s_x0 = tfX0.getText();
		String s_y0 = tfY0.getText();

		// if negative w,h then ultimately the calibration will consider
		// the roi width/height from the images themselves
		if (AccentUtils.isInteger(s_x0) && AccentUtils.isInteger(s_y0)) {

			SimpleRoi roi = new SimpleRoi(Integer.parseInt(s_x0), Integer.parseInt(s_y0), 0, 0);
			return roi;
		} else {
			return new SimpleRoi(0, 0, 0, 0);
		}
	}
	
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.add(new ProcPanel(null));
		
		frame.pack();
		frame.setVisible(true);
	}

	@Override
	public void setRoi(SimpleRoi roi) {
		if(roi != null) {
			tfX0.setText(String.valueOf(roi.x0));
			tfY0.setText(String.valueOf(roi.y0));
		}
	}

}
