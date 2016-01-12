package org.mahu.proto.lifecycle.example1.impl;

import java.util.concurrent.atomic.AtomicInteger;

import org.mahu.proto.lifecycle.ILifeCycleService;
import org.mahu.proto.lifecycle.example1.ExampleInterface1;
import org.mahu.proto.lifecycle.example1.ExampleInterface2;
import org.mahu.proto.lifecycle.example1.ExampleInterface3;

import com.google.inject.Inject;

public class ExampleInterface2Impl implements ExampleInterface2, ILifeCycleService {
    
    public static final AtomicInteger cntr = new AtomicInteger(0);

    @Inject
    ExampleInterface2Impl(final ExampleInterface1 exampleInterface1, final ExampleInterface3 exampleInterface3) {
        cntr.incrementAndGet();
    }

    @Override
    public void start() {
    }

    @Override
    public boolean stop() {
        return true;
    }

    @Override
    public void abort() {
    }

}
