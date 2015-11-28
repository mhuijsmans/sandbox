package org.mahu.proto.genericstest;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class ClassB<S,T> {
	
	/**
	 * method will work for classes that directly or indirectly inherit from ClassA.  
	 */	
	@SuppressWarnings("unchecked")
	public Class<S> getClassOfS() {
		return  (Class<S>)getParameterizedType(0);
	}	

	/**
	 * method will work for classes that directly or indirectly inherit from ClassA.  
	 */		
	@SuppressWarnings("unchecked")
	public Class<T> getClassOfT() {
		return  (Class<T>)getParameterizedType(1);
	}

	public Type getParameterizedType(final int i) {
	    Class<?> superClass = getClass(); 
	    Type superType;
	    do {
	        superType = superClass.getGenericSuperclass();
	        superClass = extractClassFromType(superType);
	    } while (! (superClass.equals(ClassB.class)));

	    Type actualArg = ((ParameterizedType)superType).getActualTypeArguments()[i];
	    return actualArg;
	}	
	
	
	private Class<?> extractClassFromType(final Type t) {
	    if (t instanceof Class<?>) {
	        return (Class<?>)t;
	    }
	    return (Class<?>)((ParameterizedType)t).getRawType();
	}	
}

