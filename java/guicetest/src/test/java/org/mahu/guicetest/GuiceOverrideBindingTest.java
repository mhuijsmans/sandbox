package org.mahu.guicetest;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Inject;
import javax.inject.Qualifier;

import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.ConfigurationException;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.util.Modules;

// This test case explores a overriding of a binding defined in a parent. Two options are explored.
//
// Modules.override, a function defined by Guice
//
// Define fake binding in parent and correct binding in a child.
public class GuiceOverrideBindingTest {

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ThisAnnotationisForAFakeBinding {
        // This annotation is used to define a binding the parent that can be
        // "overriden" in a child.
    }

    static class BindingModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(IRequestData1.class).to(RequestData1a.class);
            bind(IRequestData2.class).annotatedWith(ThisAnnotationisForAFakeBinding.class).to(RequestData2a.class);
        }
    }

    public static interface IRequestData1 {
    }

    public static class RequestData1a implements IRequestData1 {
    }

    public static class RequestData1b implements IRequestData1 {
    }

    public static interface IRequestData2 {
    }

    public static class RequestData2a implements IRequestData2 {
    }

    public static class RequestData2b implements IRequestData2 {
    }

    static class TestObject1 {
        final IRequestData1 requestData;

        @Inject
        TestObject1(final IRequestData1 requestData) {
            this.requestData = requestData;
        }
    }

    static class TestObject2 {
        final IRequestData2 requestData;

        @Inject
        TestObject2(final IRequestData2 requestData) {
            this.requestData = requestData;
        }
    }

    @Test
    public void testOverride() throws Exception {
        Injector injector1 = Guice.createInjector(new BindingModule());
        assertTrue(injector1.getInstance(TestObject1.class).requestData instanceof RequestData1a);

        Injector injector2 = Guice.createInjector(Modules.override(new BindingModule()).with(new AbstractModule() {
            @Override
            protected void configure() {
                bind(IRequestData1.class).to(RequestData1b.class);
            }
        }));
        assertTrue(injector2.getInstance(TestObject1.class).requestData instanceof RequestData1b);
    }

    @Test
    public void testFakeAnnotationInParent_binding() throws Exception {
        Injector injector1 = Guice.createInjector(new BindingModule());
        try {
            injector1.getInstance(TestObject2.class);
            fail("No binding, so exception expected");
        } catch (ConfigurationException e) {
            // @formatter:off
         /*
          *  com.google.inject.ConfigurationException: Guice configuration errors:
                1) No implementation for org.mahu.guicetest.GuiceOverrideBindingTest$IRequestData2 was bound.
                  while locating org.mahu.guicetest.GuiceOverrideBindingTest$IRequestData2
                  for the 1st parameter of org.mahu.guicetest.GuiceOverrideBindingTest$TestObject2.<init>(GuiceOverrideBindingTest.java:66)
                  while locating org.mahu.guicetest.GuiceOverrideBindingTest$TestObject2
          */
             // @formatter:on
        }

        Injector injector2 = injector1.createChildInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(IRequestData2.class).to(RequestData2b.class);
            }
        });
        assertTrue(injector2.getInstance(TestObject2.class).requestData instanceof RequestData2b);
    }

    @Test
    public void testFakeAnnotationInParent_bindingSingleton() throws Exception {
        Injector injector1 = Guice.createInjector(new BindingModule());

        Injector injector2 = injector1.createChildInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(IRequestData2.class).to(RequestData2b.class).in(Singleton.class);
            }
        });
        assertTrue(injector2.getInstance(TestObject2.class).requestData instanceof RequestData2b);
    }

    @Test
    public void testFakeAnnotationInParent_providesSingleton() throws Exception {
        Injector injector1 = Guice.createInjector(new BindingModule());

        Injector injector2 = injector1.createChildInjector(new AbstractModule() {
            @Override
            protected void configure() {
            }

            @Provides
            @Singleton
            IRequestData2 providesRequestData2() {
                return new RequestData2b();
            }
        });
        assertTrue(injector2.getInstance(TestObject2.class).requestData instanceof RequestData2b);
    }

}
