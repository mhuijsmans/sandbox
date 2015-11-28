package org.mahu.proto.javaprop;

import java.util.Arrays;
import java.util.Map;
import java.util.Properties;

public class App {
	public static void main(String[] args) {
		printProperties();
	}

	public static void printProperties() {
		Properties prop = System.getProperties();
		Object[] array = prop.keySet().toArray();
		Arrays.sort(array);
		for (Object key : array) {
			System.out.println(key + " = " + prop.getProperty((String) key));
		}
	}

	public static void printEnvironment() {
		Map<String, String> env = System.getenv();
		for (Object oo : env.keySet()) {
			System.out.println(oo + " =" + env.get((String) oo));
		}
	}
}
