package org.mahu.proto.lifecycle.impl;

import org.mahu.proto.lifecycle.IRequestProxyList;
import org.mahu.proto.lifecycle.PublicServiceKey;
import org.mahu.proto.lifecycle.example2.IEventBus;

import com.google.inject.Inject;

public class PublicServiceKeyFactory implements IPublicServiceKeyFactory {
    
    private final IEventBus eventBus;
    private final IRequestProxyList iRequestProxyList;
   
    @Inject
    public PublicServiceKeyFactory(final IEventBus eventBus, final IRequestProxyList iRequestProxyList) {
        this.eventBus = eventBus;
        this.iRequestProxyList = iRequestProxyList;
    }

    public <T> PublicServiceKey<T> createKey(Class<T> cls, T object) {
        return new PublicServiceKey<T>(cls, createProxy(object, cls));
    }
    
//    public <T> PublicServiceKey<T> createKey(PublicServiceKey<T> key) {
//        return new PublicServiceKey<T>(key.interfaceClass, createProxy(key.object, key.interfaceClass));
//    }    

    private <T> T createProxy(T object, Class<T> cls) {
        return cls.cast(java.lang.reflect.Proxy.newProxyInstance(object.getClass().getClassLoader(), new Class<?>[] {cls},
                new RequestProxy(object, eventBus, iRequestProxyList)));
    }

}