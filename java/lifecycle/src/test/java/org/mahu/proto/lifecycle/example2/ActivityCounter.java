package org.mahu.proto.lifecycle.example2;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ActivityCounter {

    private final static List<AtomicInteger> cntrs = new LinkedList<>();

    public static AtomicInteger createCounter() {
        synchronized (cntrs) {
            final AtomicInteger counter = new AtomicInteger();
            cntrs.add(counter);
            return counter;
        }
    }
    
    public static void clearCounters() {
        synchronized (cntrs) {
            for(AtomicInteger i : cntrs) {
                i.set(0);
            }
        }
    }    

}
