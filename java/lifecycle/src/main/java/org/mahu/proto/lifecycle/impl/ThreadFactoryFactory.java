package org.mahu.proto.lifecycle.impl;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.mahu.proto.lifecycle.IThreadFactoryFactory;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.inject.Inject;

public final class ThreadFactoryFactory implements IThreadFactoryFactory {
    
    private final UncaughtExceptionHandler uncaughtExceptionHandler;
    
    @Inject
    public ThreadFactoryFactory(final UncaughtExceptionHandler uncaughtExceptionHandler) {
        this.uncaughtExceptionHandler = uncaughtExceptionHandler;
    }
    
    @Override
    public ThreadFactory createNamedEventBusThreadFactory(final String name) {
        return createNamedThreadFactory(name, uncaughtExceptionHandler);
    }
    
    public static ThreadFactory createNamedThreadFactory(final String name, final UncaughtExceptionHandler uncaughtExceptionHandler) {
        if (name == null || name.isEmpty() || name.indexOf('-')>=0) {
            throw new IllegalArgumentException("Name required and may not contains -, name="+name);
        }
        // %%d resolves to %d after the String.format. This %d will be formatted
        // by ThreadFactoryBuilder.setNameFormat() (see javadoc)
        return new ThreadFactoryBuilder().setNameFormat(String.format("TFT-%s-%%d", name))
                .setUncaughtExceptionHandler(uncaughtExceptionHandler).build();
    }    
    
    public static ExecutorService createSingleThreadExecutor(final String name, final UncaughtExceptionHandler uncaughtExceptionHandler) {
        return Executors.newSingleThreadExecutor(createNamedThreadFactory(name, uncaughtExceptionHandler));
    }
    
    public static String getProvidedName(final Thread t) {
        final String name = t.getName();
        return name.split("-")[1];
    }

}
