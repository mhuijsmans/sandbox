package org.mahu.guicetest;

import javax.inject.Inject;

import com.google.inject.Injector;

class BillingService2 {

    private final ICreditCardProcessor processor;

    // Guice will create this instance, but with new Task() that will not work.
    @Inject
    Task1 task1;

    @Inject
    Task2 task2;

    @Inject
    Injector injector;

    @Inject
    BillingService2(ICreditCardProcessor processor) {
        this.processor = processor;
    }

    public void chargeOrder(int amount) {
        System.out.println("### BillingService2 ###");
        processor.charge(amount);
        task1.act();

        // Create a Task through Guice
        Task1 task = injector.getInstance(Task1.class);
        task.act();

        task2.act();
    }
}