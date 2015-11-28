package org.mahu.proto.jnitest;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mahu.proto.jnitest.nativewrapper.DataClass;
import org.mahu.proto.jnitest.nativewrapper.DataClass1;
import org.mahu.proto.jnitest.nativewrapper.HelloJNI;
import org.mahu.proto.jnitest.nativewrapper.NarSystem;

public class JniTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	/**
	 * Test helloworld
	 */
	@Test
	public void testPrintHelloCanBeCalled() {
		NarSystem.loadLibrary();
		HelloJNI app = new HelloJNI();
		app.printHello();
	}

	/**
	 * Test returned string is "hello" + inputstring
	 */
	@Test
	public void testHelloPrefixWorks() {
		String martien = " martien";
		NarSystem.loadLibrary();
		HelloJNI app = new HelloJNI();
		String str = app.addHelloPrefix(martien);
		String expected = "Hello" + martien;
		Assert.assertEquals(expected, str);
	}

	/**
	 * Test error: if null in nothing is returned
	 */
	@Test
	public void testHelloPrefixWorksWithNullAsInput() {
		NarSystem.loadLibrary();
		HelloJNI app = new HelloJNI();
		String str = app.addHelloPrefix(null);
		String expected = null;
		Assert.assertEquals(expected, str);
	}

	/**
	 * Test primitve value in and returned
	 */
	@Test
	public void testPrimitivesInOut() {
		NarSystem.loadLibrary();
		HelloJNI app = new HelloJNI();
		int result = app.addValues(1, 2);
		Assert.assertEquals(3, result);
	}

	/**
	 * Test passing in an array. result shall be length + 1 with same values as
	 * input +1
	 */
	@Test
	public void testByteArrayInOut() {
		NarSystem.loadLibrary();
		HelloJNI app = new HelloJNI();
		byte[] input = new byte[] { 0, 1 };
		byte[] result = app.processBytes(input);
		Assert.assertEquals(input.length + 1, result.length);
		for (byte i = 0; i < result.length; i++) {
			Assert.assertEquals("idx=" + i + " expected: " + (i + 3)
					+ " found: " + result[i], i + 3, result[i]);
		}
	}

	/**
	 * Test error case, null input results in null
	 */
	@Test
	public void testByteArrayInNull() {
		NarSystem.loadLibrary();
		HelloJNI app = new HelloJNI();
		byte[] input = null;
		byte[] result = app.processBytes(input);
		Assert.assertEquals(null, result);
	}

	/**
	 * Test error case, invalid input results in null
	 */
	@Test
	public void testByteArrayInTooLarge() {
		NarSystem.loadLibrary();
		HelloJNI app = new HelloJNI();
		byte[] input = new byte[20*1000*1000+1];
		byte[] result = app.processBytes(input);
		Assert.assertEquals(null, result);
	}

	/**
	 * Test which explores passing a class as argument, which can be populated
	 * by native code.
	 */
	@Test
	public void testProcessDataClass() {
		NarSystem.loadLibrary();
		DataClass dc = new DataClass();
		int intValue = 10;
		int nrOfBytes = 15;
		String str = "Hello";
		float f = 1.33F;
		float f3 = f*3;
		double d = Double.MAX_VALUE/2;
		dc.setIntValue(intValue);
		dc.setStringValue(str);
		dc.setByteArrayValue(new byte[nrOfBytes]);
		dc.setFloatValue(f);
		dc.setDoubleValue(d);
		HelloJNI app = new HelloJNI();
		app.processDataClass(dc);
		Assert.assertEquals(intValue * 2, dc.getIntValue());
		Assert.assertEquals(str+"-jni", dc.getStringValue());
		Assert.assertEquals(nrOfBytes*2, dc.getByteArrayValue().length);
		Assert.assertTrue("returned value: "+dc.getFloatValue(), f3==dc.getFloatValue());
		Assert.assertTrue("returned value: "+dc.getDoubleValue(), (d/2)==dc.getDoubleValue());
	}

	/**
	 * Test DataClass is returned
	 */
	@Test
	public void testReturnDataClass() {
		NarSystem.loadLibrary();
		HelloJNI app = new HelloJNI();
		DataClass dc = app.returnDataClass();
		Assert.assertNotNull(dc);
		Assert.assertEquals(100, dc.getIntValue());
	}

	/**
	 * Test which explores passing a class as argument, which can be populated
	 * by native code. Native code will access public fields
	 */
	@Test
	public void testProcessDataClass1() {
		NarSystem.loadLibrary();
		int intValue = 10;
		int nrOfBytes = 15;
		String str = "Hello";
		float f = 1.33F;
		float f3 = f*3;
		double d = Double.MAX_VALUE/2;
		DataClass1 dc = new DataClass1();
		dc.intValue = intValue;
		dc.stringValue = str;
		dc.byteArrayValue = new byte[nrOfBytes];
		dc.floatValue = f;
		dc.doubleValue = d;
		dc.booleanValue = false;
		HelloJNI app = new HelloJNI();
		app.processDataClass1(dc);
		Assert.assertEquals(intValue * 2, dc.intValue);
		//Assert.assertEquals(str+"-jni", dc.stringValue);
		Assert.assertEquals(nrOfBytes*2, dc.byteArrayValue.length);
		//Assert.assertTrue("returned value: "+dc.floatValue, f3==dc.floatValue);
		Assert.assertTrue("returned value: "+dc.doubleValue, (d/2)==dc.doubleValue);
		Assert.assertTrue("returned value: "+dc.booleanValue, true==dc.booleanValue);
	}

	
}
