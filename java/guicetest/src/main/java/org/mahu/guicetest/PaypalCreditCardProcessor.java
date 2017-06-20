package org.mahu.guicetest;

public class PaypalCreditCardProcessor implements ICreditCardProcessor {

    @Override
    public void charge(int amount) {
        System.out.println("Charging: " + amount);
    }

}
