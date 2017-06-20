package org.mahu.guicetest;

import static org.junit.Assert.assertNotNull;

import javax.inject.Inject;
import javax.inject.Provider;

import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;

public class GuiceRequestResultDataTest {

    static class BindingModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(RequestData.class).in(Singleton.class);
            bind(Data1Provider.class).in(Singleton.class);
            bind(Data1.class).toProvider(Data1Provider.class);
        }
    }

    public static class Data1 {
    }

    public static class Data2 {
    }

    public static class RequestData {
        private Data1 data1;
        private Data2 data2;

        RequestData() {
            clear();
        }

        public void clear() {
            data1 = new Data1();
            data2 = new Data2();
        }

        public Data1 getData1() {
            return data1;
        }

        public Data2 getData2() {
            return data2;
        }
    }

    static class Data1Provider implements Provider<Data1> {

        private final RequestData requestData;

        @Inject
        public Data1Provider(final RequestData requestData) {
            this.requestData = requestData;
        }

        @Override
        public Data1 get() {
            return requestData.getData1();
        }

    }

    static class TestObject {
        final Data1 data1;
        final Data2 data2;

        @Inject
        TestObject(final Data1 data1, final Data2 data2) {
            this.data1 = data1;
            this.data2 = data2;
        }

    }

    // This test case explores exposing data managed through a DataContainer.
    @Test
    public void requestScope() throws Exception {
        Injector injector = Guice.createInjector(new BindingModule());
        TestObject testObject = injector.getInstance(TestObject.class);

        assertNotNull(testObject.data1);
        assertNotNull(testObject.data2);
    }

}
