package org.mahu.proto.genericstest;

import java.lang.reflect.ParameterizedType;

public class ClassA<T> {

	/**
	 * method will only work for classes that directly inherit from ClassA.  
	 */
	@SuppressWarnings("unchecked")
	public Class<T> getClassOfT() {
		ParameterizedType superclass = (ParameterizedType) getClass()
				.getGenericSuperclass();
		return (Class<T>) superclass.getActualTypeArguments()[0];
	}
}
