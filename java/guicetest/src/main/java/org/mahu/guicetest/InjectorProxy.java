package org.mahu.guicetest;

import com.google.inject.Injector;

public class InjectorProxy implements IInjectorProxy {

    private Injector injector;

    public InjectorProxy() {
        System.out.println("InjectorProxy ctor");
    }

    public void set(Injector injector) {
        this.injector = injector;
    }

    @Override
    public <T> T getInstance(Class<T> clazz) {
        return injector.getInstance(clazz);
    }

}
