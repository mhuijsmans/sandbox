package org.mahu.proto.jnitest;

import org.junit.Assert;
import org.junit.Test;
import org.mahu.proto.commons.Chrono;
import org.mahu.proto.jnitest.nativewrapper.DataClass;
import org.mahu.proto.jnitest.nativewrapper.DataClass1;
import org.mahu.proto.jnitest.nativewrapper.HelloJNI;
import org.mahu.proto.jnitest.nativewrapper.NarSystem;

public class BigLoopTest {

	final static int MAX = 10000;
	@Test
	public void testProcessDataClass() {
		NarSystem.loadLibrary();
		Chrono chrono= new Chrono();
		for (int i = 0; i < MAX; i++) {
			callNativeWithDataClass();
		}
		printResults("DataClass", chrono);
	}
	
	@Test
	public void testProcessDataClass1() {
		NarSystem.loadLibrary();
		Chrono chrono= new Chrono();
		for (int i = 0; i < MAX; i++) {
			callNativeWithDataClass1();
		}
		printResults("DataClass1", chrono);
	}
	
	@Test
	public void testProcessBytes() {
		NarSystem.loadLibrary();
		final int MAX = 10000;
		int nrOfBytes = 100*1000;
		Chrono chrono= new Chrono();
		for (int i = 0; i < MAX; i++) {
			byte[]b = new byte[nrOfBytes];
			HelloJNI app = new HelloJNI();
			byte[] bb = app.processBytes(b);
			Assert.assertEquals(nrOfBytes+1, bb.length);
		}
		printResults("DataClass1", chrono);
	}

	private void callNativeWithDataClass() {
		DataClass dc = new DataClass();
		int intValue = 10;
		int nrOfBytes = 15;
		String str = "Hello";
		float f = 1.33F;
		float f3 = f * 3;
		dc.setBooleanValue(false);
		dc.setIntValue(intValue);
		dc.setStringValue(str);
		dc.setByteArrayValue(new byte[nrOfBytes]);
		dc.setFloatValue(f);
		HelloJNI app = new HelloJNI();
		app.processDataClass(dc);
		Assert.assertEquals(true, dc.getBooleanValue());
		Assert.assertEquals(intValue * 2, dc.getIntValue());
		Assert.assertEquals(str + "-jni", dc.getStringValue());
		Assert.assertEquals(nrOfBytes * 2, dc.getByteArrayValue().length);
		Assert.assertTrue("returned value: " + dc.getFloatValue(),
				f3 == dc.getFloatValue());
	}

	private void printResults(String msg, Chrono chrono) {
		long elapsedTime = chrono.elapsedMs();
		long avg = ((elapsedTime+MAX-1)/MAX);
		System.out.println("Call native with "+msg+", max="+MAX+" elapsed time(ms)="+elapsedTime+" avg(ms)="+avg);
	}

	private void callNativeWithDataClass1() {
		int intValue = 10;
		int nrOfBytes = 15;
		String str = "Hello";
		float f = 1.33F;
		float f3 = f * 3;
		DataClass1 dc = new DataClass1();
		dc.booleanValue = false;
		dc.intValue = intValue;
		dc.stringValue = str;
		dc.byteArrayValue = new byte[nrOfBytes];
		dc.floatValue = f;
		HelloJNI app = new HelloJNI();
		app.processDataClass1(dc);
		Assert.assertEquals(true, dc.booleanValue);		
		Assert.assertEquals(intValue * 2, dc.intValue);
		Assert.assertEquals(str + "-jni", dc.stringValue);
		Assert.assertEquals(nrOfBytes * 2, dc.byteArrayValue.length);
		Assert.assertTrue("returned value: " + dc.floatValue,
				f3 == dc.floatValue);
	}

}
