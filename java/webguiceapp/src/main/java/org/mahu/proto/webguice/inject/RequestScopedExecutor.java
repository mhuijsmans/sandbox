package org.mahu.proto.webguice.inject;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Callable;

import com.google.inject.Key;
import com.google.inject.servlet.ServletScopes;

public final class RequestScopedExecutor {

    private static final Map<Key<?>, Object> IMMUTABLE_EMPTYMAP = Collections.emptyMap();

    public static <T> T execute(final RequestScopeRunnable<T> r) {
        try {
            return ServletScopes.scopeRequest(new Callable<T>() {
                public T call() throws Exception {
                    return r.run();
                }
            }, IMMUTABLE_EMPTYMAP).call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private RequestScopedExecutor() {
        // empty
    }

}
