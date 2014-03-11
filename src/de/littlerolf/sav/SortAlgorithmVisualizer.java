package de.littlerolf.sav;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import de.littlerolf.sav.gui.ProgressFrame;
import de.littlerolf.sav.gui.SAVFrame;

public class SortAlgorithmVisualizer {
	private static final String SERVER = "github.io";
	private static final String SERVER_URL = "http://littlerolf.github.io/SortAlgorithmVisualizer/";
	private static final File DOWNLOADED_FILE = new File(
			System.getProperty("user.home") + File.separator + ".sav"
					+ File.separator + "SAV.jar");

	public static void main(String[] args) throws UnknownHostException,
			IOException {
		int localVersion = getLocalVersion();

		if (localVersion == -1
				|| !InetAddress.getByName(SERVER).isReachable(5000)
				|| (args.length > 1 && args[1].equals("ultraulf"))) {
			System.out.println("Starting local version.");
			startLocal();
			return;
		}

		int remoteVersion = getRemoteVersion();
		int downloadedVersion = getDownloadedJarVersion();

		if (remoteVersion > downloadedVersion) {
			System.out.println("Starting remote version.");
			downloadAndStartRemoteJar();
		} else {
			System.out.println("Starting cached version.");
			startJar(DOWNLOADED_FILE.getAbsolutePath());
		}

	}

	private static void downloadAndStartRemoteJar() {
		final ProgressFrame frame = new ProgressFrame();
		frame.setVisible(true);
		new Thread(new Runnable() {

			@Override
			public void run() {
				downloadRemoteJar(new DownloadListener() {

					@Override
					public void onDownloadProgress(int value, int max) {
						frame.setMaximum(100);
						frame.setMinimum(0);
						frame.setValue(value);
					}

					@Override
					public void onDownloadFinished(File f) {
						frame.setVisible(false);
						startJar(f.getAbsolutePath());
					}

				});

			}
		}).start();

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
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return -1;
	}

	private static int getDownloadedJarVersion() {
		if (!DOWNLOADED_FILE.exists())
			return -1;

		try {
			JarFile f = new JarFile(DOWNLOADED_FILE);
			int version = Integer.valueOf(f.getManifest().getMainAttributes()
					.getValue("Build-Number"));
			f.close();
			System.out.println("Downloaded version is " + version + ".");
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

	interface DownloadListener {
		public void onDownloadProgress(int value, int max);

		public void onDownloadFinished(File f);
	}

	private static File downloadRemoteJar(DownloadListener l) {
		System.out.println("Downloading remote jar.");
		File f = DOWNLOADED_FILE;
		f.getParentFile().mkdirs();
		URL website;
		try {
			website = new URL(SERVER_URL + "jar/SAV.jar");

			ReadableByteChannel rbc = new RBCWrapper(
					Channels.newChannel(website.openStream()),
					getContentLength(website), l);
			FileOutputStream fos = new FileOutputStream(f);
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
			fos.close();
			l.onDownloadFinished(f);
			return f;

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static int getContentLength(URL url) {
		HttpURLConnection connection;
		int contentLength = -1;

		try {
			HttpURLConnection.setFollowRedirects(false);

			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("HEAD");

			contentLength = connection.getContentLength();
		} catch (Exception e) {
		}

		return contentLength;
	}

	private static void startJar(String path) {
		System.out.println("Starting jar " + path + ".");
		ProcessBuilder pb = new ProcessBuilder("java", "-classpath", path,
				SortAlgorithmVisualizer.class.getName(), "ultraulf");
		try {
			Process p = pb.start();
			new StreamGobbler(p.getErrorStream()).start();
			new StreamGobbler(p.getInputStream()).start();

			p.waitFor();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
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

		new SAVFrame().setVisible(true);

	}

	private static class StreamGobbler extends Thread {
		InputStream is;

		// reads everything from is until empty.
		StreamGobbler(InputStream is) {
			this.is = is;
		}

		public void run() {
			try {
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				String line = null;
				while ((line = br.readLine()) != null)
					System.out.println(line);
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}

	private static final class RBCWrapper implements ReadableByteChannel {
		private DownloadListener delegate;
		private long expectedSize;
		private ReadableByteChannel rbc;
		private long readSoFar;

		RBCWrapper(ReadableByteChannel rbc, long expectedSize,
				DownloadListener delegate) {
			this.delegate = delegate;
			this.expectedSize = expectedSize;
			this.rbc = rbc;
		}

		public void close() throws IOException {
			rbc.close();
		}

		public boolean isOpen() {
			return rbc.isOpen();
		}

		public int read(ByteBuffer bb) throws IOException {
			int n;
			double progress;

			if ((n = rbc.read(bb)) > 0) {
				readSoFar += n;
				progress = expectedSize > 0 ? (double) readSoFar
						/ (double) expectedSize * 100.0 : -1.0;
				delegate.onDownloadProgress((int) progress, (int) expectedSize);
			}

			return n;
		}
	}
}
