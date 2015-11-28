package org.mahu.proto.logging;

public class ExampleConfigureLogging {

	// For JUL: example of the first method in a class/test suite such that the
	// logging configuration is loaded.
	static {
		ConfigureLogging.LoadConfiguration();
		// http://stackoverflow.com/questions/11359187/why-not-use-java-util-logging
		// interesting analysis of JUL.
		// http://examples.javacodegeeks.com/core-java/util/logging/java-util-logging-example/
	}
}
