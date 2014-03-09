package de.littlerolf.sav.gui;

import javax.swing.JFrame;
import javax.swing.JProgressBar;
import java.awt.BorderLayout;

public class ProgressFrame extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JProgressBar progressBar;

	public ProgressFrame() {
		setResizable(false);
		setTitle("Doing awesome shit.");
		setLocationRelativeTo(null);
		progressBar = new JProgressBar();
		progressBar.setStringPainted(true);
		getContentPane().add(progressBar, BorderLayout.CENTER);
		this.setSize(400, 100);
		this.setAlwaysOnTop(true);
	}

	public void setString(String s) {
		this.progressBar.setString(s);
	}

	public void setMaximum(int max) {
		this.progressBar.setMaximum(max);
	}

	public void setMinimum(int min) {
		this.progressBar.setMinimum(min);
	}

	public void setValue(int v) {
		this.progressBar.setValue(v);
	}
}
