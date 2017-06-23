package org.mahu.proto.webguice.workflowtask;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.mahu.proto.webguice.restclient.RestClientModule;
import org.mahu.proto.webguice.worktask.Task2;
import org.mahu.proto.webguice.worktask.Task2Data;
import org.mahu.proto.webguice.worktask.Task2Factory;
import org.mahu.proto.webguice.worktask.Task2ResultData;
import org.mahu.proto.webguice.worktask.TaskResultData;
import org.mahu.proto.webguice.worktask.WorkFlowTaskModule;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class Task2Test {

    @Test
    public void execute_resutDataSet() {
        Injector injector = Guice.createInjector(new WorkFlowTaskModule(), new RestClientModule());

        Task2Factory task2Factory = injector.getInstance(Task2Factory.class);

        Task2Data data = new Task2Data();
        final TaskResultData<Task2ResultData> task2ResultData = new TaskResultData<Task2ResultData>();
        Task2 task = task2Factory.create(data, task2ResultData);
        task.execute();

        assertNotNull(task2ResultData.get());
    }

}
