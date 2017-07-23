package org.mahu.guicetest;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

// This test case explores a bind chain. Saw an example. But .....
public class GuiceReBindingTest {

    static class BindingModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(IData.class).to(Data1.class);
            // I have seen an example
            bind(Data1.class).to(Data2.class);
        }
    }

    static interface IData {
    }

    static class Data1 implements IData {
    }

    static class Data2 extends Data1 {
    }

    @Test
    public void testReBinding() throws Exception {
        Injector injector = Guice.createInjector(new BindingModule());
        assertTrue(injector.getInstance(IData.class) instanceof Data1);
        //
        // Injector childInjector = injector.createChildInjector(new
        // AbstractModule() {
        // @Override
        // protected void configure() {
        // bind(Data1.class).to(Data2.class);
        // }
        // });
        // assertTrue(childInjector.getInstance(IData.class) instanceof Data2);
    }

}
