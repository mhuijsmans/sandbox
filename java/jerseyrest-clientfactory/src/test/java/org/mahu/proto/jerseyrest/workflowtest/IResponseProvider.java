package org.mahu.proto.jerseyrest.workflowtest;

import java.util.Optional;

import javax.ws.rs.core.Response;

interface IResponseProvider {

    Optional<Response> getResponse();

    Object getResponseBody();

    int getStatus();

    void verifyCorrectNumberOfRequestsReceived(final String callerInfo);
}