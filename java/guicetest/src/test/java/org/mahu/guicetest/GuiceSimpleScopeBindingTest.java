package org.mahu.guicetest;

import javax.inject.Inject;
import javax.inject.Named;

import org.junit.Test;
import org.mahu.guicetest.util.BatchScopeModule;
import org.mahu.guicetest.util.SimpleScope;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;

// This test case explores a overriding on bindings
// source: https://stackoverflow.com/questions/483087/overriding-binding-in-guice/531110#531110
public class GuiceSimpleScopeBindingTest {

    static class SomeObject {

    }

    static class TestObject {
        private final SimpleScope scope;

        @Inject
        TestObject(@Named("batchScope") SimpleScope scope) {
            this.scope = scope;
        }

        /**
         * Runs {@code runnable} in batch scope.
         */
        public void scopeRunnable(Runnable runnable) {
            scope.enter();
            try {
                // explicitly seed some seed objects...
                scope.seed(Key.get(SomeObject.class), new SomeObject());

                // create and access scoped objects
                runnable.run();

            } finally {
                scope.exit();
            }
        }
    }

    @Test
    public void testOverride() throws Exception {
        Injector injector = Guice.createInjector(new BatchScopeModule());

        TestObject to = injector.getInstance(TestObject.class);
        to.scopeRunnable(() -> {
        });
    }

}
