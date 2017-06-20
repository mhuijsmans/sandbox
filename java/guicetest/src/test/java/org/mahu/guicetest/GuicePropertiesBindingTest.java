package org.mahu.guicetest;

import static org.junit.Assert.assertEquals;

import java.util.Properties;

import javax.inject.Inject;
import javax.inject.Named;

import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;

public class GuicePropertiesBindingTest {

    private static final int PORT = 8080;
    private static final String HOST = "HOST";

    static class BindingModule extends AbstractModule {
        @Override
        protected void configure() {
            Properties p = new Properties();
            p.setProperty("port", Integer.toString(PORT));
            p.setProperty("host", HOST);

            Names.bindProperties(binder(), p);
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

    // This test case explores binding of constant values using Properties &
    // Names.bindProperties().
    @Test
    public void requestScope() throws Exception {
        Injector injector = Guice.createInjector(new BindingModule());
        TestObject testObject = injector.getInstance(TestObject.class);

        assertEquals(PORT, testObject.port);
        assertEquals(HOST, testObject.host);
    }

}
