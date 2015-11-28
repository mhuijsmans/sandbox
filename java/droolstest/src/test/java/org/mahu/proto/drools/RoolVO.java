package org.mahu.proto.drools;

public class RoolVO {
	
	private String stringValue;
	private boolean booleanValue;
	
	public void setStringValue(final String aValue) {
		stringValue = aValue;
	}
	 
	public void setBooleanValue(final boolean avalue) {
		booleanValue = avalue;
	}

	public String getStringValue() {
		return stringValue;
	}

	public boolean getBooleanValue() {
		return booleanValue;
	}
	
	public String toString() {
		return "RoolVO, stringValue="+stringValue+" booleanValue="+booleanValue;
	}
	
}
