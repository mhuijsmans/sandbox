package org.mahu.proto.webguice.workflow;

class WorkFlowExecutor implements IWorkFlowExecutor {

    @Override
    public ITaskListExecutor createTaskListExecutor() {
        return new TaskListExecutor();
    }

}
