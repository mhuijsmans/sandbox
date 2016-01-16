package org.mahu.proto.lifecycle.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.mahu.proto.lifecycle.ILifeCycleService;
import org.mahu.proto.lifecycle.IPublicService;
import org.mahu.proto.lifecycle.PublicServiceKey;

/**
 * The ObjectRegistry keeps track of objects created by IOC.
 */
public class ObjectRegistry {

    private final List<Object> objects = new ArrayList<>();
    private final List<ILifeCycleService> services = new LinkedList<>();
    private final List<IPublicService<?>> publicServices = new ArrayList<>();

    public void objectCreated(final Object createdObject) {
        objects.add(createdObject);
        if (createdObject instanceof ILifeCycleService) {
            services.add(ILifeCycleService.class.cast(createdObject));
        } 
        if (createdObject instanceof IPublicService) {
            publicServices.add(IPublicService.class.cast(createdObject));
        }        
    }

    public int getObjectCount() {
        return objects.size();
    }

    public Object getObject(final int i) {
        return objects.get(i);
    }

    public int getLifeCycleServiceCount() {
        return services.size();
    }

    public Iterator<ILifeCycleService> getLifeCycleServiceIterator() {
        return services.iterator();
    }

    public List<PublicServiceKey<?>> getPublicServiceKeys() {
        final List<PublicServiceKey<?>> services = new LinkedList<>();
        for (IPublicService<?> publicService : publicServices) {
            services.add(publicService.getPublicServiceKey());
        }
        return services;
    }

    public <T> Optional<T> getInstance(Class<T> cls) {
        for(Object o : objects) {
            if (cls.isInstance(o)) {
                return Optional.of(cls.cast(o));
            }
        }
        return Optional.empty();
    }
}
