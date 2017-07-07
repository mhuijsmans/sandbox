package org.mahu.guicetest;

import javax.inject.Inject;

import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

// This test case explores basic binding. See modules for details
public class GuiceEnumInterfaceBindingTest {

    static class BindingModule extends AbstractModule {
        @Override
        protected void configure() {
        }
    }

    static interface IData {
    }

    static class Data implements IData {
    }

    enum EData implements IData {
    };

    static class TestObject {

        @Inject
        TestObject(IData data) {
        }

    }

    @Test
    public void bindInterfaceToClass() throws Exception {
        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(IData.class).to(Data.class);
            }
        });
        injector.getInstance(TestObject.class);
    }

    @Test
    public void bindClass() throws Exception {
        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(Data.class);
            }
        });
        injector.getInstance(TestObject.class);
    }

}
