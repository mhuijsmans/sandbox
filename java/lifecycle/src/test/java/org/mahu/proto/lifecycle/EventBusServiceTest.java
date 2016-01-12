package org.mahu.proto.lifecycle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.mahu.proto.lifecycle.example2.EventBusService;
import org.mahu.proto.lifecycle.example2.EventBusUncaughtExceptionHandler;

import com.google.common.eventbus.Subscribe;

public class EventBusServiceTest {

    private final static int EVENT_MAX_WAIT_IN_MS = 10000;

    @Rule
    public Timeout globalTimeout = Timeout.seconds(30);

    EventBusUncaughtExceptionHandler handler;
    EventBusService eventBusService;

    static class TestSubscriber {
        private Optional<String> lastData = Optional.empty();
        private Object lock = new Object();

        @Subscribe
        public void process(final String data) {
            synchronized (lock) {
                lastData = Optional.ofNullable(data);
                lock.notify();
            }
        }

        public Optional<String> getData() {
            synchronized (lock) {
                while (!lastData.isPresent()) {
                    try {
                        lock.wait(EVENT_MAX_WAIT_IN_MS);
                    } catch (InterruptedException e) {
                        // JUnit may interrupt this
                    }
                }
                final Optional<String> tmp = lastData;
                lastData = Optional.empty();
                return tmp;
            }
        }

    }

    @Before
    public void prepare() {
        handler = new EventBusUncaughtExceptionHandler();
        eventBusService = new EventBusService(handler);
    }

    @After
    public void cleanup() {
        eventBusService.stop();
        eventBusService = null;
        handler = null;
    }

    @Test
    public void start_noException() {
        eventBusService.start();

        assertEquals(0, handler.getExceptionCounter());
    }

    @Test
    public void stop_afterStart_noException() {
        eventBusService.start();

        assertTrue(eventBusService.stop());

        assertEquals(0, handler.getExceptionCounter());
    }

    @Test
    public void post_text_processed() {
        eventBusService.start();
        final TestSubscriber testSub = new TestSubscriber();
        eventBusService.register(testSub);
        
        final String event = "hi";
        eventBusService.post(event);
        
        Optional<String> result = testSub.getData();
        assertTrue(result.isPresent());
        assertEquals(event, result.get());
    }

}
