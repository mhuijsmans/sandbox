package org.mahu.proto.webguice.request;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.mahu.proto.webguice.scanworkflow.ScanWorkFlow;
import org.mahu.proto.webguice.scanworkflow.ScanWorkFlowExecuteData;
import org.mahu.proto.webguice.stm.IRequest;
import org.mahu.proto.webguice.stm.IStateContext;

// PostRequest has request specific data.
class PostRequest implements IRequest {

    private final PostRequestData postRequestData;
    // Use of IStateContext is a anti-pattern. IStateContext is a dataContainer.
    // This class is interested in members of IStateContext. Can be solved in a
    // module with Provider methods.
    private final IStateContext stateMachineContext;
    private final ScanWorkFlow workflow;

    @Inject
    PostRequest(final PostRequestData postRequestData, final IStateContext stateMachineContext,
            final ScanWorkFlow workflow) {
        this.postRequestData = postRequestData;
        this.stateMachineContext = stateMachineContext;
        this.workflow = workflow;
    }

    @Override
    public Response execute() {
        // Create ScanWorkFlowExecuteData, probably from PostRequestData and
        // other (global) data.
        ScanWorkFlowExecuteData scanWorkFlowExecuteData = new ScanWorkFlowExecuteData();
        workflow.execute(scanWorkFlowExecuteData);
        return Response.ok(postRequestData.getText()).build();
    }

}
