package org.mahu.closeable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;

public class CloseableTest {

	@Test
	public void testCloseable() throws IOException {
		TestBoolean bool = new TestBoolean();
		try (TestCloseable tc = new TestCloseable(bool)) {
			tc.bla();
		}
		assertTrue(bool.getValue());
	}

	@Test
	public void testAutoCloseable() {
		TestBoolean bool = new TestBoolean();
		try (TestAutoCloseable tc = new TestAutoCloseable(bool)) {
			tc.bla();
		}
		assertTrue(bool.getValue());
	}

	@Test
	public void testAutoCloseable_assignAndEmptyBody_closeCalled() {
		TestBoolean bool = new TestBoolean();
		try (TestAutoCloseable tc = new TestAutoCloseable(bool)) {
		}
		assertTrue(bool.getValue());
	}

	@Test
	public void testAutoCloseable_noTryCallClose_closeCalledNoiException() {
		TestBoolean bool = new TestBoolean();
		TestAutoCloseable tc = new TestAutoCloseable(bool);
		tc.close();
		assertTrue(bool.getValue());
	}

	@Test
	public void testAutoCloseable_exceptionInCtor_closeCalled() {
		TestBoolean bool = new TestBoolean();
		Runnable r = createRunnableThatThrowsException();
		try {
			// exception if throw in constructor, so
			try (TestAutoCloseable tc = new TestAutoCloseable(bool, r)) {
			}
			fail("Exception is thrown");
		} catch (RuntimeException e) {

		}
		assertFalse(bool.getValue());
	}

	@Test
	public void testAutoCloseable_exceptionAfterCtorButInsisdeDeclareBlock_closeCalled() {
		TestBoolean bool = new TestBoolean();
		try {
			// exception if throw in constructor, so
			try (TestAutoCloseable tc = createTestAutoCloseableThatThrowsExceptionAfterCtor(bool)) {
			}
			fail("Exception is thrown");
		} catch (RuntimeException e) {

		}
		assertFalse(bool.getValue());
	}

	@Test
	public void testAutoCloseable_alsoFinally_firstCloseNextFinally() throws IOException {
		TestIntegerWithGlobalCounter.globalCntr = 0;
		TestIntegerWithGlobalCounter cntr1 = new TestIntegerWithGlobalCounter();
		TestIntegerWithGlobalCounter cntr2 = new TestIntegerWithGlobalCounter();
		try (TestIntegerAutoCloseable tc = new TestIntegerAutoCloseable(cntr1)) {
		} finally {
			cntr2.setTrue();
		}
		assertEquals(1, cntr1.getValue());
		assertEquals(2, cntr2.getValue());
	}

	@Test
	public void testAutoCloseable_alsoFinallyWithException_firstCloseNextFinally() throws IOException {
		TestIntegerWithGlobalCounter.globalCntr = 0;
		TestIntegerWithGlobalCounter cntr1 = new TestIntegerWithGlobalCounter();
		TestIntegerWithGlobalCounter cntr2 = new TestIntegerWithGlobalCounter();
		try {
			// exception if throw in run
			try (TestIntegerAutoCloseable tc = new TestIntegerAutoCloseable(cntr1,
					createRunnableThatThrowsException())) {
				tc.doSomeWork();
			} finally {
				cntr2.setTrue();
			}
			fail("Exception is thrown");
		} catch (RuntimeException e) {
			// expected, so ignore
		}
		assertEquals(1, cntr1.getValue());
		assertEquals(2, cntr2.getValue());
	}

	private static TestAutoCloseable createTestAutoCloseableThatThrowsExceptionAfterCtor(TestBoolean bool) {
		Runnable r = new Runnable() {

			@Override
			public void run() {
				throw new RuntimeException();
			}
		};
		TestAutoCloseable tc = new TestAutoCloseable(bool, r);
		r.run();
		return tc;
	}

	private Runnable createRunnableThatThrowsException() {
		Runnable r = new Runnable() {

			@Override
			public void run() {
				throw new RuntimeException();
			}
		};
		return r;
	}

	// next is not allowed.
	/*
	 * @Test public void testAutoCloseable_noAsignAndEmptyBody_closeCalled() {
	 * TestBoolean bool = new TestBoolean(); try (new TestAutoCloseable(bool)) { }
	 * assertTrue(bool.getValue()); }
	 */
}
