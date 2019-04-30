package main.java.embl.rieslab.accent.utils;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Dialogs {

	public static void showErrorMessage(String message) {
		JOptionPane.showMessageDialog(new JFrame(), message, "Dialog",
		        JOptionPane.ERROR_MESSAGE);
	}
}
