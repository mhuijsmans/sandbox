package org.mahu.proto.webguice.worktask;

import org.mahu.proto.webguice.restclient.RestClientModule;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

// Via this binding module, the IRequest is bound to GetRequest.
public class WorkFlowTaskModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new RestClientModule());
        bind(Task1.class);
        install(new FactoryModuleBuilder().build(Task2Factory.class));
    }
}
