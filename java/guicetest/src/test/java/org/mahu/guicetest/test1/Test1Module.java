package org.mahu.guicetest.test1;

import com.google.inject.AbstractModule;

public class Test1Module extends AbstractModule {
    @Override
    protected void configure() {
        bind(ITestClass.class).to(PackageScopeTestClass.class);
    }
}