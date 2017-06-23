package org.mahu.proto.webguice.stm;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

public class StateMachineBindingModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(IIRequestProcessor.class).to(IRequestProcessor.class);
        bind(IStateMachine.class).to(StateMachine.class).in(Singleton.class);
        bind(IStateContext.class).to(StateContext.class).in(Singleton.class);
    }
}