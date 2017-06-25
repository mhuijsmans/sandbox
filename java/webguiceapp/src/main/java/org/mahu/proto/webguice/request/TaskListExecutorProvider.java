package org.mahu.proto.webguice.request;

import javax.inject.Inject;
import javax.inject.Provider;

import org.mahu.proto.webguice.workflow.ITaskListExecutor;
import org.mahu.proto.webguice.workflow.IWorkFlowExecutor;

final class TaskListExecutorProvider implements Provider<ITaskListExecutor> {

    private final IWorkFlowExecutor workFlowExecutor;

    @Inject
    TaskListExecutorProvider(final IWorkFlowExecutor workFlowExecutor) {
        this.workFlowExecutor = workFlowExecutor;
    }

    @Override
    public ITaskListExecutor get() {
        return workFlowExecutor.createTaskListExecutor();
    }

}
