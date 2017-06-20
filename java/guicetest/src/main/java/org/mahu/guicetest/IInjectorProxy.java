package org.mahu.guicetest;

public interface IInjectorProxy {

    public <T> T getInstance(Class<T> clazz);

}
