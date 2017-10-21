package org.mahu.guicetest;

import javax.inject.Inject;

import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

// This test case explores basic binding. See modules for details
public class GuiceBindingTest {

    static class BindingModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(Data1.class);
            // Data 2 (default constructor) is not bound, but can be injected
            // Data 3 (@Inject constructor) is not bound, but is/can be injected
            // Data 4 (@Inject members) is not bound, but is/can be injected
            bind(TestObject.class);
            bind(IData5.class).to(Data5.class);
            bind(IData6.class).to(Data6.class);
        }
    }

    static class Data1 {
    }

    static class Data2 {
    }

    static class Data3 {
        @Inject
        Data3(Data1 data1, IData6 data6) {

        }
    }

    static class Data4 {
        @Inject
        Data1 data1;
    }

    static interface IData5 {
    }

    static class Data5 implements IData5 {

        @Inject
        Data5(IData6 data6) {

        }
    }

    static interface IData6 {
    }

    static class Data6 implements IData6 {
    }

    static class TestObject {

        @Inject
        TestObject(Data1 data1, Data1 data2, Data3 data3, Data4 data4, IData5 data5a, Data5 data5b) {
        }
    }

    @Test
    public void testBasicBinding() throws Exception {
        Injector injector = Guice.createInjector(new BindingModule());
        TestObject testObject = injector.getInstance(TestObject.class);
        // cal method on test object

        injector.getInstance(IData5.class);
        injector.getInstance(Data5.class);
    }

}
