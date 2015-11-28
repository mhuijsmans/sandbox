package org.mahu.proto.jnitest;

import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mahu.proto.jnitest.nativewrapper.HelloJNI;
import org.mahu.proto.jnitest.nativewrapper.NarSystem;

public class JniLoadLibFromResourceTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	// To manual test
	// @Test
	public void testThatItWorksTheNarMavenWay() {
		NarSystem.loadLibrary();
		HelloJNI app = new HelloJNI();
		int result = app.sayHi("hello");
		assertEquals(0, result);
	}

	// This test case will fail.
	// http://stackoverflow.com/questions/4691095/java-loading-dlls-by-a-relative-path-and-hide-them-inside-a-jar
	// suggest that you can load lib from inside a Jar. That does NOT work.
	// For linux, lib needs to be on the library path
	// Fow window I didn't test.
	@Test
	public void testThatItWorksLoadingFromResourcenWay() {
		System.loadLibrary("sayhello"); // no .dll or .so extension!
		HelloJNI app = new HelloJNI();
		int result = app.sayHi("hello");
		assertEquals(0, result);
	}

}
