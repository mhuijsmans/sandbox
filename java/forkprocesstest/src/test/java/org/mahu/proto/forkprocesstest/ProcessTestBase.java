package org.mahu.proto.forkprocesstest;

import static org.junit.Assert.assertEquals;

import java.util.List;

public abstract class ProcessTestBase {
	
	protected static void printOutputData(ChildProcess fork) {
		print("OutputData", fork.getOutputData());
		print("ErrorData", fork.getErrorData());
	}

	private static void print(final String msg, final List<String> lines) {
		System.out.println(msg);
		for (String s : lines) {
			System.out.println(s);
		}
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

}
