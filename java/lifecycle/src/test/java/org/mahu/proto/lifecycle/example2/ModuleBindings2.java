package org.mahu.proto.lifecycle.example2;

import org.mahu.proto.lifecycle.impl.AbstractServiceModule;
import org.mahu.proto.lifecycle.impl.IPublicServiceKeyFactory;
import org.mahu.proto.lifecycle.impl.PublicServiceKeyFactory;
import org.mahu.proto.lifecycle.impl.RequestProxyService;

import com.google.inject.Scopes;

public class ModuleBindings2 extends AbstractServiceModule {
    
    public ModuleBindings2() {
        super("org.mahu.proto");
    }

    @Override
    protected void configure() {
          
        bind(EventBusUncaughtExceptionHandler.class).in(Scopes.SINGLETON);
        bind(IPublicServiceKeyFactory.class).to(PublicServiceKeyFactory.class).in(Scopes.SINGLETON);
        bindService(RequestProxyService.class).in(Scopes.SINGLETON);
        bindService(IEventBus.class).to(EventBusService.class).in(Scopes.SINGLETON);
        bindService(RequestService.class).in(Scopes.SINGLETON);
        bindService(ErrorService.class).in(Scopes.SINGLETON);
        
        bindServiceListener();
    }
}
