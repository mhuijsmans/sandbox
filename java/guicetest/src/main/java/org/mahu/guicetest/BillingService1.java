package org.mahu.guicetest;

import javax.inject.Inject;

class BillingService1 {

    private final ICreditCardProcessor processor;
    private final BillingService2 billingService2;

    @Inject
    BillingService1(ICreditCardProcessor processor, final BillingService2 billingService2) {
        this.processor = processor;
        this.billingService2 = billingService2;
    }

    public void chargeOrder(int amount) {
        System.out.println("### BillingService ###");
        processor.charge(amount);
        final String msg = "BillingService: is BillingService2 is injected: " + (billingService2 != null);
        System.out.println(msg);
        billingService2.chargeOrder(amount);
    }
}
