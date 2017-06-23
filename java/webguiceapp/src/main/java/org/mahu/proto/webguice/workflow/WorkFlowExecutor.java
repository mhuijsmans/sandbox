package org.mahu.proto.webguice.workflow;

class WorkFlowExecutor implements IWorkFlowExecutor {

    private final ITaskListExecutor taskListExecutor = new TaskListExecutor();

    @Override
    public void execute(IWorkFlow workflow) {
    }

    @Override
    public ITaskListExecutor getTaskListExecutor() {
        return taskListExecutor;
    }

}
