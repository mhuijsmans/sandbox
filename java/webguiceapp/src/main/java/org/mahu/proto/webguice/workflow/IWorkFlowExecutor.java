package org.mahu.proto.webguice.workflow;

public interface IWorkFlowExecutor {

    void execute(IWorkFlow workflow);

    ITaskListExecutor getTaskListExecutor();

}
