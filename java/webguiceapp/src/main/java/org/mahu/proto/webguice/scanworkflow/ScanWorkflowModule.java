package org.mahu.proto.webguice.scanworkflow;

import org.mahu.proto.webguice.workflow.WorkflowModule;
import org.mahu.proto.webguice.worktask.WorkFlowTaskModule;

import com.google.inject.AbstractModule;

// Via this binding module, the IRequest is bound to GetRequest.
public class ScanWorkflowModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new WorkFlowTaskModule());
        install(new WorkflowModule());
        bind(ScanWorkFlow.class);
    }
}
