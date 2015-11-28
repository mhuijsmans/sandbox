package org.mahu.proto.jnitest.nativewrapper;

/**
 * Class uses to expose to JNI and allow setting of data
 */
public class DataClass {

	private int intValue = 0;
	private String stringValue = "";
	private byte[] byteArrayValue = null;
	// float: Uses 4 bytes
	// double: Uses 8 bytes
	private float floatValue = 0;
	private boolean booleanValue = true;
	private double doubleValue = 0;

	public void setIntValue(final int i) {
		this.intValue = i;
	}

	public void setStringValue(final String s) {
		// Do noth remove the print statements, there are relevant for testcases
		// (see AppTest)
		if (s != null) {
			String className = s.getClass().getName();
			if (className.equals(DataClass.class.getName())) {
				// this will cause a NPE
				System.out.println("setStringValue, s: " + s);
			}
		}
		// if (s!=null) {
		// System.out.println(
		// "setStringValue, s.class: "+s.getClass().getName() );
		// System.out.println( "setStringValue, s: "+s );
		// } else {
		// System.out.println( "setStringValue, string isNull");
		// }
		this.stringValue = s;
	}

	public void setByteArrayValue(final byte[] byteArrayValue) {
		this.byteArrayValue = byteArrayValue;
	}

	public void setFloatValue(final float i) {
		this.floatValue = i;
	}
	
	public void setDoubleValue(final double i) {
		this.doubleValue = i;
	}

	public int getIntValue() {
		return intValue;
	}

	public String getStringValue() {
		return stringValue;
	}

	public byte[] getByteArrayValue() {
		return byteArrayValue;
	}

	public float getFloatValue() {
		return this.floatValue;
	}
	
	public double getDoubleValue() {
		return this.doubleValue;
	}	

	public boolean getBooleanValue() {
		return booleanValue;
	}

	public void setBooleanValue(boolean booleanValue) {
		this.booleanValue = booleanValue;
	}

}
