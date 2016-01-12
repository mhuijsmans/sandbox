package org.mahu.proto.lifecycle.impl;

import java.util.LinkedList;
import java.util.List;

import org.mahu.proto.lifecycle.ILifeCycleService;
import org.mahu.proto.lifecycle.IRequestProxyList;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;
import com.google.inject.binder.AnnotatedBindingBuilder;
import com.google.inject.matcher.Matchers;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

public abstract class AbstractServiceModule extends AbstractModule {

    private final ObjectRegistry objectRegistry = new ObjectRegistry();
    private List<Class<?>> lifeCycleServices = new LinkedList<>();
    private final String subPackageOf;
    
    protected AbstractServiceModule(final String subPackageOf) {
        this.subPackageOf = subPackageOf;
    }

    protected void bindServiceListener() {

        bind(ObjectRegistry.class).toInstance(objectRegistry);
        bind(IRequestProxyList.class).to(RequestProxyList.class).in(Scopes.SINGLETON);        

        //bindListener(new ClassToTypeLiteralMatcherAdapter(Matchers.subclassesOf(ILifeCycleService.class)),
        bindListener(new ClassToTypeLiteralMatcherAdapter(Matchers.inSubpackage(subPackageOf)),
                new TypeListener() {
                    @Override
                    public <I> void hear(final TypeLiteral<I> typeLiteral, TypeEncounter<I> typeEncounter) {
                        typeEncounter.register(new InjectionListener<I>() {
                            @Override
                            public void afterInjection(Object i) {
                                objectRegistry.objectCreated(i);
                            }
                        });
                    }
                });

    }

    public ObjectRegistry getObjectRegistry() {
        return objectRegistry;
    }

    public void createLifeCycleServicesObjects(final Injector injector) {
        List<ILifeCycleService> services = new LinkedList<>();
        for (Class<?> clazz : lifeCycleServices) {
            services.add(ILifeCycleService.class.cast(injector.getInstance(clazz)));
        }
    }

    protected <T> AnnotatedBindingBuilder<T> bindService(Class<T> clazz) {
        lifeCycleServices.add(clazz);
        return super.bind(clazz);
    }

}
