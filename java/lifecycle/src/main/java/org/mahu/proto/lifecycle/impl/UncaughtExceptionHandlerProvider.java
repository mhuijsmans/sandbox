package org.mahu.proto.lifecycle.impl;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Optional;

import com.google.inject.Provider;

final class UncaughtExceptionHandlerProvider implements Provider<UncaughtExceptionHandler> {
    private Optional<UncaughtExceptionHandler> uncaughtExceptionHandler = Optional.empty();
    
    boolean isPresent() {
        return uncaughtExceptionHandler.isPresent();
    }
    
    void setUncaughtExceptionHandler(final UncaughtExceptionHandler uncaughtExceptionHandler) {
        this.uncaughtExceptionHandler = Optional.of(uncaughtExceptionHandler);
    }    
    
    @Override
    public UncaughtExceptionHandler get() {
        return uncaughtExceptionHandler.get();
    }
}
