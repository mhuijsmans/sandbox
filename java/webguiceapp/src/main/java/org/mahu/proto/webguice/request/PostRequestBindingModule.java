package org.mahu.proto.webguice.request;

import org.mahu.proto.webguice.scanworkflow.ScanWorkflowModule;
import org.mahu.proto.webguice.stm.IRequest;

import com.google.inject.AbstractModule;

public class PostRequestBindingModule extends AbstractModule {
    private final PostRequestData requestData;

    public PostRequestBindingModule(final PostRequestData requestData) {
        this.requestData = requestData;
    }

    @Override
    protected void configure() {
        install(new ScanWorkflowModule());
        bind(IRequest.class).to(PostRequest.class);
        bind(PostRequestData.class).toInstance(requestData);
    }
}
