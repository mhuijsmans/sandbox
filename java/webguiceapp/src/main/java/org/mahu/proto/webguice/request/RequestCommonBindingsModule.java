package org.mahu.proto.webguice.request;

import org.mahu.proto.webguice.workflow.ITaskListExecutor;
import org.mahu.proto.webguice.workstep.WorkFlowStepModule;

import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.Singleton;

public class RequestCommonBindingsModule extends AbstractModule {

    @Override
    protected void configure() {
        // next module requires ITaskListExecutor, which is RequestScoped
        install(new WorkFlowStepModule());
        bindTaskListExecutorInSingleton(binder());
    }

    protected static void bindTaskListExecutorInSingleton(Binder binder) {
        binder.bind(ITaskListExecutor.class).toProvider(TaskListExecutorProvider.class).in(Singleton.class);
    }

}
