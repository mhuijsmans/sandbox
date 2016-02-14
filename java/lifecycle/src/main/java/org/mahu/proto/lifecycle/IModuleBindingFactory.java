package org.mahu.proto.lifecycle;

import java.lang.Thread.UncaughtExceptionHandler;

import org.mahu.proto.lifecycle.impl.AbstractServiceModule;

public interface IModuleBindingFactory {

    /**
     * Create new ModuleBinding for Guice, with the provided
     * UncaughtExceptionHandler
     * 
     * @param uncaughtExceptionHandler
     * @return AbstractServiceModule
     */
    public AbstractServiceModule createModuleBindings(final UncaughtExceptionHandler uncaughtExceptionHandler);

}
