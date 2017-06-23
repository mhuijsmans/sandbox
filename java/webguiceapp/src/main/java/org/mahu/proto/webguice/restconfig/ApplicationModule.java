package org.mahu.proto.webguice.restconfig;

import org.mahu.proto.webguice.restclient.RestClientModule;
import org.mahu.proto.webguice.stm.StateMachineBindingModule;
import org.mahu.proto.webguice.workflow.WorkflowModule;
import org.mahu.proto.webguice.worktask.WorkFlowTaskModule;

import com.google.inject.AbstractModule;

public class ApplicationModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new StateMachineBindingModule());
        install(new RestClientModule());
        install(new WorkflowModule());
        install(new WorkFlowTaskModule());
    }

}
