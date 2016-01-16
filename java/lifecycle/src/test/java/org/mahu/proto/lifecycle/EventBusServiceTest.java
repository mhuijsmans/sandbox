package org.mahu.proto.lifecycle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.annotation.AnnotationFormatError;
import java.util.Optional;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.mahu.proto.lifecycle.example2.EventBusService;
import org.mahu.proto.lifecycle.example2.EventBusUncaughtExceptionHandler;
import org.mahu.proto.lifecycle.impl.ReadyAbortLock;

import com.google.common.eventbus.Subscribe;

public class EventBusServiceTest {

    private final static int EVENT_MAX_WAIT_IN_MS = 10000;

    @Rule
    public Timeout globalTimeout = Timeout.seconds(30);

    EventBusUncaughtExceptionHandler handler;
    EventBusService eventBusService;
    
    static class ErrorEvent {
        final Throwable t;
        ErrorEvent(final Throwable t) {
            this.t = t;
        }
    }
    
    static class TestErrorSubscriber {
        private ReadyAbortLock lock = new ReadyAbortLock();

        @Subscribe
        public void process(final ErrorEvent event) throws Throwable {
            synchronized (lock) {
                lock.ready();
            }
            throw event.t;
        }

        public void waitForEventReceived() {
            synchronized (lock) {
                lock.wait3(EVENT_MAX_WAIT_IN_MS);
            }
        }
    }    
    
    static class TestSubscriber {
        private Optional<String> lastData = Optional.empty();
        private ReadyAbortLock lock = new ReadyAbortLock();

        @Subscribe
        public void process(final String data) {
            synchronized (lock) {
                lastData = Optional.ofNullable(data);
                lock.ready();
            }
        }

        public Optional<String> getData() {
            synchronized (lock) {
                lock.wait3(EVENT_MAX_WAIT_IN_MS);
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

        eventBusService.stop();

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
    
    @Test
    public void subscriberThrowsRunTimeException_exceptionIsCaught() {
        eventBusService.start();
        final TestErrorSubscriber testSub = new TestErrorSubscriber();
        eventBusService.register(testSub);
        
        eventBusService.post(new ErrorEvent(new RuntimeException()));
        handler.waitForExceptionCaught(EVENT_MAX_WAIT_IN_MS);
 
        assertEquals(1, handler.getExceptionCounter());
        assertTrue(handler.getLastThrownException().isPresent());
        assertTrue(handler.getLastThrownException().get() instanceof RuntimeException);
        assertTrue(handler.getSubscriberExceptionContext().isPresent());          
        assertTrue(handler.getSubscriberExceptionContext().get().getSubscriber() instanceof TestErrorSubscriber);         
    }
    
    @Test
    public void subscriberThrowsException_exceptionIsCaught() {
        eventBusService.start();
        final TestErrorSubscriber testSub = new TestErrorSubscriber();
        eventBusService.register(testSub);
        
        eventBusService.post(new ErrorEvent(new Exception()));
        handler.waitForExceptionCaught(EVENT_MAX_WAIT_IN_MS);
 
        assertEquals(1, handler.getExceptionCounter());
        assertTrue(handler.getLastThrownException().isPresent());
        assertTrue(handler.getLastThrownException().get() instanceof Exception);   
        assertTrue(handler.getSubscriberExceptionContext().isPresent());          
        assertTrue(handler.getSubscriberExceptionContext().get().getSubscriber() instanceof TestErrorSubscriber);        
    }
    
    @Test
    public void subscriberThrowsThrowable_exceptionIsCaught() {
        eventBusService.start();
        final TestErrorSubscriber testSub = new TestErrorSubscriber();
        eventBusService.register(testSub);
        
        eventBusService.post(new ErrorEvent(new Throwable()));
        handler.waitForExceptionCaught(EVENT_MAX_WAIT_IN_MS);
 
        assertEquals(1, handler.getExceptionCounter());
        assertTrue(handler.getLastThrownException().isPresent());
        assertTrue(handler.getLastThrownException().get() instanceof Throwable);    
        assertTrue(handler.getSubscriberExceptionContext().isPresent());          
        assertTrue(handler.getSubscriberExceptionContext().get().getSubscriber() instanceof TestErrorSubscriber);
    }
    
    @Test
    public void subscriberThrowsError_exceptionIsCaught() {
        eventBusService.start();
        final TestErrorSubscriber testSub = new TestErrorSubscriber();
        eventBusService.register(testSub);
        
        eventBusService.post(new ErrorEvent(new Error()));
        handler.waitForExceptionCaught(EVENT_MAX_WAIT_IN_MS);
 
        assertEquals(1, handler.getExceptionCounter());
        assertTrue(handler.getLastThrownException().isPresent());
        assertTrue(handler.getLastThrownException().get() instanceof Error);
        assertFalse(handler.getSubscriberExceptionContext().isPresent());          
    }
    
    @Test
    public void subscriberThrowsAnnotationFormatError_exceptionIsCaught() {
        eventBusService.start();
        final TestErrorSubscriber testSub = new TestErrorSubscriber();
        eventBusService.register(testSub);
        
        // AnnotationFormatError extends Error
        eventBusService.post(new ErrorEvent(new AnnotationFormatError("DoNotCare")));
        handler.waitForExceptionCaught(EVENT_MAX_WAIT_IN_MS);
 
        assertEquals(1, handler.getExceptionCounter());
        assertTrue(handler.getLastThrownException().isPresent());
        assertTrue(handler.getLastThrownException().get() instanceof AnnotationFormatError);
        assertFalse(handler.getSubscriberExceptionContext().isPresent());          
    }

}
