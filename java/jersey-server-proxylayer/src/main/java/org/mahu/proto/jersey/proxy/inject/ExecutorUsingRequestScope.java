package org.mahu.proto.jersey.proxy.inject;

import java.util.Map;
import java.util.concurrent.Callable;

import com.google.inject.Key;
import com.google.inject.servlet.ServletScopes;

/**
 * This class provides and RequestScoped object map to an injector. 
 */
final class ExecutorUsingRequestScope {

    private  final Map<Key<?>, Object> seedMap;

    ExecutorUsingRequestScope(Map<Key<?>, Object> seedMap) { 
    	this.seedMap = seedMap;
    }

    public <T> T execute(RequestScopeRunnable<T> r) {
        try {
            return ServletScopes.scopeRequest(new Callable<T>() {
                public T call() throws Exception {
                    return r.run();
                }
            }, seedMap).call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}