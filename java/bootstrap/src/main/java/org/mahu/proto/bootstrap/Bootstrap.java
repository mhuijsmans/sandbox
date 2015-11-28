package org.mahu.proto.bootstrap;

import java.io.File;
import java.net.URLClassLoader;
import java.net.URL;
import java.util.LinkedList;

public class Bootstrap {

	public final static String BOOTSTRAP_MAINCLASS = "bootstrap.mainclass";

	public static void main(String[] argv) {
		(new Bootstrap()).start(argv);
	}

	private void start(String[] argv) {
		try {
			// Get a list of jar's
			// URL[]urls = getURLs(argv);
			URL[]urls = getURLs(null);

			// then start main application
			URLClassLoader cl = new URLClassLoader( urls );
			// next line is needed for cases where the a class want to
			// find data by looking at the classloader of the current thread
			Thread.currentThread().setContextClassLoader(cl);

			final String bootstrapMain = System.getProperty(BOOTSTRAP_MAINCLASS,null);
			if (bootstrapMain==null) {
				System.out.println("Property not set: "+BOOTSTRAP_MAINCLASS);
				return;
			}

			final Class clazz =	Class.forName(bootstrapMain, true, cl);
			Class[] parameterTypes = new Class[1];
			parameterTypes[0] = argv.getClass();
			Object[] args = new Object[1];
			args[0] = argv;

			clazz.getMethod("main",parameterTypes).invoke(null,args);

		} catch(Exception e) {

			System.err.println("Exception while executing Bootstrap.start()");
			printDetails(e);
			return;
		}
	}

	private static URL[] getURLs(String []argv) throws Exception {
		// Get a list of jar's
		String libdir = getLibDir(argv);
		LinkedList jars = getFiles(libdir,".jar",false);
		LinkedList urls = new LinkedList();
		while(!jars.isEmpty()) {
			urls.add(((File)jars.removeFirst()).toURL());
		}
		// If this class is not loaded via a jar, add that directory
		// Format:
		// file:/C:/Documents and Settings/elnmahu/My Documents/elnmahu/vegalite/vegalite_processes2/sipcontainer/classes/SipContainer.class
		String s = ClassLoader.getSystemResource("SipContainer.class").toString();
		if (s.startsWith("file:/")) {
			s = s.substring(s.indexOf('/') + 1, s.lastIndexOf('/')+1);
			urls.addFirst((new File(s)).toURL());
		}
		//
		URL[]tmp = new URL[urls.size()];
		urls.toArray(tmp);
		return tmp;
	}


	/**
	 * Get the name of the directory that contained the jar
	 */
	 private static String getLibDir(String []argv) {
		if (argv!=null && argv.length>0) {
			return argv[0];
		}

		String s = ClassLoader.getSystemResource("Bootstrap.class").toString();
		if (!s.startsWith("jar:file:/"))
			throw new RuntimeException("Coding error, resourcename=" + s);

		s = s.substring(s.indexOf('/') + 1);
		s = s.substring(0, s.indexOf('!'));
		s = s.substring(0, s.lastIndexOf('/') + 1);
		int idx=0;
		while((idx=s.indexOf("%20"))>0) {
			s = s.substring(0,idx)+' '+s.substring(idx+3);
		}
		return s;
	}

	/**
	 * Get all the files in the given directory with the given suffix
	 */
	private static LinkedList getFiles(
		final String dirName,
		final String suffix,
		final boolean inspectSubdirs) {

		LinkedList list = new LinkedList();
		File dir = new File(dirName);
		getFiles(dir, suffix, list, inspectSubdirs);
		return list;
	}

	/**
	 * Add all the files in the given directory with the given suffix
	 * to list
	 */
	private static void getFiles(
		final File dir,
		final String suffix,
		final LinkedList list,
		final boolean inspectSubdirs) {

		if (!dir.isDirectory())
			throw new RuntimeException("Specified path is not a directory "+dir.getAbsolutePath());

		File[] files = dir.listFiles();
		if (files == null)
			throw new RuntimeException("Specified directory is empty");

		// String seperator = System.getProperty("file.separator");
		for (int j=0,i=0; i<files.length; i++) {
			File file = files[i];
			if (isFileWithSuffix(file,suffix)) {
				list.add(file);
			} else
			if (inspectSubdirs && file.isDirectory()) {
				getFiles(file, suffix, list, inspectSubdirs);
			}
		}
	}

	private static boolean isFileWithSuffix(File file,String suffix) {
		return file.isFile() && file.getName().endsWith(suffix);
	}

	private static void log(String s) {
		System.out.println(s);
	}

	static final private void printDetails(Throwable e) {
		System.err.println("##..-" + thread() + " Exception StackTrace");
		e.printStackTrace(System.out);
	}

	static final private String thread() {
		final Thread t = Thread.currentThread();
		return t.getName();
	}

}