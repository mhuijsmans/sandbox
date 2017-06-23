package org.mahu.proto.webguice.workflow;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.servlet.ServletScopes;

// Via this binding module, the IRequest is bound to GetRequest.
public class WorkflowModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(IWorkFlowExecutor.class).to(WorkFlowExecutor.class).in(ServletScopes.REQUEST);
    }

    @Provides
    ITaskListExecutor provideTaskListExecutor(IWorkFlowExecutor workFlowExecutor) {
        return workFlowExecutor.getTaskListExecutor();
    }
}
