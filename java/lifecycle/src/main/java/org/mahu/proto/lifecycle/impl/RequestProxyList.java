package org.mahu.proto.lifecycle.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.RejectedExecutionException;

import org.mahu.proto.lifecycle.IRequestProxyList;

public class RequestProxyList implements IRequestProxyList {
    
    public final static String REJECT_REQUEST_REASON = "No ready to execute requests";

    private boolean isRequestAllowed = false;
    private final List<RequestProxy> requests = new LinkedList<>();

    @Override
    public void add(final RequestProxy requestProxy) {
        synchronized (requests) {
            if (isRequestAllowed) {
                requests.add(requestProxy);
            } else {
                throw new RejectedExecutionException(REJECT_REQUEST_REASON);
            }
        }
    }

    @Override
    public void remove(final RequestProxy requestProxy) {
        synchronized (requests) {
            requests.remove(requestProxy);
        }
    }

    public void abortAllRequests() {
        synchronized (requests) {
            for (RequestProxy requestProxy : requests) {
                requestProxy.abort();
            }
        }
    }

    public void allowedExecutionRequests() {
        synchronized (requests) {
            isRequestAllowed = true;
        }
    }

    public void rejectExecutionRequests() {
        synchronized (requests) {
            isRequestAllowed = false;
        }  
    }

}
