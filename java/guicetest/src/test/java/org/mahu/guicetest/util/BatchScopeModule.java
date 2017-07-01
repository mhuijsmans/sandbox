package org.mahu.guicetest.util;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

public class BatchScopeModule extends AbstractModule {
    public void configure() {
        SimpleScope batchScope = new SimpleScope();

        // tell Guice about the scope
        bindScope(BatchScoped.class, batchScope);

        // make our scope instance injectable
        bind(SimpleScope.class).annotatedWith(Names.named("batchScope")).toInstance(batchScope);
    }
}