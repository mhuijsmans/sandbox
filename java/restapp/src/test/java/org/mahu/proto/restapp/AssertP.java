package org.mahu.proto.restapp;

import static org.junit.Assert.assertTrue;

import org.mahu.proto.restapp.model.Node;
import org.mahu.proto.restapp.model.ProcessTask;

/**
 * This class contains a set of Assert method that shall make writing test cases
 * easier by providing a higher abstraction level.
 */
public class AssertP {

	public static void assertNodeInstanceOf(final Node node,
			final Class<? extends ProcessTask> testCls) {
		assertTrue(node != null);
		assertTrue(testCls != null);
		Class<? extends ProcessTask> cls = node.getProcessTaskClass();
		assertTrue(cls != null);
		assertTrue(cls == testCls);
	}

}
