package org.mahu.guicetest;

import static org.junit.Assert.assertNotEquals;

import javax.inject.Inject;
import javax.inject.Provider;

import org.junit.Test;
import org.mahu.guicetest.GuiceRequestScopeTest.RequestData;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

// This test case explores BuildIn bindings.
// source: https://github.com/google/guice/wiki/InjectingProviders
public class GuiceInjectingProvidersTest {

    static class BindingModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(RequestData.class);
        }
    }

    static class TestObject {
        // Although there is no Provider<RequestData> binding, it is still
        // possible to define a binding here.
        // That means for me that injection of the object created by the
        // provider is also delayed. Feels like lazy
        final Provider<RequestData> requestDataProvider;

        @Inject
        TestObject(final Provider<RequestData> requestDataProvider) {
            this.requestDataProvider = requestDataProvider;
        }

    }

    @Test
    public void requestScope() throws Exception {
        Injector injector = Guice.createInjector(new BindingModule());
        TestObject testObject = injector.getInstance(TestObject.class);

        // Check that provider can be used to get 2 objects.
        assertNotEquals(testObject.requestDataProvider.get(), testObject.requestDataProvider.get());
    }

}
