package org.mahu.proto;

public interface ITypedRestClientBuilder<T> {

	default ITypedRestClientBuilder<T> create(Service<T> a) {
		TypedRestClientBuilder<T> create = new TypedRestClientBuilder<T>().create(a);
		return create;
	}

	T get();

	ITypedRestClientBuilderExceptionCaught<T> catchExceptions();

	void delete();

	T post(Object requestBody);

	T post();
}
