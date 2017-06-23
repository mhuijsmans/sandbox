package org.mahu.proto.webguice.workflow;

import com.google.inject.AbstractModule;

// Via this binding module, the IRequest is bound to GetRequest.
public class WorkflowModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(ITaskListExecutor.class).to(TaskListExecutor.class);
    }
}
