package org.mahu.proto.webguice.workflow;

import org.mahu.proto.webguice.annotation.NotDefinedBindings;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

//@formatter:off
@NotDefinedBindings(binding = { 
        ITaskListExecutor.class // Typically defined as singleton on application level
     })
//@formatter:on
public class WorkflowModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(IWorkFlowExecutor.class).to(WorkFlowExecutor.class).in(Singleton.class);
    }

}
