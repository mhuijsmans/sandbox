package org.mahu.proto.lifecycle;

import java.lang.Thread.UncaughtExceptionHandler;

import org.mahu.proto.lifecycle.impl.AbstractServiceModule;

public class ModuleBindingFactory implements IModuleBindingFactory {

    @Override
    public AbstractServiceModule createModuleBindings(UncaughtExceptionHandler uncaughtExceptionHandler) {
        return null;
    }

}
