package org.mahu.guicetest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;

// Explore how to support Optional<Class> where the optionality changes in time.
public class GuiceOptionalBindingTest {

    static class GlobalModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(Context.class).in(Singleton.class);
        }

        @Provides
        Optional<Data1> providesData1(Context context) {
            return context.get(Data1.class);
        }

        @Provides
        @Singleton
        Optional<Data2> providesData2(Context context) {
            return context.get(Data2.class);
        }
    }

    public static class Data1 {
    }

    public static class Data2 {
    }

    public static class Context {
        private Object[] objects;

        Context() {
            this.objects = new Object[0];
        }

        void set(Object[] objects) {
            this.objects = objects;
        }

        <T> Optional<T> get(Class<T> clazz) {
            for (final Object o : objects) {
                if (clazz.isAssignableFrom(o.getClass())) {
                    return Optional.of(clazz.cast(o));
                }
            }
            return Optional.empty();
        }
    }

    static class TestObject1 {
        final Optional<Data1> data1;
        final Optional<Data2> data2;

        @Inject
        public TestObject1(final Optional<Data1> data1, final Optional<Data2> data2) {
            this.data1 = data1;
            this.data2 = data2;
        }

    }

    @Test
    public void requestScope() throws Exception {
        Injector injector = Guice.createInjector(new GlobalModule());

        {
            TestObject1 to1 = injector.getInstance(TestObject1.class);
            assertFalse(to1.data1.isPresent());
            assertFalse(to1.data2.isPresent());
        }

        {
            injector.getInstance(Context.class).set(new Object[] { new Data1() });
            TestObject1 to1 = injector.getInstance(TestObject1.class);
            assertTrue(to1.data1.isPresent());
            // to1.data2 is still false, because it os defined as singleton
            assertFalse(to1.data2.isPresent());
        }

    }

}
