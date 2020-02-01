package org.mahu.closeable;

import java.io.IOException;

public class TestIntegerAutoCloseable implements AutoCloseable {

    private TestIntegerWithGlobalCounter counter;
    private final Runnable runnable;

    public TestIntegerAutoCloseable(final TestIntegerWithGlobalCounter value) {
        this(value, null);
    }
    
    public TestIntegerAutoCloseable(final TestIntegerWithGlobalCounter value, final Runnable runnable) {
        this.counter = value;
        this.runnable = runnable;
    }    

    public void doSomeWork() {
        if (runnable!=null) {
        	runnable.run();
        }
    }

    @Override
    public void close() throws IOException {
    	counter.setTrue();
    }

}
