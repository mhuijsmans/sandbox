package org.mahu.proto.jerseyrest.workflowtest;

import java.util.Optional;

import javax.ws.rs.core.Response;

class NullResponseProvider implements IResponseProvider {
    @Override
    public Optional<Response> getResponse() {
        return Optional.empty();
    }

    @Override
    public Object getResponseBody() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public int getStatus() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void verifyCorrectNumberOfRequestsReceived(String callerInfo) {
        // no check
    }
}