package org.mahu.proto;

interface IRestClientBuilder {

	<T> ITypedRestClientBuilder<T> create(Service<T> a);

}
