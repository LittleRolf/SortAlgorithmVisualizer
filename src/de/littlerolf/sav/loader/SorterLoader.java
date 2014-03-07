package de.littlerolf.sav.loader;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.littlerolf.sav.data.BaseSorter;
import de.littlerolf.sav.gui.ProgressFrame;

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
	private List<BaseSorter> sorters = new ArrayList<BaseSorter>();

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
		this.classes.clear();
		this.sorters.clear();
		File file = new File(classpath);
		ProgressFrame frame = new ProgressFrame();
		frame.setVisible(true);
		try {
			// Convert File to a URL
			URL url = file.toURI().toURL(); // file:/c:/myclasses/
			URL[] urls = new URL[] { url };

			// Create a new class loader with the directory
			ClassLoader cl = new URLClassLoader(urls);

			String[] directories = getSubdirectories();
			frame.setMaximum(directories.length);
			int i = 0;
			for (String folder : directories) {
				frame.setValue(i++);
				try {
					classes.add(cl.loadClass(folder + ".Sorter"));
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (NoClassDefFoundError e) {
					e.printStackTrace();
				}
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		frame.setVisible(false);

		if (debug) {
			System.out.println("[Debug] Loaded Classes:");
			System.out.println("____________________");
			for (Class c : classes) {
				System.out.println(c.getPackage().getName() + "    "
						+ c.getName());
			}
		}
	}

	public void instanstiateAllClasses() {
		if (classes.size() == 0) {
			if (debug) {
				System.out.println("You have to call loadAllClasses() first!");
			}
			return;
		}
		for (Class c : classes) {
			try {
				sorters.add((BaseSorter) c.newInstance());
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	public List<BaseSorter> getAllSorters() {
		return sorters;
	}

	public BaseSorter getSorterByName(String name) {
		for (BaseSorter bs : sorters) {
			if (bs.getName().equals(name)) {
				return bs;
			}
		}
		return null;
	}

	public String[] getAvailableSorters() {
		String[] packages = new String[sorters.size()];
		for (int i = 0; i < sorters.size(); i++) {
			packages[i] = sorters.get(i).getName();
		}
		if (debug) {
			System.out.println(Arrays.toString(packages));
		}
		return packages;
	}

	/**
	 * Gets the Class of a Sorter via the Package name
	 * 
	 * @param packageName
	 *            package name to search for
	 * @return The found Class or null
	 */
	public Class getSorterClassByPackageName(String packageName) {
		for (Class c : classes) {
			if (c.getPackage().getName().equals(packageName)) {
				if (debug) {
					System.out.println("Found loaded class for package name: "
							+ packageName);
				}
				return c;
			}
		}
		return null;
	}

	/**
	 * Returns a full list of all available package names
	 * 
	 * @return The list of absolute awesomeness
	 */
	public String[] getAvailablePackagesList() {
		String[] packages = new String[classes.size()];
		for (int i = 0; i < classes.size(); i++) {
			packages[i] = classes.get(i).getPackage().getName();
		}
		if (debug) {
			System.out.println(Arrays.toString(packages));
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
