package org.mahu.proto.webguice.request;

import org.mahu.proto.webguice.workflow.ITaskListExecutor;
import org.mahu.proto.webguice.workflow.IWorkFlowExecutor;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

public class RequestCommonBindingsModule extends AbstractModule {

    @Override
    protected void configure() {
    }

    // This modules defines all common request scoped bindings.
    @Provides
    @Singleton
    ITaskListExecutor provideTaskListExecutor(IWorkFlowExecutor workFlowExecutor) {
        return workFlowExecutor.getTaskListExecutor();
    }
}
