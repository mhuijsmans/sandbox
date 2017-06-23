package org.mahu.proto.webguice.worktask;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

// Via this binding module, the IRequest is bound to GetRequest.
public class WorkFlowTaskModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(Task1.class);
        install(new FactoryModuleBuilder().build(Task2Factory.class));
    }
}
