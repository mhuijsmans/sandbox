package org.mahu.proto.webguice.request;

import org.mahu.proto.webguice.scanworkflow.ScanWorkFlowSettings;
import org.mahu.proto.webguice.scanworkflow.ScanWorkflowModule;
import org.mahu.proto.webguice.stm.IRequest;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class PostRequestBindingModule extends AbstractModule {
    private final PostRequestData requestData;

    // Constructor contains all request specific data that is used to create the
    // request specific bindings.
    public PostRequestBindingModule(final PostRequestData requestData) {
        this.requestData = requestData;
    }

    @Override
    protected void configure() {
        // First install the request specific modules
        install(new RequestCommonBindingsModule());
        install(new ScanWorkflowModule());
        // Next define the request specific binding
        // First it is the request that is to be executed
        bind(IRequest.class).to(PostRequest.class);
        // Next it makes request data available
        bind(PostRequestData.class).toInstance(requestData);
    }

    @Provides
    ScanWorkFlowSettings provideScanWorkFlowData(PostRequestData postRequestData) {
        // Perform conversion from PostRequestData to ScanWorkFlowData
        return new ScanWorkFlowSettings();
    }
}
