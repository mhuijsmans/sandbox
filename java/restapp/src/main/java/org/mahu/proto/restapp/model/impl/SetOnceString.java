package org.mahu.proto.restapp.model.impl;

import static org.mahu.proto.restapp.model.impl.ModelAssert.checkArgumentLengtGtZero;
import static org.mahu.proto.restapp.model.impl.ModelAssert.checkState;

public class SetOnceString {
	
	private String string; 
	
	public SetOnceString(){
		// empty
	}

	public void set (final String name) throws ProcessBuilderException {
		checkArgumentLengtGtZero(name);
		checkState(string == null);
		string = name;
	}
	
	public String getValue() {
		return string;
	}
	
	public String toString() {
		return string;
	}
}
