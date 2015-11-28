package org.mahu.proto.jnitest;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mahu.proto.jnitest.nativewrapper.HelloJNI;
import org.mahu.proto.jnitest.nativewrapper.NarSystem;

public class JniOnLoadTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Before
	public void init() {
		NarSystem.loadLibrary();
	}

	@Test
	public void testSayHiWithString() {
		HelloJNI app = new HelloJNI();
		int result = app.sayHi("hello",3);
		assertEquals(3,result);
	}
	
	@Test
	public void testSayHiNoString() {
		HelloJNI app = new HelloJNI();
		int result = app.sayHi(null,3);
		assertEquals(0,result);
	}	

}
