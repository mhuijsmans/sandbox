package org.mahu.proto.lifecycle.example1.impl;

import java.util.concurrent.atomic.AtomicInteger;

import org.mahu.proto.lifecycle.ILifeCycleService;
import org.mahu.proto.lifecycle.example1.ExampleInterface1;

import com.google.inject.Inject;

public class ExampleInterface1Impl implements ExampleInterface1, ILifeCycleService {

    public static final AtomicInteger cntr = new AtomicInteger(0);

    @Inject
    public ExampleInterface1Impl() {
        cntr.incrementAndGet();
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
    }

    @Override
    public void abort() {
    }

}
