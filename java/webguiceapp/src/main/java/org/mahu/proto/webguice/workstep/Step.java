package org.mahu.proto.webguice.workstep;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.mahu.proto.webguice.workflow.ITaskListExecutor;
import org.mahu.proto.webguice.worktask.Task1;
import org.mahu.proto.webguice.worktask.Task2Data;
import org.mahu.proto.webguice.worktask.Task2Factory;
import org.mahu.proto.webguice.worktask.Task2ResultData;
import org.mahu.proto.webguice.worktask.TaskResultData;

public class Step {

    private final Task1 task1;
    private final Task2Factory task2factory;
    private final TaskResultData<Task2ResultData> task2ResultData = new TaskResultData<Task2ResultData>();
    private final ITaskListExecutor executor;

    @Inject
    Step(final Task1 task1, final Task2Factory task2factory, final ITaskListExecutor executor) {
        this.task1 = task1;
        this.task2factory = task2factory;
        this.executor = executor;
    }

    public void execute(Task2Data data) {
        List<Object> list = new ArrayList<>();
        list.add(task1);
        list.add(task2factory.create(data, task2ResultData));
        executor.execute(list);
    }

}
