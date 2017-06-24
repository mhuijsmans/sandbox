package org.mahu.proto.webguice.workflow;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

// Via this binding module, the IRequest is bound to GetRequest.
public class WorkflowModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(IWorkFlowExecutor.class).to(WorkFlowExecutor.class).in(Singleton.class);
    }

}
