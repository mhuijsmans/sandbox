package org.mahu.proto.lifecycle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.Subscribe;
import com.google.common.eventbus.SubscriberExceptionContext;
import com.google.common.eventbus.SubscriberExceptionHandler;

public class GuaveEventBusTest {

    @Rule
    public Timeout globalTimeout = Timeout.seconds(30);

    private static class TestSubscriber1 {

        @Subscribe
        public void process(String event) {
        }
    }

    private static class EventLoggerBase {
        final List<String> eventLog;
        final CountDownLatch latch;
        final Object object = new Object();

        EventLoggerBase(List<String> eventLog, CountDownLatch latch) {
            this.eventLog = eventLog;
            this.latch = latch;
        }

        protected void log(String method) {
            synchronized (object) {
                // Next line is useful for debugging
                // System.out.println(method);
                eventLog.add(method);
                latch.countDown();
            }
        }
    }

    private static class TestSubscriber2 extends EventLoggerBase {

        private static final String PROCESS1 = "process1";
        private static final String PROCESS2 = "process2";
        private static final String PROCESS3 = "process3";
        private static final String PROCESS4 = "process4";

        TestSubscriber2() {
            this(new LinkedList<>());
        }

        TestSubscriber2(List<String> calledMethods) {
            super(calledMethods, new CountDownLatch(4));
        }

        @Subscribe
        public void process1(Object event) {
            log(PROCESS1);
        }

        @Subscribe
        public void EventBase(Event event) {
            log(PROCESS2);
        }

        @Subscribe
        public void process3(Object event) {
            log(PROCESS3);
        }

        @Subscribe
        public void process4(EventBase event) {
            log(PROCESS4);
        }
    }

    private static class TestSubscriber3 extends TestSubscriber2 {
        TestSubscriber3(List<String> calledMethods) {
            super(calledMethods);
        }
    }

    private class TestSubscriber4 extends EventLoggerBase {

        private static final String PROCESS = "TestSubscriber4.process";
        private boolean hasUnregistered = false;

        TestSubscriber4() {
            super(new LinkedList<>(), new CountDownLatch(2));
        }

        @Subscribe
        public void process(String event) {
            log(PROCESS);
            if (!hasUnregistered) {
                eventBus.unregister(this);
                hasUnregistered = true;
            }
        }
    }

    private static class TestDeadEventSubscriber extends EventLoggerBase {
        private static final String DEADEVENT = "TestDeadEventSubscriber.deadEvent";

        TestDeadEventSubscriber() {
            super(new LinkedList<>(), new CountDownLatch(1));
        }

        @Subscribe
        public void EventBase(DeadEvent event) {
            log(DEADEVENT);
        }
    }

    private static class TestThrowExceptionSubscriber extends EventLoggerBase {

        private static final String EXCEPTION = "TestThrowExceptionSubscriber.exception";

        TestThrowExceptionSubscriber() {
            super(new LinkedList<>(), new CountDownLatch(1));
        }

        @Subscribe
        public void process(String event) {
            log(EXCEPTION);
            throw new RuntimeException();
        }
    }

    private class TestSubscriberExceptionHandler extends EventLoggerBase implements SubscriberExceptionHandler {

        private static final String EXCEPTION = "TestSubscriber4.handleException";

        TestSubscriberExceptionHandler() {
            super(new LinkedList<>(), new CountDownLatch(1));
        }

        @Override
        public void handleException(Throwable exception, SubscriberExceptionContext context) {
            log(EXCEPTION);
        }

    }

    private static class EventBase extends Object {
    }

    private static class Event extends EventBase {
    }

    ExecutorService executor;
    AsyncEventBus eventBus;
    TestSubscriber1 sub;
    TestSubscriberExceptionHandler exceptionHandler;

    @Before
    public void prepare() {
        exceptionHandler = new TestSubscriberExceptionHandler();
        executor = Executors.newSingleThreadExecutor();
        eventBus = new AsyncEventBus(executor, exceptionHandler);
        sub = new TestSubscriber1();
    }

    @After
    public void cleanup() {
        executor.shutdownNow();
        eventBus = null;
        sub = null;
        assertEquals(0, exceptionHandler.eventLog.size());
        exceptionHandler = null;
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

    /**
     * This test case verifies that when a Object get's posted, that a class
     * hierarchy defines the order in which they are delivered. In test case
     * first method(Event), method(EventBase) and next method(Object)
     */
    @Test
    public void post_oneSubscriber2_hierarchicalDeliveryOrder() throws InterruptedException {
        TestSubscriber2 sub2 = new TestSubscriber2();
        eventBus.register(sub2);

        eventBus.post(new Event());
        sub2.latch.await();

        assertEquals(TestSubscriber2.PROCESS2, sub2.eventLog.get(0));
        assertEquals(TestSubscriber2.PROCESS4, sub2.eventLog.get(1));
        // Observed that order is not fixed
        assertTrue(sub2.eventLog.contains(TestSubscriber2.PROCESS1));
        assertTrue(sub2.eventLog.contains(TestSubscriber2.PROCESS3));
    }

    /**
     * Delivery of a posted Object when multiple Subscribers have subscribed, is
     * hierarchical, which is consistent with previous test case.
     */
    @Test
    public void post_twoSubscribers_hierarchicalDeliveryOrder() throws InterruptedException {
        TestSubscriber2 sub2 = new TestSubscriber2();
        TestSubscriber3 sub3 = new TestSubscriber3(sub2.eventLog);
        eventBus.register(sub2);
        eventBus.register(sub3);

        eventBus.post(new Event());
        sub2.latch.await();
        sub3.latch.await();

        assertEquals(TestSubscriber2.PROCESS2, sub2.eventLog.get(0));
        assertEquals(TestSubscriber2.PROCESS2, sub2.eventLog.get(1));
        assertEquals(TestSubscriber2.PROCESS4, sub2.eventLog.get(2));
        assertEquals(TestSubscriber2.PROCESS4, sub2.eventLog.get(3));
    }

    @Test
    public void post_twoStringsPostedSubscribersUnregistersAfterFirst_stillDeliveredTwice()
            throws InterruptedException {
        TestSubscriber4 sub4 = new TestSubscriber4();
        eventBus.register(sub4);

        eventBus.post(new String());
        // after subscriber has processed first post, it will unregister. But at
        // post, the events are added to the event queue and thus it is also
        // delivered to subscriber that has unregistered.
        eventBus.post(new String());
        sub4.latch.await();

        assertEquals(TestSubscriber4.PROCESS, sub4.eventLog.get(0));
        assertEquals(TestSubscriber4.PROCESS, sub4.eventLog.get(1));
    }

    @Test
    public void post_onlyDeadSubscriberRegistered_deliveredToDeadEventSubscriber() throws InterruptedException {
        TestDeadEventSubscriber deadSub = new TestDeadEventSubscriber();
        eventBus.register(deadSub);

        eventBus.post(new String());
        deadSub.latch.await();

        assertEquals(TestDeadEventSubscriber.DEADEVENT, deadSub.eventLog.get(0));
    }

    @Test
    public void post_subscriberThrowsException_hhhhr() throws InterruptedException {
        TestThrowExceptionSubscriber throwExceptionSub = new TestThrowExceptionSubscriber();
        eventBus.register(throwExceptionSub);

        eventBus.post(new String());
        // Synchronize with Subscriber and ExceptionHandler. Otherwise test case
        // will fail, because test case continues execution before exception is
        // thrown.
        throwExceptionSub.latch.await();
        exceptionHandler.latch.await();

        assertEquals(TestThrowExceptionSubscriber.EXCEPTION, throwExceptionSub.eventLog.get(0));
        assertEquals(1, exceptionHandler.eventLog.size());
        exceptionHandler.eventLog.clear();
    }

}
