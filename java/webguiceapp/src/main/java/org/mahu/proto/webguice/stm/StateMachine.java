package org.mahu.proto.webguice.stm;

import javax.ws.rs.core.Response;

class StateMachine implements IStateMachine {

    private final Object lock = new Object();

    @Override
    public Response execute(IRequestProvider requestProvider) {
        if (requestProvider.getRequestType().isLongRequest()) {
            return executeLong(requestProvider);
        } else {
            return executeShort(requestProvider);
        }
    }

    private Response executeShort(IRequestProvider requestProvider) {
        // During execution of a short request, no other requests are accepted.
        synchronized (lock) {
            if (!isRequestAllowed(requestProvider)) {
                return Response.serverError().build();
            } else {
                return requestProvider.get().execute();
            }
        }
    }

    private Response executeLong(IRequestProvider requestProvider) {
        synchronized (lock) {
            if (!isRequestAllowed(requestProvider)) {
                return Response.serverError().build();
            }
        }
        // Long request is executed outside the lock so that other requests can
        // be processed
        try {
            return requestProvider.get().execute();
        } finally {
            synchronized (lock) {
                longRequestHasTerminated(requestProvider.getRequestType().getEndRequestType());
            }
        }
    }

    // Code up to here can be a separate class.
    // Code below can be done in a separate class.

    private boolean isRequestAllowed(IRequestProvider requestProvider) {
        // Check state if request is allowed.
        return true;
    }

    private void longRequestHasTerminated(RequestType endRequestType) {
        // Update state. That can be a separate class.
    }

}
