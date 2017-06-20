package org.mahu.guicetest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.servlet.ServletScopes;

public class GuiceRequestScopeTest {

    static class BindingModule extends AbstractModule {
        @Override
        protected void configure() {
            // Next line will result in creation of 1 RequestData object for a
            // RequestScope.
            bind(RequestData.class).in(ServletScopes.REQUEST);
        }
    }

    public static class RequestData {
    }

    static class TestObject {
        final RequestData requestData;

        @Inject
        TestObject(final RequestData requestData) {
            this.requestData = requestData;
        }
    }

    // RequestScopeRunnable because Guice uses Callable which throws an
    // exception.
    public interface RequestScopeRunnable<T> {
        public T run();
    }

    static class ExecutorUsingRequestScope {

        private static final Map<Key<?>, Object> IMMUTABLE_EMPTYMAP = Collections.emptyMap();

        public <T> T execute(RequestScopeRunnable<T> r) {
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

    }

    // This test case explores use of JSR330 @RequestScope scope.
    @Test
    public void requestScope() throws Exception {
        Injector injector = Guice.createInjector(new BindingModule());

        ExecutorUsingRequestScope executorUsingRequestScope = injector.getInstance(ExecutorUsingRequestScope.class);

        TestObject testObject1 = executorUsingRequestScope.execute(new RequestScopeRunnable<TestObject>() {
            public TestObject run() {
                TestObject testObject1 = injector.getInstance(TestObject.class);
                assertNotNull(testObject1);
                assertNotNull(testObject1.requestData);

                TestObject testObject2 = injector.getInstance(TestObject.class);
                assertEquals(testObject1.requestData, testObject2.requestData);

                return testObject1;
            }
        });

        TestObject testObject2 = executorUsingRequestScope.execute(new RequestScopeRunnable<TestObject>() {
            public TestObject run() {
                return injector.getInstance(TestObject.class);
            }
        });

        assertNotEquals(testObject1.requestData, testObject2.requestData);
    }

}
