package org.mahu.guicetest;

import static org.junit.Assert.assertNotNull;

import javax.inject.Inject;

import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class GuiceBuiltInBindingsTest {

    static class BindingModule extends AbstractModule {
        @Override
        protected void configure() {
        }
    }

    public static class RequestData {
    }

    static class TestObject {
        final Injector injector;

        @Inject
        TestObject(final Injector injector) {
            this.injector = injector;
        }
    }

    // This test case explores BuildIn bindings supported.
    @Test
    public void requestScope() throws Exception {
        Injector injector = Guice.createInjector(new BindingModule());
        TestObject testObject = injector.getInstance(TestObject.class);

        assertNotNull(testObject.injector);
    }

}
