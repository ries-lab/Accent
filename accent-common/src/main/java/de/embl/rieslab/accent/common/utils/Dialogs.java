package de.embl.rieslab.accent.common.utils;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Dialogs {

	public static void showErrorMessage(String message) {
		JOptionPane.showMessageDialog(new JFrame(), message, "Dialog",
		        JOptionPane.ERROR_MESSAGE);
	}
	
	public static void showWarningMessage(String message) {
		JOptionPane.showMessageDialog(new JFrame(), message, "Dialog",
		        JOptionPane.WARNING_MESSAGE);
	}
}
