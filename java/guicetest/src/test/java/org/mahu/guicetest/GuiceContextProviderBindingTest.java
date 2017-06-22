package org.mahu.guicetest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.ProvisionException;

// Explore how to inject objects from an array.
// It uses providers defined in the Module
// It makes use of a childInjector.
public class GuiceContextProviderBindingTest {

    static class GlobalModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(GlobalData.class).in(Singleton.class);
        }
    }

    static class ContextModule extends AbstractModule {
        private final Context context;

        ContextModule(final Context context) {
            this.context = context;
        }

        @Override
        protected void configure() {
            bind(Context.class).toInstance(context);
        }

        // A provider can also require injected data, see
        // https://github.com/caplin/Guice-Example/blob/master/src/main/java/com/guiceexample/injection/MyModule.java
        @Provides
        Data1 providesData1() {
            return context.get(Data1.class);
        }

        @Provides
        Data2 providesData2() {
            return context.get(Data2.class);
        }

        @Provides
        Data3 providesData3() {
            return context.get(Data3.class);
        }
    }

    public static class GlobalData {
    }

    public static class Data1 {
    }

    public static class Data2 {
    }

    public static class Data3 {
    }

    public static class Context {
        private final Object[] objects;

        Context(Object[] objects) {
            this.objects = objects;
        }

        <T> T get(Class<T> clazz) {
            for (final Object o : objects) {
                if (clazz.isAssignableFrom(o.getClass())) {
                    return clazz.cast(o);
                }
            }
            return null;
        }
    }

    static class TestObject1 {
        final Data1 data1;
        final Data2 data2;

        @Inject
        TestObject1(final Data1 data1, final Data2 data2) {
            this.data1 = data1;
            this.data2 = data2;
        }

    }

    static class TestObject2 {
        final Data3 data3;

        @Inject
        TestObject2(final Data3 data3) {
            this.data3 = data3;
        }

    }

    @Test
    public void requestScope() throws Exception {
        Injector injector = Guice.createInjector(new GlobalModule());

        Object[] objects = { new Data1(), new Data2() };

        Injector childInjector = injector.createChildInjector(new ContextModule(new Context(objects)));

        TestObject1 testObject1 = childInjector.getInstance(TestObject1.class);
        TestObject1 testObject2 = childInjector.getInstance(TestObject1.class);

        // Check that provider can be used to get 2 objects.
        assertEquals(objects[0], testObject1.data1);
        assertEquals(objects[1], testObject1.data2);

        assertEquals(objects[0], testObject2.data1);
        assertEquals(objects[1], testObject2.data2);

        try {
            childInjector.getInstance(TestObject2.class);
            fail("Exception expected because ");
        } catch (ProvisionException e) {
            // expected behavior
        }
    }

}
