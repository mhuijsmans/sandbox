package org.mahu.proto.lifecycle.example2;

import java.lang.Thread.UncaughtExceptionHandler;

import org.mahu.proto.lifecycle.IEventBus;
import org.mahu.proto.lifecycle.IPublicServiceKeyFactory;
import org.mahu.proto.lifecycle.IThreadFactoryFactory;
import org.mahu.proto.lifecycle.impl.AbstractServiceModule;
import org.mahu.proto.lifecycle.impl.EventBusService;
import org.mahu.proto.lifecycle.impl.PublicServiceKeyFactory;
import org.mahu.proto.lifecycle.impl.RequestProxyDispatchService;
import org.mahu.proto.lifecycle.impl.ThreadFactoryFactory;

import com.google.inject.Scopes;

public class ModuleBindings2 extends AbstractServiceModule {
    
    public ModuleBindings2() {
        super("org.mahu.proto");
    }

    @Override
    protected void configure() {
        bindServiceListener();        
        
        bind(UncaughtExceptionHandler.class).to(UncaughtExceptionInMemoryLog.class).in(Scopes.SINGLETON);      
        bind(IThreadFactoryFactory.class).to(ThreadFactoryFactory.class).in(Scopes.SINGLETON);
        bind(IPublicServiceKeyFactory.class).to(PublicServiceKeyFactory.class).in(Scopes.SINGLETON);
        // Services
        bindService(RequestProxyDispatchService.class).in(Scopes.SINGLETON);
        bindService(IEventBus.class).to(EventBusService.class).in(Scopes.SINGLETON);
        bindService(RequestService.class).in(Scopes.SINGLETON);
        bindService(ErrorService.class).in(Scopes.SINGLETON);
    }
}
