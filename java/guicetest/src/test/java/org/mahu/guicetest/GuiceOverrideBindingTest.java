package org.mahu.guicetest;

import static org.junit.Assert.assertTrue;

import javax.inject.Inject;

import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.util.Modules;

// This test case explores a overriding on bindings
// source: https://stackoverflow.com/questions/483087/overriding-binding-in-guice/531110#531110
public class GuiceOverrideBindingTest {

    static class BindingModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(IRequestData.class).to(RequestData1.class);
        }
    }

    public static interface IRequestData {
    }

    public static class RequestData1 implements IRequestData {
    }

    public static class RequestData2 implements IRequestData {
    }

    static class TestObject {
        final IRequestData requestData;

        @Inject
        TestObject(final IRequestData requestData) {
            this.requestData = requestData;
        }
    }

    @Test
    public void testOverride() throws Exception {
        Injector injector1 = Guice.createInjector(new BindingModule());
        assertTrue(injector1.getInstance(TestObject.class).requestData instanceof RequestData1);

        Injector injector2 = Guice.createInjector(Modules.override(new BindingModule()).with(new AbstractModule() {
            @Override
            protected void configure() {
                bind(IRequestData.class).to(RequestData2.class);
            }
        }));
        assertTrue(injector2.getInstance(TestObject.class).requestData instanceof RequestData2);
    }

}
