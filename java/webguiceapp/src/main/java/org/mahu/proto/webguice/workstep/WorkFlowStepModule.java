package org.mahu.proto.webguice.workstep;

import com.google.inject.AbstractModule;

public class WorkFlowStepModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(Step.class);
    }
}
