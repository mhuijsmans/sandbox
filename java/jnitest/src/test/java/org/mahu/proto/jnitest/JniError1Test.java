package org.mahu.proto.jnitest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mahu.proto.jnitest.nativewrapper.HelloJNI;
import org.mahu.proto.jnitest.nativewrapper.NarSystem;

public class JniError1Test {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	// Testing different error cases.
	static int JNI_ERROR_UNKNOWN_CLASS = 0;
	static int JNI_ERROR_UNKNOWN_METHOD = 1;
	static int JNI_ERROR_UNKNOWN_FIELD = 2;
	static int JNI_ERROR_INVALID_CLASS_AS_ARGUMENT = 3;
	static int JNI_ERROR_THROW_CPP_EXCEPTION = 4;
	static int JNI_ERROR_CATCH_THROWN_CPP_EXCEPTION = 5;
	static int JNI_ERROR_CATCH_THROWN_JAVA_EXCEPTION = 6;
	static int JNI_ERROR_CATCH_THROWN_JAVA_EXCEPTION_CPP_EXCEPTION = 7;
	static int JNI_ERROR_CATCH_THROWN_JAVA_EXCEPTION_CPP_MYEXCEPTION = 8;
	static int JNI_ERROR_CATCH_THROWN_JAVA_EXCEPTION_SEGV = 9;
	//
	static int JNI_ERROR_UNKNOWN_CLASS_CAUGTH = 10;
	//
	static int JNI_ASSERT_NULL_CLASSNAME = 11;
	
	@Before
	public void init() {
		NarSystem.loadLibrary();
	}

	@Test
	public void testJniErrorUnknownClass() {
		// preparation
		HelloJNI app = new HelloJNI();
		// test
		exception.expect(java.lang.NoClassDefFoundError.class);
		exception.expectMessage("org/unknown/notExistingClass");
		app.testErrorCase1(JNI_ERROR_UNKNOWN_CLASS);
	}

	@Test
	public void testJniErrorUnknownMethod() {
		// preparation
		HelloJNI app = new HelloJNI();
		// test
		exception.expect(java.lang.NoSuchMethodError.class);
		exception.expectMessage("someUnknownMethod");
		app.testErrorCase1(JNI_ERROR_UNKNOWN_METHOD);
	}

	@Test
	public void testJniErrorUnknownField() {
		// preparation
		HelloJNI app = new HelloJNI();
		// test
		exception.expect(java.lang.NoSuchFieldError.class);
		exception.expectMessage("unknownField");
		app.testErrorCase1(JNI_ERROR_UNKNOWN_FIELD);
	}

	// There is a bug in openJDK 1.7.0_60
	// From JNI I can set the wrong class.
	// setStringValue, s.class: org.mahu.proto.jnitest.nativewrapper.DataClass
	@Test
	public void testJniErrorInvalidClassAsArgument() {
		// preparation
		HelloJNI app = new HelloJNI();
		// test (not bug)
		exception.expect(java.lang.NullPointerException.class);
		app.testErrorCase1(JNI_ERROR_INVALID_CLASS_AS_ARGUMENT);
	}

	@Test
	public void testJniErrorUnknownClassCaugth() {
		// preparation
		HelloJNI app = new HelloJNI();
		// test: try catch in C++ doesn't work
		exception.expect(java.lang.NoClassDefFoundError.class);
		exception.expectMessage("org/unknown/notExistingClass");
		app.testErrorCase1(JNI_ERROR_UNKNOWN_CLASS_CAUGTH);
	}

	// The test (C++ exception thrown) below results in a core dump on JDK 1.7
	// @Test
	public void testJniThrowCppException() {
		// preparation
		HelloJNI app = new HelloJNI();
		// test: try catch in C++ doesn't work
		exception.expect(java.lang.NoClassDefFoundError.class);
		exception.expectMessage("org/unknown/notExistingClass");
		app.testErrorCase1(JNI_ERROR_THROW_CPP_EXCEPTION);
	}

	@Test
	public void testJniCatchThrowCppException() {
		// preparation
		HelloJNI app = new HelloJNI();
		// test:
		int result = app.testErrorCase1(JNI_ERROR_CATCH_THROWN_CPP_EXCEPTION);
		Assert.assertEquals(1, result);
	}

	@Test
	public void testJniCatchThrowJavaException() {
		// preparation
		HelloJNI app = new HelloJNI();
		// test:
		try {
			int result = app
					.testErrorCase1(JNI_ERROR_CATCH_THROWN_JAVA_EXCEPTION);
			Assert.assertEquals(0, result);
		} catch (RuntimeException e) {
			e.printStackTrace();
			Assert.assertEquals("c++ error message", e.getMessage());
		}
	}
	
	@Test
	public void testJniCatchThrownJavaCppException() {
		// preparation
		HelloJNI app = new HelloJNI();
		// test
		exception.expect(java.lang.RuntimeException.class);
		exception.expectMessage("cpp exception");
		app.testErrorCase1(JNI_ERROR_CATCH_THROWN_JAVA_EXCEPTION_CPP_EXCEPTION);
	}
	
	@Test
	public void testJniCatchThrownJavaCppMyException() {
		// preparation
		HelloJNI app = new HelloJNI();
		// test
		exception.expect(java.lang.RuntimeException.class);
		exception.expectMessage("cpp MyException");
		app.testErrorCase1(JNI_ERROR_CATCH_THROWN_JAVA_EXCEPTION_CPP_MYEXCEPTION);
	}	
	
	// Next fails causes memory dump with SEGV. Attempt to write to p[0], where p=0.
	//@Test
	public void testJniCatchThrownJavaSegv() {
		// preparation
		HelloJNI app = new HelloJNI();
		// test
		exception.expect(java.lang.RuntimeException.class);
		exception.expectMessage("unknown");
		app.testErrorCase1(JNI_ERROR_CATCH_THROWN_JAVA_EXCEPTION_SEGV);
	}	
	
	@Test
	public void testJniAssertClassNameIsNull() {
		// preparation
		NarSystem.loadLibrary();
		HelloJNI app = new HelloJNI();
		// test
		exception.expect(java.lang.RuntimeException.class);
		exception.expectMessage("JavaClass::JavaClass, jclass is null");
		app.testErrorCase1(JNI_ASSERT_NULL_CLASSNAME);
	}	
		
}
