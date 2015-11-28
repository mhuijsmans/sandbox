package org.mahu.proto.jnitest;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mahu.proto.jnitest.nativewrapper.HelloJNI;
import org.mahu.proto.jnitest.nativewrapper.NarSystem;

public class JniError2Test {
	
	private final static String DATACLASSNAME = "org/mahu/proto/jnitest/nativewrapper/DataClass"; 
	private final static String DATACLASS_METHOD_GETSTRINGVALUE = "getStringValue";

	@Rule
	public ExpectedException exception = ExpectedException.none();

	// Testing different error cases.
	static int JNI_CALL_METHOD = 0;
	static int JNI_CALL_FIELD = 1;
	
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
		app.testErrorCase2(JNI_CALL_METHOD, "org/unknown/notExistingClass", "no-method", "no-signature", "no-object-to-assign");
	}
	
	@Test
	public void testJniErrorUnknownMethod() {
		// preparation
		HelloJNI app = new HelloJNI();
		String noMethod = "no-method";
		// test
		exception.expect(java.lang.NoSuchMethodError.class);
		exception.expectMessage(noMethod);		
		app.testErrorCase2(JNI_CALL_METHOD, DATACLASSNAME, noMethod, "no-signature", "no-object-to-assign");
	}
	
	@Test
	public void testJniMethodFound() {
		// preparation
		HelloJNI app = new HelloJNI();
		// test		
		int result = app.testErrorCase2(JNI_CALL_METHOD, DATACLASSNAME, DATACLASS_METHOD_GETSTRINGVALUE, "()Ljava/lang/String;", "no-object-to-assign");
		assertEquals(0,result);
	}		
	
}
