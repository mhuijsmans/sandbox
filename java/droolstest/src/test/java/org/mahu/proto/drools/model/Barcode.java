package org.mahu.proto.drools.model;

public class Barcode {
	private String value;
	private final BarcodeType type;

	public Barcode(final String value, final BarcodeType type) {
		this.value = value;
		this.type = type;
	}
	
	public void setValue(final String newValue) {
		value = newValue;
	}

	public String getValue() {
		return value;
	}
	
	public boolean startsWidth(final String prefix) {
		return value.startsWith(prefix);
	}

	public BarcodeType getType() {
		return type;
	}

}
