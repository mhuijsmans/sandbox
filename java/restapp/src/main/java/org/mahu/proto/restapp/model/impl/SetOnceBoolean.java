package org.mahu.proto.restapp.model.impl;

import static org.mahu.proto.restapp.model.impl.ModelAssert.checkState;

public class SetOnceBoolean {
	
	private enum BOOL_VALUE {
		NOT_SET, TRUE, FALSE
	}
	
	private BOOL_VALUE bool;
	
	public SetOnceBoolean(){
		// empty
	}

	public void set (final boolean value) throws ProcessBuilderException {
		checkState(bool == BOOL_VALUE.NOT_SET);
		bool = value ? BOOL_VALUE.TRUE : BOOL_VALUE.FALSE;
	}
	
	public boolean getValue() {
		return bool == BOOL_VALUE.TRUE;
	}
	
	public String toString() {
		return Boolean.toString(getValue());
	}
}
