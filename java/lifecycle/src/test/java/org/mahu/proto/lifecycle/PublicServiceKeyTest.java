package org.mahu.proto.lifecycle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.mahu.proto.lifecycle.example2.EventBusService;
import org.mahu.proto.lifecycle.example2.EventBusUncaughtExceptionHandler;
import org.mahu.proto.lifecycle.example2.ISessionRequest;
import org.mahu.proto.lifecycle.example2.ISessionRequest.MeetUpLock;
import org.mahu.proto.lifecycle.example2.ISessionRequest.SessionRequestException;
import org.mahu.proto.lifecycle.example2.RequestService;
import org.mahu.proto.lifecycle.impl.PublicServiceKeyFactory;
import org.mahu.proto.lifecycle.impl.RequestProxy;
import org.mahu.proto.lifecycle.impl.RequestProxyDispatchService;
import org.mahu.proto.lifecycle.impl.RequestProxyEvent;
import org.mahu.proto.lifecycle.impl.RequestProxyList;

public class PublicServiceKeyTest {

    @Rule
    public Timeout globalTimeout = Timeout.seconds(30);

    ExecutorService threadpool;
    EventBusUncaughtExceptionHandler handler;
    EventBusService eventBusService;
    RequestProxyList requestProxyList;
    RequestService requestService;
    PublicServiceKey<ISessionRequest> key;

    @Before
    public void prepare() {
        threadpool = Executors.newFixedThreadPool(2);
        handler = new EventBusUncaughtExceptionHandler();
        eventBusService = new EventBusService(handler);
        eventBusService.start();
        requestProxyList = new RequestProxyList();
        RequestProxyDispatchService requestProxyDispatchService = new RequestProxyDispatchService(eventBusService);
        PublicServiceKeyFactory factory = new PublicServiceKeyFactory(eventBusService, requestProxyList);
        requestService = new RequestService(eventBusService, factory);
        eventBusService.register(requestService);
        requestProxyList.allowedExecutionRequests();
        requestProxyDispatchService.start();
        requestService.start();
        key = requestService.getPublicServiceKey();
    }

    @After
    public void cleanup() {
        eventBusService.stop();
        eventBusService = null;
        handler = null;
        requestProxyList = null;
        key = null;
        threadpool.shutdownNow();
    }

    private static class AsyncWaitOnLock implements Callable<Void> {
        MeetUpLock lock = new MeetUpLock();
        private final PublicServiceKey<ISessionRequest> key;

        AsyncWaitOnLock(final PublicServiceKey<ISessionRequest> key) {
            this.key = key;
        }

        @Override
        public Void call() {
            this.key.object.waitOnLock(lock);
            return null;
        }
    }

    private static class AsyncProcess implements Callable<String> {
        private final PublicServiceKey<ISessionRequest> key;

        AsyncProcess(final PublicServiceKey<ISessionRequest> key) {
            this.key = key;
        }

        @Override
        public String call() {
            return this.key.object.process("hi");
        }
    }

    @Test
    public void process_correctResponse() {
        final String msg = "hi";
        final String response = key.object.process(msg);

        assertEquals(ISessionRequest.RESPONSE + msg, response);
    }

    @Test
    public void process_calledTwice_exceptionThrown() {
        final String msg = "hi";
        final String response = key.object.process(msg);
        assertEquals(ISessionRequest.RESPONSE + msg, response);

        try {
            key.object.process(msg);
            fail("Because method is called twice, exception shall be trown");
        } catch (RuntimeException e) {
            assertEquals(RequestProxy.METHOD_CALLED_TWICE_REASON, e.getMessage());
        }
    }

    @Test
    public void throwResourceWithMessage_exceptionWithCorrectMessage() {
        final String msg = "iamTheExceptionMessage";
        try {
            key.object.throwResourceWithMessage(msg);
            fail("Method should throw exception");
        } catch (SessionRequestException e) {
            assertEquals(msg, e.getMessage());
        }
    }

    @Test
    public void process_rejectExecutionRequests_exception() {
        requestProxyList.rejectExecutionRequests();

        final String msg = "hi";
        try {
            key.object.process(msg);
            fail("Because of reject, exception should be thrown");
        } catch (RejectedExecutionException e) {
            assertEquals(RequestProxyList.REJECT_REQUEST_REASON, e.getMessage());
        }
    }

    /**
     * This test case deal with a abort a 2 request, one request that is
     * executing on the EventBus and a second request that is posted( thus queued), ready to
     * execute on EventBus, but is not started yet.
     */
    @Test
    public void process_abortPostedRequest_exception() throws InterruptedException, ExecutionException {
        final AsyncWaitOnLock asyncWaitOnLock1 = new AsyncWaitOnLock(key);
        final Future<Void> future1 = threadpool.submit(asyncWaitOnLock1);
        asyncWaitOnLock1.lock.waitForWaitOnLockCall();

        final PublicServiceKey<ISessionRequest> key2 = requestService.getPublicServiceKey();
        final AsyncProcess asyncProcess = new AsyncProcess(key2);
        final Future<String> future2 = threadpool.submit(asyncProcess);
        while (requestProxyList.size() != 2) {
            TimeUnit.MICROSECONDS.sleep(1);
        }

        requestProxyList.abortAllRequests();
        asyncWaitOnLock1.lock.signalWaitOnLockToContinue();

        try {
            future1.get();
            fail("Get shall throw exception because of abort");
        } catch (ExecutionException e) {
            assertEquals(RequestProxyEvent.REQUEST_EXECUTE_ABORT_REASON, e.getCause().getMessage());
        }

        try {
            future2.get();
            fail("Get shall throw exception because of abort");
        } catch (ExecutionException e) {
            assertEquals(RequestProxyEvent.REQUEST_EXECUTE_ABORT_REASON, e.getCause().getMessage());
        }
    }

}
