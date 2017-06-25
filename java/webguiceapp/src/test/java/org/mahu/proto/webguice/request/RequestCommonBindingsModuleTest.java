package org.mahu.proto.webguice.request;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.mahu.proto.webguice.workflow.ITaskListExecutor;
import org.mahu.proto.webguice.workflow.IWorkFlowExecutor;
import org.mahu.proto.webguice.workflow.WorkflowModule;

import com.google.inject.AbstractModule;
import com.google.inject.ConfigurationException;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class RequestCommonBindingsModuleTest {

    static class AppTestModule extends AbstractModule {
        @Override
        protected void configure() {
            install(new WorkflowModule());
        }
    };

    static class RequestTestModule extends AbstractModule {
        @Override
        protected void configure() {
            RequestCommonBindingsModule.bindTaskListExecutorInSingleton(binder());
        }
    };

    @Test
    public void getInstance_noTaskListBinding_exception() {
        Injector injector = Guice.createInjector(new AppTestModule());
        try {
            injector.getInstance(ITaskListExecutor.class);
            fail("Exception expected because there is no binding for ITaskListExecutor");
        } catch (ConfigurationException e) {

        }
    }

    @Test
    public void getInstance_oneChildWithBinding_workFlowIsSingletonAndTaskListExecutorIsRequestScoped() {
        Injector injector = Guice.createInjector(new AppTestModule());
        Injector childInjector = injector.createChildInjector(new RequestTestModule());

        assertEquals(childInjector.getInstance(IWorkFlowExecutor.class),
                childInjector.getInstance(IWorkFlowExecutor.class));
        assertEquals(childInjector.getInstance(ITaskListExecutor.class),
                childInjector.getInstance(ITaskListExecutor.class));
    }

    @Test
    public void getInstance_twoChildrenWithBinding_workFlowIsSingletonAndTaskListExecutorIsRequestScoped() {
        Injector injector = Guice.createInjector(new AppTestModule());
        Injector childInjector1 = injector.createChildInjector(new RequestTestModule());
        Injector childInjector2 = injector.createChildInjector(new RequestTestModule());

        assertEquals(childInjector1.getInstance(IWorkFlowExecutor.class),
                childInjector2.getInstance(IWorkFlowExecutor.class));
        ITaskListExecutor taskListExecutor1 = childInjector1.getInstance(ITaskListExecutor.class);
        ITaskListExecutor taskListExecutor2 = childInjector2.getInstance(ITaskListExecutor.class);
        assertNotEquals(taskListExecutor1, taskListExecutor2);
    }

}
