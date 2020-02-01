package org.mahu.proto.jersey.lifecycle;

public interface IRequestPreProcessor {

	<T> T getInstance(Class<T> clazz);

}
