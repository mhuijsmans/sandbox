package org.mahu.proto.forkprocesstest;

import java.util.Properties;

public class Utils {

	public static void printAllProperties() {
		Properties prop = System.getProperties();
		for (Object key : prop.keySet()) {
			System.out.println(key + "=" + System.getProperty((String) key));
		}
	}
	
	public static void printAllEnvironmentData() {
		for (String key : System.getenv().keySet()) {
			System.out.println(key + "=" + System.getenv(key));
		}
	}

	public static void printSomeProperties() {
		String[] keys = new String[] { "java.class.path", "user.name",
				"java.home", "user.dir", "os.arch", "sun.java.command" };
		for (Object key : keys) {
			System.out.println(key + "=" + System.getProperty((String) key));
		}
	}
	
	public static boolean isWindows() {
		final String os = System.getProperty("os.name").toLowerCase();
		return os.contains("windows");
	}
	
	public static boolean isLinux() {
		final String os = System.getProperty("os.name").toLowerCase();
		return os.contains("linux");
	}	

}
