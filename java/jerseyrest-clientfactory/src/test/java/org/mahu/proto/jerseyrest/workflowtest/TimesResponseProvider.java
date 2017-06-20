package org.mahu.proto.jerseyrest.workflowtest;

import java.util.Optional;

import javax.ws.rs.core.Response;

class TimesResponseProvider implements IResponseProvider {

    private final int status;
    private final Optional<Object> responseBody;
    private final long maxTimes;
    private long count = 0;

    TimesResponseProvider(final int status, final int maxTimes) {
        this(status, null, maxTimes);
    }

    TimesResponseProvider(final IResponseProvider other, final int maxTimes) {
        this(other.getStatus(), other.getResponseBody(), maxTimes);
    }

    TimesResponseProvider(final int status, final Object responseBody, final int maxTimes) {
        this.status = status;
        this.maxTimes = maxTimes;
        this.responseBody = Optional.ofNullable(responseBody);
    }

    @Override
    public Optional<Response> getResponse() {
        if (maxTimes == 0 || count < maxTimes) {
            // Always count so that it can be checked if ResponseProvider has
            // been called.
            count++;
            return createResponse();
        }
        return Optional.empty();
    }

    @Override
    public Object getResponseBody() {
        return responseBody.isPresent() ? responseBody.get() : null;
    }

    @Override
    public int getStatus() {
        return status;
    }

    private Optional<Response> createResponse() {
        return Optional.of(ResponseBuilder.createResponse(status, responseBody));
    }

    @Override
    public void verifyCorrectNumberOfRequestsReceived(final String callerInfo) {
        if (maxTimes == 0) {
            if (count == 0) {
                throw new RuntimeException("For " + callerInfo + " always() specified, but never called");
            }
        }
        if (maxTimes > 0) {
            if (count != maxTimes) {
                throw new RuntimeException("For " + callerInfo + " times=" + maxTimes + " but calledCount=" + count);
            }
        }
    }
}
