package main.java.embl.rieslab.photonfreecamcalib.ui;

import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.SystemColor;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.micromanager.Studio;

import main.java.embl.rieslab.photonfreecamcalib.PipelineController;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainFrame extends JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = -903001376907979987L;

	/**
	 * Create the frame.
	 */
	public MainFrame(Studio studio) {
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		JPanel content = new JPanel();
		content.setLayout(new BoxLayout(content, BoxLayout.PAGE_AXIS));
		
		// help panel
		JPanel helppanel = new JPanel();
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.rowHeights = new int[] {0, 0};
		gbl_contentPane.columnWidths = new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		helppanel.setLayout(gbl_contentPane);
		content.add(helppanel);
		JLabel helpLabel = new JLabel("<HTML><U>Help...</U></HTML>");
		helpLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				
			}
		});
		
		helpLabel.setForeground(SystemColor.textHighlight);
		helpLabel.setFont(new Font("Tahoma", Font.PLAIN, 11));
		helpLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		GridBagConstraints gbc_helpLabel = new GridBagConstraints();
		gbc_helpLabel.anchor = GridBagConstraints.EAST;
		gbc_helpLabel.insets = new Insets(0, 0, 5, 0);
		gbc_helpLabel.gridx = 9;
		gbc_helpLabel.gridy = 0;
		helppanel.add(helpLabel, gbc_helpLabel);
		
		// controller and other panels
		PipelineController controller = new PipelineController(studio);
		
		AcqPanel acqpane = new AcqPanel(studio.getCMMCore().getCameraDevice(), controller); 
		ProcPanel procpane = new ProcPanel(controller);
		GenPanel genpane = new GenPanel(controller);
		
		
		controller.setAcquisitionPanel(acqpane);
		controller.setProcessingPanel(procpane);
		controller.setGeneratePanel(genpane);

		content.add(acqpane);
		content.add(procpane);
		content.add(genpane);
		
		this.setContentPane(content);
		
		this.pack();
		this.setLocationRelativeTo(null);
	}

}
