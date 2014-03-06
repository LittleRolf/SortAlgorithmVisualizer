package de.littlerolf.sav;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.jar.Manifest;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import de.littlerolf.sav.gui.SAVFrame;

public class SortAlgorithmVisualizer {
	private static final String SERVER_URL = "http://littlerolf.github.io/SortAlgorithmVisualizer/";

	public static void main(String[] args) {
		if (isRemoteNewer()) {
			startJar(downloadRemoteJar().getAbsolutePath());
		} else
			startLocal();
	}

	private static int getLocalVersion() {
		URLClassLoader cl = (URLClassLoader) SortAlgorithmVisualizer.class
				.getClassLoader();
		try {
			URL url = cl.findResource("META-INF/MANIFEST.MF");
			Manifest manifest = new Manifest(url.openStream());
			int version = Integer.valueOf(manifest.getMainAttributes()
					.getValue("Build-Number"));
			System.out.println("Local version is " + version + ".");
			return version;

		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
		}
		return -1;
	}

	private static int getRemoteVersion() {
		URL url = null;
		try {
			url = new URL(SERVER_URL + "version.txt");
		} catch (MalformedURLException e2) {
			e2.printStackTrace();
		}
		try {
			URLConnection urlConnection = url.openConnection();
			urlConnection.setAllowUserInteraction(false);

			InputStream urlStream = url.openStream();
			byte buffer[] = new byte[1000];
			int numRead = urlStream.read(buffer);
			String content = new String(buffer, 0, numRead);

			while ((numRead != -1) && (content.length() < 1024)) {
				numRead = urlStream.read(buffer);
				if (numRead != -1) {
					String newContent = new String(buffer, 0, numRead);
					content += newContent;
				}
			}
			content = content.trim();
			System.out.println("Remote version is " + content + ".");

			return Integer.valueOf(content);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IndexOutOfBoundsException e1) {
			e1.printStackTrace();
		}

		return -1;
	}

	private static File downloadRemoteJar() {
		System.out.println("Downloading remote jar.");
		File f = new File(System.getProperty("user.home") + File.separator
				+ ".sav" + File.separator + "SAV.jar");
		f.getParentFile().mkdirs();
		URL website;
		try {
			website = new URL(SERVER_URL + "jar/SAV.jar");

			ReadableByteChannel rbc = Channels.newChannel(website.openStream());
			FileOutputStream fos = new FileOutputStream(f);
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
			fos.close();
			return f;

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static boolean isRemoteNewer() {
		return getRemoteVersion() > getLocalVersion();
	}

	private static void startJar(String path) {
		System.out.println("Starting jar " + path + ".");
		ProcessBuilder pb = new ProcessBuilder("java", "-classpath", path,
				SortAlgorithmVisualizer.class.getName());
		try {
			pb.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void startLocal() {
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
