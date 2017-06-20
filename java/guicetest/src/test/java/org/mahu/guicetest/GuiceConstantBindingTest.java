package org.mahu.guicetest;

import static org.junit.Assert.assertEquals;

import javax.inject.Inject;
import javax.inject.Named;

import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;

public class GuiceConstantBindingTest {

    private static final int PORT = 8080;
    private static final String HOST = "HOST";

    static class BindingModule extends AbstractModule {
        @Override
        protected void configure() {
            bindConstant().annotatedWith(Names.named("port")).to(PORT);
            // next line uses different style
            bind(String.class).annotatedWith(Names.named("host")).toInstance(HOST);
        }
    }

    static class TestObject {
        final int port;
        final String host;

        @Inject
        TestObject(@Named("port") final int port, @Named("host") final String host) {
            this.port = port;
            this.host = host;
        }
    }

    // This test case explores binding is constant value for basic data type &
    // string
    @Test
    public void requestScope() throws Exception {
        Injector injector = Guice.createInjector(new BindingModule());
        TestObject testObject = injector.getInstance(TestObject.class);

        assertEquals(PORT, testObject.port);
        assertEquals(HOST, testObject.host);
    }

}
