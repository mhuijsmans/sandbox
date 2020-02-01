package org.mahu.proto.jersey.lifecycle;

public class RequestPreProcessor implements IRequestPreProcessor {

	@Override
	public <T> T getInstance(Class<T> clazz) {
		return (T)new BootRequest();
	}

}
