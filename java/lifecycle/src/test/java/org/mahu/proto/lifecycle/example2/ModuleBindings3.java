package org.mahu.proto.lifecycle.example2;

import java.util.LinkedList;
import java.util.List;

import org.mahu.proto.lifecycle.EventBusService;
import org.mahu.proto.lifecycle.IEventBus;
import org.mahu.proto.lifecycle.IPublicServiceKeyFactory;
import org.mahu.proto.lifecycle.IThreadFactoryFactory;
import org.mahu.proto.lifecycle.impl.AbstractServiceModule;
import org.mahu.proto.lifecycle.impl.PublicServiceKeyFactory;
import org.mahu.proto.lifecycle.impl.RequestProxyDispatchService;
import org.mahu.proto.lifecycle.impl.ThreadFactoryFactory;

import com.google.inject.Scopes;

public class ModuleBindings3 extends AbstractServiceModule {

    private final List<Class<?>> singletonServices;

    public ModuleBindings3() {
        super("org.mahu.proto");
        singletonServices = new LinkedList<>();
        bindAsSingleton(RequestProxyDispatchService.class, RequestService.class, ErrorService.class);
    }

    @Override
    protected void configure() {
        bindServiceListener();

        bind(IThreadFactoryFactory.class).to(ThreadFactoryFactory.class).in(Scopes.SINGLETON);
        bind(IPublicServiceKeyFactory.class).to(PublicServiceKeyFactory.class).in(Scopes.SINGLETON);
        // Services
        bindService(IEventBus.class).to(EventBusService.class).in(Scopes.SINGLETON);
        for(Class<?> cls : singletonServices) {
            bindService(cls).in(Scopes.SINGLETON);
        }
    }

    public void bindAsSingleton(Class<?>... classes) {
        for (Class<?> cls : classes) {
            singletonServices.add(cls);
        }
    }
}
