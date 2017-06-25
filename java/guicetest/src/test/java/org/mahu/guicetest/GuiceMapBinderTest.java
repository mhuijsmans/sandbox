package org.mahu.guicetest;

import static org.junit.Assert.assertEquals;

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
        SKITTLES, SNICKERS, TWIX
    }

    static class BindingModule extends AbstractModule {

        @Override
        protected void configure() {
            MapBinder<Snacks, Snack> mapbinder = MapBinder.newMapBinder(binder(), Snacks.class, Snack.class);
            mapbinder.addBinding(Snacks.TWIX).toInstance(new Twix());
            mapbinder.addBinding(Snacks.SNICKERS).toProvider(SnickersProvider.class);
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

        public String getName() {
            return NAME;
        }
    }

    static class TestObject {

        private final Map<Snacks, Snack> snacks;
        private final Map<Snacks, Provider<Snack>> snackProviders;

        @Inject
        TestObject(Map<Snacks, Snack> snacks, Map<Snacks, Provider<Snack>> snackProviders) {
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

    @Test
    public void requestScope() throws Exception {
        Injector injector = Guice.createInjector(new BindingModule());
        TestObject testObject = injector.getInstance(TestObject.class);

        assertEquals(Twix.NAME, testObject.getSnackProvider(Snacks.TWIX).get().getName());
        assertEquals(Skittles.NAME, testObject.getSnack(Snacks.SKITTLES).getName());
    }

}
