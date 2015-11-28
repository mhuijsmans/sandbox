package org.mahu.proto.jnitest;

import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mahu.proto.jnitest.nativewrapper.HelloJNI;
import org.mahu.proto.jnitest.nativewrapper.NarSystem;

public class SegvTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	// Testing different error cases.
	static int JNI_SPECIAL_CASE_SEGV = 7;

	@Before
	public void init() {
		NarSystem.loadLibrary();
		System.out.println(System.getProperty("java.library.path"));
	}

	@Test
	public void testSegv() {
		HelloJNI app = new HelloJNI();
		app.testSpecial(JNI_SPECIAL_CASE_SEGV);
		fail();
	}
}
