package org.mahu.proto.jnitest;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mahu.proto.jnitest.nativewrapper.HelloJNI;
import org.mahu.proto.jnitest.nativewrapper.NarSystem;

public class AppTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Before
	public void init() {
		LibraryUtil u = new LibraryUtil();
		u.copyLibrary("cpp","libtesthelper.so");
	}

	@Test
	public void testAction() {
		NarSystem.loadLibrary();
		HelloJNI app = new HelloJNI();
		int result = app.action(1);
		assertEquals(2,result);
	}

}
