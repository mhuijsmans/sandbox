package org.mahu.guicetest;

import static org.junit.Assert.assertNotEquals;

import javax.inject.Inject;
import javax.inject.Provider;

import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

// source: https://github.com/google/guice/wiki/ProviderBindings
public class GuiceProviderBindingTest {

    static class BindingModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(RequestData.class).toProvider(RequestDataProvider.class);
        }
    }

    public static class RequestData {
    }

    static class RequestDataProvider implements Provider<RequestData> {

        @Override
        public RequestData get() {
            // New is applied here, so no injection for RequestData
            return new RequestData();
        }

    }

    static class TestObject {
        final RequestData requestData;

        @Inject
        TestObject(final RequestData requestData) {
            this.requestData = requestData;
        }

    }

    // This test case explores Provider responsible for generating an object.
    @Test
    public void requestScope() throws Exception {
        Injector injector = Guice.createInjector(new BindingModule());
        TestObject testObject1 = injector.getInstance(TestObject.class);
        TestObject testObject2 = injector.getInstance(TestObject.class);

        // Check that provider can be used to get 2 objects.
        assertNotEquals(testObject1.requestData, testObject2.requestData);
    }

}
