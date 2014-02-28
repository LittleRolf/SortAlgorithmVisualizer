package de.littlerolf.sav.gui;

import java.awt.BorderLayout;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;

public class CodeEditorFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 738669143009851723L;
	private JEditorPane codeEditor = new JEditorPane();

	public CodeEditorFrame() {
		setTitle("Code Editor");
		getContentPane().setLayout(new BorderLayout());

		JScrollPane scrPane = new JScrollPane(codeEditor);
		getContentPane().add(scrPane, BorderLayout.CENTER);
		getContentPane().doLayout();
		codeEditor.setContentType("text/java");
		codeEditor.setText("public static void main(String[] args) {\n}");

		setSize(800, 600);

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu mnDatei = new JMenu("Datei");
		menuBar.add(mnDatei);

		JMenuItem mntmSpeichern = new JMenuItem("Speichern");
		mnDatei.add(mntmSpeichern);

	}
}
