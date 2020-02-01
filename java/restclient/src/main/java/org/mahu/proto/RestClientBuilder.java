package org.mahu.proto;

final class RestClientBuilder implements IRestClientBuilder {

	@Override
	public <T> ITypedRestClientBuilder<T> create(Service<T> a) {
		return new TypedRestClientBuilder<T>().create(a);
	}

}
