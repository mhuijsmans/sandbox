package org.mahu.guicetest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import javax.inject.Inject;

import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.google.inject.multibindings.OptionalBinder;

// This test case explores Optional<..>
// Current impression is that a transition from Optional.empty() to Optional not-empty. 
public class GuiceOptionalBinderBindingTest {

    static class BindingModule1 extends AbstractModule {
        @Override
        protected void configure() {
            OptionalBinder.newOptionalBinder(binder(), Renamer.class);
        }
    }

    static class BindingModule2 extends AbstractModule {
        @Override
        protected void configure() {
            OptionalBinder.newOptionalBinder(binder(), Painter.class);
            // next line creates a concrete binding
            bind(Painter.class).in(Singleton.class);
        }
    }

    static class BindingModule3 extends AbstractModule {
        @Override
        protected void configure() {
            OptionalBinder.newOptionalBinder(binder(), Renamer.class);
            bind(Renamer.class).in(Singleton.class);
        }
    }

    static class BindingModule4 extends AbstractModule {
        @Override
        protected void configure() {
            bind(Renamer.class).in(Singleton.class);
        }
    }

    static class Renamer {
    }

    static class Painter {
    }

    static class TestObject {
        final Optional<Renamer> renamer;
        final Optional<Painter> painter;

        @Inject
        TestObject(final Optional<Renamer> renamer, Optional<Painter> painter) {
            this.renamer = renamer;
            this.painter = painter;
        }
    }

    @Test
    public void requestScope() throws Exception {
        {
            Injector injector = Guice.createInjector(new BindingModule1(), new BindingModule2());
            TestObject testObject1 = injector.getInstance(TestObject.class);
            assertNotNull(testObject1.renamer);
            assertFalse(testObject1.renamer.isPresent());
            assertNotNull(testObject1.painter);
            assertTrue(testObject1.painter.isPresent());
        }

        {
            Injector injector = Guice.createInjector(new BindingModule1(), new BindingModule2());
            // In Child add instance for Renamer
            // Child DOES NOT WORK. De tails below.
            Injector childInjector = injector.createChildInjector(new BindingModule4());
            TestObject testObject2 = childInjector.getInstance(TestObject.class);
            assertNotNull(testObject2.renamer);
            assertFalse(testObject2.renamer.isPresent()); // fails. why? Bug?
            assertNotNull(testObject2.painter);
            assertTrue(testObject2.painter.isPresent());
        }

        {
            Injector childInjector = Guice.createInjector(new BindingModule2(), new BindingModule3());
            TestObject testObject2 = childInjector.getInstance(TestObject.class);
            assertNotNull(testObject2.renamer);
            assertTrue(testObject2.renamer.isPresent());
            assertNotNull(testObject2.painter);
            assertTrue(testObject2.painter.isPresent());
        }
    }

}
