package org.mahu.proto.lifecycle;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.Subscribe;

public class GuaveEventBusTest {

    private static class TestSubscriber {

        @Subscribe
        public void process(String event) {
        }
    }

    ExecutorService executor;
    AsyncEventBus eventBus;
    TestSubscriber sub;

    @Before
    public void prepare() {
        executor = Executors.newSingleThreadExecutor();
        eventBus = new AsyncEventBus(executor);
        sub = new TestSubscriber();
    }

    @After
    public void cleanup() {
        executor.shutdownNow();
        eventBus = null;
        sub = null;
    }

    @Test
    public void register_ok() {
        eventBus.register(sub);
    }

    @Test
    public void unregister_afterRegisterok() {
        eventBus.register(sub);

        eventBus.unregister(sub);
    }
    
    @Test
    public void register_afterShutdown_ok() {
        executor.shutdown();
        assertTrue(executor.isTerminated());        
        
        eventBus.register(sub);
    }     

    /**
     * Posting on a stopped EventBus will a subscriber results in an Exception.
     */
    @Test
    public void post_onShutdownEventBusWithNoRegisteredSubscriber_rejectedExecutionException() {
        assertFalse(executor.isShutdown());
        executor.shutdown();
        assertTrue(executor.isTerminated());

        try {
            eventBus.post(new String());
        } catch (RejectedExecutionException e) {
            fail("No exception is thrown when posting on not shutdown eventbus without Subscriber");
        }
    }
    
    /**
     * Posting on a stopped EventBus will a subscriber results in an Exception.
     */
    @Test
    public void post_onShutdownEventBusWithRegisteredSubscriber_rejectedExecutionException() {
        eventBus.register(sub);
        assertFalse(executor.isShutdown());
        executor.shutdown();
        assertTrue(executor.isTerminated());

        try {
            eventBus.post(new String());
            fail("Exception should be thrown when posting on not active eventbus");
        } catch (RejectedExecutionException e) {
            assertTrue(true);
        }
    }     

    /**
     * Posting on a stopped EventBus will a subscriber results in an Exception.
     */
    @Test
    public void post_onShutdownNowEventBusWithRegisteredSubscriber_rejectedExecutionException() {
        eventBus.register(sub);
        assertFalse(executor.isShutdown());
        executor.shutdownNow();
        assertTrue(executor.isTerminated());

        try {
            eventBus.post(new String());
            fail("Exception should be trown when posting on not active eventbus");
        } catch (RejectedExecutionException e) {
            assertTrue(true);
        }
    }
    
    /**
     * Posting on a stopped EventBus will NO subscriber is ok.
     */
    @Test
    public void post_onShutdownNowEventBusWithNoRegisteredSubscriber_rejectedExecutionException() {
        assertFalse(executor.isShutdown());
        executor.shutdownNow();
        assertTrue(executor.isTerminated());

        try {
            eventBus.post(new String());
        } catch (RejectedExecutionException e) {
            fail("No exception is thrown when posting on not shutdown eventbus without Subscriber");
        }
    }    

}
