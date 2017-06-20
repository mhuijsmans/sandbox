package org.mahu.guicetest;

// RequestContext holds request specific data
public interface IRequestContext {

    <T> T get(Class<T> class1);

}
