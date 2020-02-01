package org.mahu.proto;

enum HttpStatus{
	HTTP_OK(200), HTTP_NO_CONTENT(204);
	
	private final int value;
	
	HttpStatus(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
	
}

