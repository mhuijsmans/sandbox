package org.mahu.proto;

public class TypedRestClientBuilder<T> implements ITypedRestClientBuilder<T> {
	
	private Service service;
	
	private class TypedRestClientBuilderExceptionCaught<T> implements ITypedRestClientBuilderExceptionCaught<T> {

		@Override
		public Result<T> get() {
			return null;
		}
		
	}

	@Override
	public TypedRestClientBuilder<T> create(Service<T> a) {
		service = a;
		return this;
	}

	@Override
	public T get() {
		return null;
	}

	@Override
	public ITypedRestClientBuilderExceptionCaught<T> catchExceptions() {
		return new TypedRestClientBuilderExceptionCaught<T>();
	}

	@Override
	public void delete() {
	}

	@Override
	public T post(Object requestBody) {
		return null;
	}

	@Override
	public T post() {
		return null;
	}

}
