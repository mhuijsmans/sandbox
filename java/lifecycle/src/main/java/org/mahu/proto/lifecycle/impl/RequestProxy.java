package org.mahu.proto.lifecycle.impl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

import org.mahu.proto.lifecycle.IRequestProxyList;
import org.mahu.proto.lifecycle.example2.IEventBus;

public class RequestProxy implements InvocationHandler {
    public static final String METHOD_CALLED_TWICE_REASON = "Use-once Object"; 
    
    private final Object lock = new Object();
    private final IEventBus eventBus;
    private final Object proxyToObject;
    private final IRequestProxyList requestProxyList;
    private Optional<RequestProxyEvent> event;

    public RequestProxy(final Object proxyToObject, final IEventBus eventBus,
            final IRequestProxyList requestProxyList) {
        this.eventBus = eventBus;
        this.proxyToObject = proxyToObject;
        this.requestProxyList = requestProxyList;
        this.event = Optional.empty();
    }

    public Object invoke(final Object requestProxy, final Method method, final Object[] args) throws Throwable {
        createRequestProxyEvent(method, args);
        try {
            requestProxyList.add(this);
            eventBus.post(event.get());
            return event.get().getResult();
        } catch (InvocationTargetException e) {
            // InvocationTargetException is a checked exception that wraps an
            // exception thrown by an invoked method or constructor.
            throw e.getTargetException();
        } catch (Exception e) {
            // EventBus throws an RejectedExecutionException when posting on a
            // closed eventBus
            throw e;
        } finally {
            requestProxyList.remove(this);
        }
    }

    private void createRequestProxyEvent(final Method method, final Object[] args) {
        synchronized (lock) {
            if (event.isPresent()) {
                throw new RuntimeException(METHOD_CALLED_TWICE_REASON);
            }
            event =  Optional.of(new RequestProxyEvent(proxyToObject, method, args));
        }
    }

    @Override
    public boolean equals(final Object other) {
        // compare using pointers
        return this == other;
    }

    public void abort() {
        synchronized (lock) {
            if (event.isPresent()) {
                event.get().abort();
            }
        }
    }

}
