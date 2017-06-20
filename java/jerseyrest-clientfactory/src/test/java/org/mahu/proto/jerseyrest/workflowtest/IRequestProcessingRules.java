package org.mahu.proto.jerseyrest.workflowtest;

import javax.ws.rs.core.Response;

interface IRequestProcessingRules {

    Response getResponse(String method, String path);

    void verifyThatCorrectRequestsAreReceived();
}
