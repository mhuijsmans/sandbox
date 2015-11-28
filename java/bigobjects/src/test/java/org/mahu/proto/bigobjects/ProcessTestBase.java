package org.mahu.proto.bigobjects;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.mahu.proto.forkprocesstest.ChildProcess;

public abstract class ProcessTestBase {

	protected static void printOutputAndErrorData(ChildProcess fork) {
		printOutputData(fork);
		printErrorData(fork);
	}

	protected static void printOutputData(ChildProcess fork) {
		print("OutputData", fork.getOutputData());
	}

	protected static void printErrorData(ChildProcess fork) {
		print("ErrorData", fork.getErrorData());
	}

	protected static void print(List<String> data) {
		int l = data.size();
		int i = 0;
		while (i < l) {
			System.out.println(data.get(i++));
		}
	}

	protected static void print(final String msg, final List<String> lines) {
		System.out.println(msg);
		print(lines);
	}

	protected static void printStrings(final String[] HI) {
		for (String s : HI) {
			System.out.println(s);
		}
	}

	protected static void assertOutputData(final ChildProcess fork,
			final String[] HI) {
		assertEquals(HI.length, fork.getOutputData().size());
		int i = 0;
		for (String s : HI) {
			assertEquals(s, fork.getOutputData().get(i++));
		}
	}
	
	protected static void printLibraryPath() {
		String currentLibraryPath = getCurrentLibraryPath();
		System.out.println("java.library.path=" + currentLibraryPath);
	}
	
	protected static String getCurrentLibraryPath() {
		return System.getProperty("java.library.path");
	}	

	// copied from:
	// http://viralpatel.net/blogs/getting-jvm-heap-size-used-memory-total-memory-using-java-runtime/
	protected void printMemoryInfo() {
		int mb = 1024 * 1024;

		// Getting the runtime reference from system
		Runtime runtime = Runtime.getRuntime();

		System.out.println("##### Heap utilization statistics [MB] #####");

		// Print used memory
		System.out.println("Used Memory :"
				+ (runtime.totalMemory() - runtime.freeMemory()) / mb + " mb");

		// Print free memory
		System.out.println("Free Memory :" + runtime.freeMemory() / mb + " mb");

		// Print total available memory
		System.out
				.println("Total Memory:" + runtime.totalMemory() / mb + " mb");

		// Print Maximum available memory
		System.out.println("Max Memory  :" + runtime.maxMemory() / mb + " mb");
	}

}
