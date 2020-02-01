package org.mahu.proto.jersey.proxy.statemachine;

public class NotAllowedinCurrentStateException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	NotAllowedinCurrentStateException(String msg) {
		super(msg);
	}

}
