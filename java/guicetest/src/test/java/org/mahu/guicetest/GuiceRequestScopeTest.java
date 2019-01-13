package org.mahu.guicetest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.servlet.ServletScopes;

// This test case explores use of JSR330 @RequestScope scope.
// It includes open/close of the RequestScope.
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
    	private static final long TWO_MS = 2;
		final long now = System.currentTimeMillis();
    	
    	RequestData() {
    		try {
				Thread.sleep(TWO_MS);
			} catch (InterruptedException e) {
				// ignore
			}
    	}
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

        private  final Map<Key<?>, Object> seedMap;
        
        ExecutorUsingRequestScope() {
        	this(Collections.emptyMap());
        }
        
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

    @Test
    public void requestScope_emptySeedMap_guiceCreatesOneObject() throws Exception {
        Injector injector = Guice.createInjector(new BindingModule());
        final ExecutorUsingRequestScope executorUsingRequestScope = new ExecutorUsingRequestScope();

        TestObject testObject1 = executorUsingRequestScope.execute(new RequestScopeRunnable<TestObject>() {
            public TestObject run() {
                TestObject testObject1 = injector.getInstance(TestObject.class);
                assertNotNull(testObject1);
                assertNotNull(testObject1.requestData);

                TestObject testObject2 = injector.getInstance(TestObject.class);
                assertEquals(testObject1.requestData.now, testObject2.requestData.now);

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
    
    @Test
    public void requestScope_seedMapWithData_guiceUsesProviderInstance() throws Exception {
        Injector injector = Guice.createInjector(new BindingModule());
        RequestData requestData = new RequestData();
        Map<Key<?>, Object> seedMap = new HashMap<>();
        seedMap.put(Key.get(RequestData.class), requestData);
        
        final ExecutorUsingRequestScope executorUsingRequestScope = new ExecutorUsingRequestScope(seedMap);

        TestObject testObject1 = executorUsingRequestScope.execute(new RequestScopeRunnable<TestObject>() {
            public TestObject run() {
                return injector.getInstance(TestObject.class);
            }
        });
        
        assertNotNull(testObject1);
        assertNotNull(testObject1.requestData);
        assertEquals(requestData.now, testObject1.requestData.now);
        
        TestObject testObject2 = executorUsingRequestScope.execute(new RequestScopeRunnable<TestObject>() {
            public TestObject run() {
                return injector.getInstance(TestObject.class);
            }
        });
        
        assertNotNull(testObject2);
        assertNotNull(testObject2.requestData);
        assertEquals(requestData.now, testObject2.requestData.now);        
    }    

}
