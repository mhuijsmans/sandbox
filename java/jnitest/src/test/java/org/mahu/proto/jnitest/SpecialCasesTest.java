package org.mahu.proto.jnitest;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mahu.proto.commons.Chrono;
import org.mahu.proto.jnitest.nativewrapper.HelloJNI;
import org.mahu.proto.jnitest.nativewrapper.NarSystem;

public class SpecialCasesTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	// Testing different error cases.
	static int JNI_SPECIAL_CASE_CALL_OBJECT_METHOD = 0;
	static int JNI_SPECIAL_CASE_CALL_OBJECT_FIELD = 1;
	static int JNI_SPECIAL_CASE_MYCLASS_GET_FIELD_INT = 2;
	static int JNI_SPECIAL_CASE_MYCLASS_GET_METHOD_INT = 3;
	static int JNI_SPECIAL_CASE_GET_SIZEOF_VOID_POINTER = 4;
	static int JNI_SPECIAL_CASE_GET_SIZEOF_JLONG = 5;
	static int JNI_SPECIAL_CASE_LOAD_CLASS = 6;
	static int JNI_SPECIAL_CASE_SEGV = 7; // class SegvTest
	static int JNI_SPECIAL_CASE_PRINT_OBJECTREF = 8;
	static int JNI_SPECIAL_CASE_BIG_ARRAY_READ = 9;

	@Before
	public void init() {
		NarSystem.loadLibrary();
	}

	@Test
	public void testThatICanCallMethodThatReturnsObject() {
		// preparation
		HelloJNI app = new HelloJNI();
		// test
		int result = app.testSpecial(JNI_SPECIAL_CASE_CALL_OBJECT_METHOD);
		assertEquals(0, result);
	}

	@Test
	public void testThatICanCallFieldThatReturnsObject() {
		// preparation
		HelloJNI app = new HelloJNI();
		// test
		int result = app.testSpecial(JNI_SPECIAL_CASE_CALL_OBJECT_FIELD);
		assertEquals(0, result);
	}

	@Test
	public void testMyClassGetFieldInt() {
		// preparation
		int val = 5;
		HelloJNI app = new HelloJNI();
		app.test = val;
		// test
		int result = app.testSpecial(JNI_SPECIAL_CASE_MYCLASS_GET_FIELD_INT);
		assertEquals(val + 10, result);
		assertEquals(val + 10, app.test);
	}

	@Test
	public void testMyClassGetMethodInt() {
		// preparation
		int val = 8;
		HelloJNI app = new HelloJNI();
		app.test = val;
		// test
		int result = app.testSpecial(JNI_SPECIAL_CASE_MYCLASS_GET_METHOD_INT);
		assertEquals(val + 10, result);
		assertEquals(val + 10, app.test);
	}

	// ref:
	// http://stackoverflow.com/questions/3095497/jni-and-using-c-newed-objects-in-java
	@Test
	public void testSizeOfVoidPointer() {
		// preparation
		HelloJNI app = new HelloJNI();
		// test
		int result = app.testSpecial(JNI_SPECIAL_CASE_GET_SIZEOF_VOID_POINTER);
		assertEquals(8, result);
	}

	@Test
	public void testSizeOfJLong() {
		// preparation
		HelloJNI app = new HelloJNI();
		// test
		int result = app.testSpecial(JNI_SPECIAL_CASE_GET_SIZEOF_JLONG);
		assertEquals(8, result);
	}

	@Test
	public void testLoadClass() {
		// preparation
		HelloJNI app = new HelloJNI();
		// test that result in loading of a class by the current thread
		// classloader
		int result = app.testSpecial(JNI_SPECIAL_CASE_LOAD_CLASS);
		assertEquals(99, result);
	}

	@Test
	public void testPrintObjectReferences() {
		// preparation
		HelloJNI app = new HelloJNI();
		// Print object references
		int result = app.testSpecial(JNI_SPECIAL_CASE_PRINT_OBJECTREF);
		assertEquals(1, result);
	}

	@Test
	public void testBigArrayRead() {
		// preparation
		HelloJNI app = new HelloJNI();
		// Print object references
		Chrono chrono = new Chrono();
		app.specialBytes = new byte[25 * 1000 * 1000];
		int MAX = 50;
		for (int i = 0; i < MAX; i++) {
			int result = app.testSpecial(JNI_SPECIAL_CASE_BIG_ARRAY_READ);
			assertEquals(1, result);
		}
		System.out.println("Elapsed & avg time (ms): " + chrono.elapsedAndAvg(MAX));
	}

}
