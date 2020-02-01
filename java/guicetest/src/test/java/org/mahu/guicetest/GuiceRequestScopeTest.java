package org.mahu.guicetest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

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
import com.google.inject.Provides;
import com.google.inject.servlet.RequestScoped;
import com.google.inject.servlet.ServletScopes;

// This test case explores use of JSR330 @RequestScope scope.
// It includes open/close of the RequestScope.
public class GuiceRequestScopeTest {

    static class BindingModule extends AbstractModule {
    	    	
        @Override
        protected void configure() {
        	bindScope(RequestScoped.class, ServletScopes.REQUEST);
            // Next line will result in creation of 1 RequestData object for a RequestScope.
        	// BUT, an object will be created only when there is none exists in the RequestScope map.
        	// That map can be initialized with an object.
        	bind(IRequestData1.class).to(RequestData1.class).in(RequestScoped.class);
        	
        	// Binding to a scope can be based on annotation (as shown above) or to an instance (as shown below);
        	// recommended is above. 
        	// Further reading: https://stackoverflow.com/questions/42185668/the-difference-between-a-scoping-annotation-and-scope-instances-in-guice
            //bind(IRequestData1.class).to(RequestData1.class).in(ServletScopes.REQUEST); 
        }
        
        @Provides
        @RequestScoped
        IRequestData2 providesIRequestData2() {
        	return new RequestData2();
        }
    }
    
    interface IRequestData1 {
    	
    	long getId();
    	
    }

    public static class RequestData1 implements IRequestData1 {
    	private static int idCntr = 0;
		private final long id = idCntr++;
    	
    	RequestData1() {
    		// empty
    	}

		@Override
		public long getId() {
			return id;
		}
    }
    
    interface IRequestData2 {
    	
    	long getId();
    	
    }

    public static class RequestData2 implements IRequestData2 {
    	private static int idCntr = 0;
		private final long id = idCntr++;
    	
    	RequestData2() {
    		// empty
    	}

		@Override
		public long getId() {
			return id;
		}
    }    

    // TestObject has dependency on request-scoped-data.
    static class TestObject {
        private final IRequestData1 requestData1;
        private final IRequestData2 requestData2;

        @Inject
        TestObject(final IRequestData1 requestData1, final IRequestData2 requestData2) {
            this.requestData1 = requestData1;
            this.requestData2 = requestData2;
        }
        
        long getIdOfRequestScopedObject1() {
        	return requestData1.getId();
        }
        
        long getIdOfRequestScopedObject2() {
        	return requestData2.getId();
        }        
    }

    // =====================================================================
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
    // =====================================================================    

    // Note that with an EMPTY seedMap, an RequestScoped object is created by Guice and added to the RequestScope map.
    // That object is used thereafter.
    @Test
    public void requestScope_emptySeedMap_guiceCreatesOneObject() throws Exception {
        Injector injector = Guice.createInjector(new BindingModule());
        final ExecutorUsingRequestScope executorUsingRequestScope = new ExecutorUsingRequestScope();

        // REST call with one request scope
        final TestObject testObject1 = executorUsingRequestScope.execute(new RequestScopeRunnable<TestObject>() {
            public TestObject run() {
            	// Within object graph same requested-scoped object is used
                TestObject testObject1 = injector.getInstance(TestObject.class);
                TestObject testObject2 = injector.getInstance(TestObject.class);
                assertEquals(testObject1.getIdOfRequestScopedObject1(), testObject2.getIdOfRequestScopedObject1());
                return testObject1;
            }
        });

        // I make a new rest call with a new scope
        // In a new object graph a new request specific object is created
        TestObject testObject2 = executorUsingRequestScope.execute(new RequestScopeRunnable<TestObject>() {
            public TestObject run() {
                return injector.getInstance(TestObject.class);
            }
        });
        assertNotEquals(testObject1.getIdOfRequestScopedObject1(), testObject2.getIdOfRequestScopedObject1());
    }
      
    // A RequestScoped object can be created prior to creating an object graph and provided through a so-called seedmap.
    // During object graph creation the provided instance is used.
    @Test
    public void requestScope_seedMapWithDataOutsideExecute_guiceUsesProvidedInstance() throws Exception {  
    	// Create the object prior to creation of the object graph
        final RequestData1 providedRequestData = new RequestData1();
        final Map<Key<?>, Object> seedMap = new HashMap<>();
        seedMap.put(Key.get(IRequestData1.class), providedRequestData);
        
        // Make rest call with request scope
        final ExecutorUsingRequestScope executorUsingRequestScope = new ExecutorUsingRequestScope(seedMap);
        final Injector injector = Guice.createInjector(new BindingModule());        
        TestObject testObject1 = executorUsingRequestScope.execute(new RequestScopeRunnable<TestObject>() {
            public TestObject run() {
            	// Within object graph same requested-scoped object is used, i.e. the externally provided
                TestObject testObject1 = injector.getInstance(TestObject.class);
                TestObject testObject2 = injector.getInstance(TestObject.class);
                assertEquals(testObject1.getIdOfRequestScopedObject1(), testObject2.getIdOfRequestScopedObject1());
                return testObject1;
            }
        });
        assertEquals(providedRequestData.getId(), testObject1.getIdOfRequestScopedObject1());
        
        // Create another object graph, again the provided object is used. 
        TestObject testObject2 = executorUsingRequestScope.execute(new RequestScopeRunnable<TestObject>() {
            public TestObject run() {
                return injector.getInstance(TestObject.class);
            }
        });
        assertEquals(providedRequestData.getId(), testObject2.getIdOfRequestScopedObject1());        
    }
    
    // RequestScoped object can be created directly (via getInstance()) or indirectly (via getInstance() of another class/interface).
    // Order doesn't matter
    @Test
    public void requestScope_getInstanceRequestScopedInstanceDirectlyAndIndirect_guiceReturnsOneInstance() throws Exception {
        final Injector injector = Guice.createInjector(new BindingModule());          	
        
        final Map<Key<?>, Object> seedMap1 = new HashMap<>();
        final ExecutorUsingRequestScope executorUsingRequestScope1 = new ExecutorUsingRequestScope(seedMap1);        
        executorUsingRequestScope1.execute(new RequestScopeRunnable<TestObject>() {
            public TestObject run() {
            	// Indirect and direct retrieval request scoped object
                TestObject testObject1 = injector.getInstance(TestObject.class);
                IRequestData1 requestData1 = injector.getInstance(IRequestData1.class);                
                assertEquals(testObject1.getIdOfRequestScopedObject1(), requestData1.getId());
                return testObject1;
            }
        });
        
        final Map<Key<?>, Object> seedMap2 = new HashMap<>();
        final ExecutorUsingRequestScope executorUsingRequestScope2 = new ExecutorUsingRequestScope(seedMap2);        
        executorUsingRequestScope2.execute(new RequestScopeRunnable<TestObject>() {
            public TestObject run() {
            	// Direct and indirect retrieval request scoped object
                IRequestData1 requestData1 = injector.getInstance(IRequestData1.class);             	
                TestObject testObject1 = injector.getInstance(TestObject.class);               
                assertEquals(testObject1.getIdOfRequestScopedObject1(), requestData1.getId());
                return testObject1;
            }
        });
                
    }    
    
    // Injector is created inside the "execute with scope".
    @Test
    public void requestScope_seedMapWithDataInsideExecute_guiceUsesProvidedInstance() throws Exception {
        final RequestData1 providedRequestData = new RequestData1();
        final Map<Key<?>, Object> seedMap = new HashMap<>();
        seedMap.put(Key.get(IRequestData1.class), providedRequestData);
        
        // with seedMap
        final ExecutorUsingRequestScope executorUsingRequestScope = new ExecutorUsingRequestScope(seedMap);
        TestObject testObject1 = executorUsingRequestScope.execute(new RequestScopeRunnable<TestObject>() {
            public TestObject run() {
                Injector injector = Guice.createInjector(new BindingModule());            	
                return injector.getInstance(TestObject.class);
            }
        });
        assertEquals(providedRequestData.getId(), testObject1.getIdOfRequestScopedObject1());
        
        // No seedMap
        TestObject testObject2 = executorUsingRequestScope.execute(new RequestScopeRunnable<TestObject>() {
            public TestObject run() {
                Injector injector = Guice.createInjector(new BindingModule());              	
                return injector.getInstance(TestObject.class);
            }
        });
        assertEquals(providedRequestData.getId(), testObject2.getIdOfRequestScopedObject1());        
    }     

}
