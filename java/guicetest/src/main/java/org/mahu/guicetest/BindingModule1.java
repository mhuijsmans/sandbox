package org.mahu.guicetest;

import javax.inject.Singleton;

import com.google.inject.AbstractModule;

public class BindingModule1 extends AbstractModule {
    @Override
    protected void configure() {
        bind(ICreditCardProcessor.class).to(PaypalCreditCardProcessor.class);
        bind(IConfigData.class).to(RealConfigData.class);
        bind(IRequestContext.class).to(RequestContextImpl.class).in(Singleton.class);
        bind(DataType1.class).toProvider(DataType1Provider.class).in(Singleton.class);

        bind(InjectorProxy.class).toInstance(new InjectorProxy());
    }
}
