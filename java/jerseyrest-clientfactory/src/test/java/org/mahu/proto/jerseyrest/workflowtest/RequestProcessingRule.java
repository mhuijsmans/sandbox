package org.mahu.proto.jerseyrest.workflowtest;

import java.util.Optional;

import javax.ws.rs.core.Response;

class RequestProcessingRule {
    private final String name;
    private IResponseProvider responseProvider = new NullResponseProvider();
    private final int defaultStatus;

    RequestProcessingRule(final String name, final int defaultResponse) {
        this.name = name;
        this.defaultStatus = defaultResponse;
    }

    public RequestProcessingRule thenReturnDefaultResponse() {
        responseProvider = new TimesResponseProvider(defaultStatus, 1);
        return this;
    }

    public void thenReturnOkWithBody(final Object responseBody) {
        responseProvider = new TimesResponseProvider(RequestProcessingRules.OK, responseBody, 1);
    }

    public Optional<Response> getResponse() {
        return responseProvider.getResponse();
    }

    public void thenReturnNoContentResponse() {
        responseProvider = new TimesResponseProvider(RequestProcessingRules.NO_CONTENT, 1);
    }

    public void thenReturnResourceCreated() {
        responseProvider = new TimesResponseProvider(RequestProcessingRules.CREATED, 1);
    }

    public void always() {
        times(0);
    }

    public void times(final int maxTimes) {
        responseProvider = new TimesResponseProvider(responseProvider, maxTimes);
    }

    void verifyCorrectNumberOfRequestsReceived() {
        responseProvider.verifyCorrectNumberOfRequestsReceived(name);
    }

}