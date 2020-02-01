package org.mahu.proto.stm;

public class NotAllowedinCurrentStateException extends RuntimeException {
	NotAllowedinCurrentStateException(String msg) {
		super(msg);
	}

}
