package org.mahu.guicetest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.inject.Inject;

import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

// This test case explores a childInjector. 
// It include a check that the childInjector is injected.
public class GuiceChildInjectorTest {

    static class BindingModule1 extends AbstractModule {
        @Override
        protected void configure() {
            bind(RequestData1.class);
        }
    }

    public static class RequestData1 {
    }

    public static class RequestData2 {
    }

    static class TestObject1 {
        final RequestData1 requestData;

        @Inject
        TestObject1(final RequestData1 requestData) {
            this.requestData = requestData;
        }
    }

    static class TestObject2 {
        final RequestData1 requestData1;
        final RequestData2 requestData2;
        final Injector childInjector;

        @Inject
        TestObject2(final RequestData1 requestData1, final RequestData2 requestData2, final Injector childInjector) {
            this.requestData1 = requestData1;
            this.requestData2 = requestData2;
            this.childInjector = childInjector;

            // Verify that childInjector is injected
            childInjector.getInstance(TestObject3.class);
        }
    }

    static class TestObject3 {
        final RequestData2 requestData2;

        @Inject
        TestObject3(final RequestData2 requestData2) {
            this.requestData2 = requestData2;
        }
    }

    @Test
    public void requestScope() throws Exception {
        Injector injector = Guice.createInjector(new BindingModule1());
        injector.getInstance(TestObject1.class);

        // Create a child Injector with bindings specific for the request.
        // Binding can differ per request
        RequestData2 requestData2 = new RequestData2();
        Injector requestInjector = injector.createChildInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(RequestData2.class).toInstance(requestData2);
            }
        });
        TestObject2 testObject2 = requestInjector.getInstance(TestObject2.class);

        assertNotNull(testObject2.requestData1);
        assertEquals(requestData2, testObject2.requestData2);
    }

}
