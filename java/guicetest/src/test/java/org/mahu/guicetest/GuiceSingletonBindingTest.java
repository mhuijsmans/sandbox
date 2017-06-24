package org.mahu.guicetest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;

// This test case explores different Singleton patterns
// For @Provides both $javax.inject.Singleton and @com.google.inject.Singleton work. 
public class GuiceSingletonBindingTest {

    static class ParentModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(Data1.class).in(Singleton.class);
            bind(IData2.class).toInstance(new Data2());
        }

        @Provides
        Data3 providesData3() {
            return new Data3();
        }

        @Provides
        @Singleton
        Data4 providesData4() {
            return new Data4();
        }
    }

    public static class Data1 {
    }

    public static interface IData2 {
    }

    public static class Data2 implements IData2 {
    }

    public static class Data3 {
    }

    public static class Data4 {
    }

    static class ChildModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(Data5.class).in(Singleton.class);
            bind(IData6.class).toInstance(new Data6());
        }

        @Provides
        @Singleton // com.google.inject.Singleton
        Data7 providesData7() {
            return new Data7();
        }

        @Provides
        @javax.inject.Singleton // javax.inject.Singleton
        Data8 providesData8() {
            return new Data8();
        }
    }

    public static class Data5 {
    }

    public static interface IData6 {
    }

    public static class Data6 implements IData6 {
    }

    public static class Data7 {
    }

    public static class Data8 {
    }

    @Test
    public void requestScope() throws Exception {
        Injector injector = Guice.createInjector(new ParentModule());

        // Check the (singleton) bindings defined in the parent.
        assertEquals(injector.getInstance(Data1.class), injector.getInstance(Data1.class));
        assertEquals(injector.getInstance(IData2.class), injector.getInstance(IData2.class));
        assertNotEquals(injector.getInstance(Data3.class), injector.getInstance(Data3.class));
        assertEquals(injector.getInstance(Data4.class), injector.getInstance(Data4.class));

        Injector childInjector1 = injector.createChildInjector(new ChildModule());

        // A child can have singletons
        assertEquals(childInjector1.getInstance(Data5.class), childInjector1.getInstance(Data5.class));
        assertEquals(childInjector1.getInstance(IData6.class), childInjector1.getInstance(IData6.class));
        assertEquals(childInjector1.getInstance(Data7.class), childInjector1.getInstance(Data7.class));
        assertEquals(childInjector1.getInstance(Data8.class), childInjector1.getInstance(Data8.class));

        Injector childInjector2 = injector.createChildInjector(new ChildModule());

        assertEquals(childInjector2.getInstance(Data5.class), childInjector2.getInstance(Data5.class));
        assertEquals(childInjector2.getInstance(IData6.class), childInjector2.getInstance(IData6.class));
        assertEquals(childInjector2.getInstance(Data7.class), childInjector2.getInstance(Data7.class));
        assertEquals(childInjector2.getInstance(Data8.class), childInjector2.getInstance(Data8.class));

        // Child singletons are unique per child.
        assertNotEquals(childInjector1.getInstance(Data5.class), childInjector2.getInstance(Data5.class));
        assertNotEquals(childInjector1.getInstance(IData6.class), childInjector2.getInstance(IData6.class));
        assertNotEquals(childInjector1.getInstance(Data7.class), childInjector2.getInstance(Data7.class));
        assertNotEquals(childInjector1.getInstance(Data8.class), childInjector2.getInstance(Data8.class));
    }

}
