package org.mahu.guicetest;

import static org.junit.Assert.assertEquals;

import javax.inject.Inject;

import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;

// This test case explores FactoryModuleBuilder.
// Source: https://google.github.io/guice/api-docs/4.1/javadoc/com/google/inject/assistedinject/FactoryModuleBuilder.html
public class GuiceGenericBindingTest {

    static class BindingModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(new TypeLiteral<Converter<String, Integer>>() {
            }).to(RealConverter.class);
        }
    }

    public interface Converter<T, S> {

        int hi();
    }

    public static class RealConverter implements Converter<String, Integer> {

        public static final int HI = 10;

        @Override
        public int hi() {
            return HI;
        }

    }

    public static class TestClass {
        private final Converter<String, Integer> converter;

        @Inject
        TestClass(Converter<String, Integer> converter) {
            this.converter = converter;
        }

        int hi() {
            return converter.hi();
        }

    }

    @Test
    public void injectFactory() throws Exception {
        Injector injector = Guice.createInjector(new BindingModule());

        TestClass testClass = injector.getInstance(TestClass.class);

        assertEquals(RealConverter.HI, testClass.hi());

    }

}
