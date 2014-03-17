package de.littlerolf.sav.loader;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JOptionPane;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import de.littlerolf.sav.data.BaseSorter;

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
	private SorterLoaderListener listener;

	private List<Class> classes = new ArrayList<Class>();
	private List<BaseSorter> sorters = new ArrayList<BaseSorter>();

	/**
	 * Creates a new SorterLoader looking for classes in the given path
	 * 
	 * @param path
	 *            The path to look for classes
	 */
	public SorterLoader(String path, SorterLoaderListener listener) {
		this.listener = listener;
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
	public SorterLoader(String path, SorterLoaderListener listener,
			boolean debug) {
		classpath = path;
		this.listener = listener;
		this.debug = debug;
	}

	public void compileAllClasses() {
		try {
		String[] directories = getSubdirectories();
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		StandardJavaFileManager fileManager = compiler.getStandardFileManager(
				null, null, null);
		for (final String d : directories) {
			File sourceFile = new File(classpath + File.separator + d
					+ File.separator + "Sorter.java");
			if (!sourceFile.exists())
				continue;
			File targetFile = new File(classpath + File.separator + d
					+ File.separator + "Sorter.class");
			if (targetFile.exists())
				continue;
			Iterable<? extends JavaFileObject> units;
			units = fileManager.getJavaFileObjectsFromFiles(Arrays
					.asList(sourceFile));
			CompilationTask task = compiler.getTask(null, fileManager,
					new DiagnosticListener<Object>() {

						@Override
						public void report(Diagnostic<?> diagnostic) {
							JOptionPane.showMessageDialog(null,
									diagnostic.toString(), "Fehler in Klasse "
											+ d, JOptionPane.ERROR_MESSAGE);
						}
					}, null, null, units);

			if (!task.call()) {
				System.err.println("Couldn't compile class " + d + ".");
			} else {
				System.out.println("Compiled " + d);
			}
			try {
				fileManager.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		System.out.println("Finished compiling.");
	} catch(Exception e) {
		e.printStackTrace();
		
	}
	}

	/**
	 * Loads all classes named 'Sorter' from the set folder from all the
	 * subdirectories
	 */
	@SuppressWarnings("unchecked")
	// We can safely ignore the warnings because it's being checked
	public void loadAllClasses() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// Create a File object on the root of the directory containing
				// the
				// class file
				SorterLoader.this.classes.clear();
				SorterLoader.this.sorters.clear();
				File file = new File(classpath);
				SorterLoader.this.listener.onSorterLoadingStarted();
				try {
					// Convert File to a URL
					URL url = file.toURI().toURL(); // file:/c:/myclasses/
					URL[] urls = new URL[] { url };

					// Create a new class loader with the directory
					@SuppressWarnings("resource")
					// Bug in JDK.
					ClassLoader cl = new URLClassLoader(urls);

					String[] directories = getSubdirectories();

					int i = 0;
					for (String folder : directories) {
						SorterLoader.this.listener.onSorterLoaded(i++,
								directories.length);
						try {
							Class<?> clazz = cl.loadClass(folder + ".Sorter");

							classes.add(clazz);
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						} catch (NoClassDefFoundError e) {
							e.printStackTrace();
						}
					}

				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
				SorterLoader.this.listener.onSorterLoadingFinished();

				if (debug) {
					System.out.println("[Debug] Loaded Classes:");
					System.out.println("____________________");
					for (Class<BaseSorter> c : classes) {
						System.out.println(c.getPackage().getName() + "    "
								+ c.getName());
					}
				}
			}

		}).start();

	}

	public void instanstiateAllClasses() {
		if (classes.size() == 0) {
			if (debug) {
				System.out.println("You have to call loadAllClasses() first!");
			}
			return;
		}
		for (Class<BaseSorter> c : classes) {
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
	public Class<BaseSorter> getSorterClassByPackageName(String packageName) {
		for (Class<BaseSorter> c : classes) {
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
