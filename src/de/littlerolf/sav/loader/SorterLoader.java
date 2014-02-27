package de.littlerolf.sav.loader;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

/**
 * The Main Class of the Loader module, used for loading and accessing the
 * specific Sorters
 * 
 * @author ole
 * 
 */
public class SorterLoader {

	private final String classpath;
	private boolean debug = false;

	private List<Class> classes = new ArrayList<Class>();

	/**
	 * Creates a new SorterLoader looking for classes in the given path
	 * 
	 * @param path
	 *            The path to look for classes
	 */
	public SorterLoader(String path) {
		classpath = path;

	}

	/**
	 * Creates a new SorterLoader looking for classes in the given path and
	 * offers ability to enable debug output
	 * 
	 * @param path
	 *            The path to look for classes
	 * @param debug
	 *            Debug Flag
	 */
	public SorterLoader(String path, boolean debug) {
		classpath = path;
		this.debug = debug;
	}

	/**
	 * Loads all classes named 'Sorter' from the set folder from all the
	 * subdirectories
	 */
	public void loadAllClasses() {
		// Create a File object on the root of the directory containing the
		// class file
		File file = new File(classpath);

		try {
			// Convert File to a URL
			URL url = file.toURI().toURL(); // file:/c:/myclasses/
			URL[] urls = new URL[] { url };

			// Create a new class loader with the directory
			ClassLoader cl = new URLClassLoader(urls);

			String[] directories = getSubdirectories();

			for (String folder : directories) {
				classes.add(cl.loadClass(folder + ".Sorter"));
			}

		} catch (MalformedURLException e) {
		} catch (ClassNotFoundException e) {
		}

		if (debug) {
			System.out.println("[Debug] Loaded Classes:");
			System.out.println("____________________");
			for (Class c : classes) {
				System.out.println(c.getPackage().getName() + "    "
						+ c.getName());
			}
		}
	}

	public Class getSorterByPackageName(String packageName) {
		for (Class c : classes) {
			if (c.getPackage().getName().equals(packageName)) {
				return c;
			}
		}
		return null;
	}

	public String[] getAvailablePackagesList() {
		String[] packages = new String[classes.size()];
		for (int i = 0; i < classes.size() - 1; i++) {
			packages[i] = classes.get(i).getPackage().getName();
		}
		return packages;
	}

	private String[] getSubdirectories() {
		File file = new File(classpath);
		String[] directories = file.list(new FilenameFilter() {
			public boolean accept(File current, String name) {
				return new File(current, name).isDirectory();
			}
		});
		return directories;
	}
}
