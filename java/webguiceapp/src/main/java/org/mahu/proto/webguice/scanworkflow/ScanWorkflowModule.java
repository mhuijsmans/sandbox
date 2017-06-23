package org.mahu.proto.webguice.scanworkflow;

import com.google.inject.AbstractModule;

// Via this binding module, the IRequest is bound to GetRequest.
public class ScanWorkflowModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(ScanWorkFlow.class);
    }
}
