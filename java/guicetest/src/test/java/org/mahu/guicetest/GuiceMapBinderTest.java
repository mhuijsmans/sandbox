package org.mahu.guicetest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;

import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.multibindings.MapBinder;

// This test case explores MapBinder
// Source: http://google.github.io/guice/api-docs/latest/javadoc/com/google/inject/multibindings/MapBinder.html
public class GuiceMapBinderTest {

    enum Snacks {
        SKITTLES, SNICKERS, TWIX, MARS
    }

    static class BindingModule1 extends AbstractModule {

        @Override
        protected void configure() {
            MapBinder<Snacks, Snack> mapbinder = MapBinder.newMapBinder(binder(), Snacks.class, Snack.class);
            mapbinder.addBinding(Snacks.TWIX).toInstance(new Twix());
            mapbinder.addBinding(Snacks.SNICKERS).toProvider(SnickersProvider.class);
            mapbinder.addBinding(Snacks.SKITTLES).to(Skittles.class);
        }
    }

    static class BindingModule2 extends AbstractModule {

        @Override
        protected void configure() {
            MapBinder<Snacks, Snack> mapbinder = MapBinder.newMapBinder(binder(), Snacks.class, Snack.class);
            mapbinder.addBinding(Snacks.SKITTLES).to(Skittles.class);
        }
    }

    interface Snack {
        String getName();
    }

    static class Twix implements Snack {
        private static final String NAME = "TWIX";

        public String getName() {
            return NAME;
        }
    }

    static class SnickersProvider implements Provider<Snickers> {

        @Override
        public Snickers get() {
            return new Snickers();
        }
    }

    static class Snickers implements Snack {
        private static final String NAME = "SNICKERS";

        public String getName() {
            return NAME;
        }
    }

    static class Skittles implements Snack {
        private static final String NAME = "SKITTLES";
        public static int counter = 0;

        Skittles() {
            counter++;
        }

        public String getName() {
            return NAME;
        }
    }

    static class TestObject1 {

        private final Map<Snacks, Snack> snacks;
        private final Map<Snacks, Provider<Snack>> snackProviders;

        @Inject
        TestObject1(Map<Snacks, Snack> snacks, Map<Snacks, Provider<Snack>> snackProviders) {
            this.snacks = snacks;
            this.snackProviders = snackProviders;
        }

        Snack getSnack(final Snacks key) {
            return snacks.get(key);
        }

        Provider<Snack> getSnackProvider(final Snacks key) {
            return snackProviders.get(key);
        }

    }

    static class TestObject2 {

        private final Map<Snacks, Provider<Snack>> snackProviders;

        @Inject
        TestObject2(Map<Snacks, Provider<Snack>> snackProviders) {
            this.snackProviders = snackProviders;
        }

        Provider<Snack> getSnackProvider(final Snacks key) {
            return snackProviders.get(key);
        }

    }

    @Test
    public void keyExists() throws Exception {
        Injector injector = Guice.createInjector(new BindingModule1());
        TestObject1 testObject = injector.getInstance(TestObject1.class);

        assertEquals(Twix.NAME, testObject.getSnackProvider(Snacks.TWIX).get().getName());
        assertEquals(Skittles.NAME, testObject.getSnack(Snacks.SKITTLES).getName());
    }

    @Test
    public void keyDoesNotExists() throws Exception {
        Injector injector = Guice.createInjector(new BindingModule1());
        TestObject1 testObject = injector.getInstance(TestObject1.class);

        assertNull(testObject.getSnack(Snacks.MARS));
    }

    @Test
    public void test_objectInMapIsCreatedLazy() throws Exception {
        assertEquals(0, Skittles.counter);
        Injector injector = Guice.createInjector(new BindingModule2());
        assertEquals(0, Skittles.counter);
        TestObject2 testObject = injector.getInstance(TestObject2.class);
        assertEquals(0, Skittles.counter);
        // At this moment the object is created lazy
        testObject.getSnackProvider(Snacks.SKITTLES).get();
        assertEquals(1, Skittles.counter);
    }

}
