package org.mahu.guicetest;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.junit.Assert.assertEquals;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.inject.Inject;
import javax.inject.Named;

import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.BindingAnnotation;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;

// This test case explores binding is constant value for basic data type & string.
// Both Names and custom annotation are used.
public class GuiceConstantBindingTest {

    private static final String PORT = "8080";
    private static final String HOST = "HOST";
    private static final String SERVICE = "SERVICE";

    @BindingAnnotation
    @Target({ FIELD, PARAMETER, METHOD })
    @Retention(RUNTIME)
    public @interface DemoAnnotation {

    }

    static class BindingModule extends AbstractModule {
        @Override
        protected void configure() {
            // Here string is bound. Guice support type conversion. In this test
            // from String to int.
            bindConstant().annotatedWith(Names.named("port")).to(PORT);
            // next line uses different style
            bind(String.class).annotatedWith(Names.named("host")).toInstance(HOST);
            bindConstant().annotatedWith(DemoAnnotation.class).to(SERVICE);
        }
    }

    static class TestObject {
        final int port;
        final String host;
        final String service;

        @Inject
        TestObject(@Named("port") final int port, @Named("host") final String host,
                @DemoAnnotation final String service) {
            this.port = port;
            this.host = host;
            this.service = service;
        }
    }

    @Test
    public void requestScope() throws Exception {
        Injector injector = Guice.createInjector(new BindingModule());
        TestObject testObject = injector.getInstance(TestObject.class);

        assertEquals(Integer.valueOf(PORT).intValue(), testObject.port);
        assertEquals(HOST, testObject.host);
        assertEquals(SERVICE, testObject.service);
    }

}
