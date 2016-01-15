package org.mahu.proto.lifecycle.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.mahu.proto.lifecycle.IApiBroker;
import org.mahu.proto.lifecycle.IApiRegistry;
import org.mahu.proto.lifecycle.PublicServiceKey;

public class ApiBroker implements IApiBroker, IApiRegistry {

    private HashMap<Class<?>, Object> values = new HashMap<>();

    @SuppressWarnings("unchecked")
    @Override
    public <T> Optional<T> resolve(final Class<T> requestedInterface) {
        return Optional.ofNullable((T) values.get(requestedInterface));
    }

    @Override
    public void setPublicService(final List<PublicServiceKey<?>> keys) {
        synchronized(values) {
            values.clear();
            for(PublicServiceKey<?> key : keys) {
                values.put(key.interfaceClass, key.object);
            }
        } 
    }

    @Override
    public void removeAllPublicServices() {
        synchronized(values) {
            values.clear();
        } 
    }

}
