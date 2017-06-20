package org.mahu.guicetest;

public class RequestContextImpl implements IRequestContext {

    @Override
    public <T> T get(Class<T> class1) {
        try {
            return class1.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            return null;
        }
    }

}
