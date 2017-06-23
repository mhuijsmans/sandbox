package org.mahu.proto.webguice.workstep;

import com.google.inject.AbstractModule;

// Via this binding module, the IRequest is bound to GetRequest.
public class WorkFlowStepModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(Step.class);
    }
}
