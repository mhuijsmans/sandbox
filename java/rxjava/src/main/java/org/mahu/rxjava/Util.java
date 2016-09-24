package org.mahu.rxjava;

public class Util {

    public static void delayInMS(int i) {
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
        }
        ;
    }

}
