package de.littlerolf.sav;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import de.littlerolf.sav.gui.SAVFrame;

public class SortAlgorithmVisualizer {
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (UnsupportedLookAndFeelException e) {
		} catch (ClassNotFoundException e) {
		} catch (InstantiationException e) {
		} catch (IllegalAccessException e) {
		}
		JOptionPane
				.showMessageDialog(
						null,
						"Yo. Also. Gleich kommt so ein Ordnerauswahlfenster.\n"
								+ "Da musst du dann mal eben den Ordner auswählen, in dessen Unterordnern deine .class-Dateien liegen.\n"
								+ "Alles normal.");
		JFileChooser folderChooser = new JFileChooser();
		folderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		JFrame f = new JFrame();
		f.setVisible(true);
		int returnedValue = folderChooser.showOpenDialog(f);
		f.dispose();
		if (returnedValue == JFileChooser.APPROVE_OPTION) {
			new SAVFrame(folderChooser.getSelectedFile().getAbsolutePath())
					.setVisible(true);
		} else {
			JOptionPane.showMessageDialog(null, "Sheesh.", "Fail.",
					JOptionPane.ERROR_MESSAGE);
		}
	}
}
