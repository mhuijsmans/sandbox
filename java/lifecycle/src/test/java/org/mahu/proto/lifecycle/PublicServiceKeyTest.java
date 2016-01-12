package org.mahu.proto.lifecycle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;

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
import org.mahu.proto.lifecycle.impl.RequestProxyEvent;
import org.mahu.proto.lifecycle.impl.RequestProxyList;
import org.mahu.proto.lifecycle.impl.RequestProxyService;

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
        threadpool = Executors.newFixedThreadPool(3);
        handler = new EventBusUncaughtExceptionHandler();
        eventBusService = new EventBusService(handler);
        eventBusService.start();
        requestProxyList = new RequestProxyList();
        eventBusService.register(new RequestProxyService(eventBusService));
        PublicServiceKeyFactory factory = new PublicServiceKeyFactory(eventBusService, requestProxyList);
        requestService = new RequestService(eventBusService, factory);
        eventBusService.register(requestService);
        requestProxyList.allowedExecutionRequests();
        key = requestService.getPublicServiceKey();
    }

    @After
    public void cleanup() {
        eventBusService.stop();
        eventBusService = null;
        handler = null;
        requestProxyList = null;
        key = null;
        threadpool.isShutdown();
    }

    private class AsyncWaitOnLock implements Callable<Void> {
        MeetUpLock lock = new MeetUpLock();

        @Override
        public Void call() {
            key.object.waitOnLock(lock);
            return null;
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

    @Test
    public void process_abort_exception() throws InterruptedException, ExecutionException {
        AsyncWaitOnLock asyncWaitOnLock = new AsyncWaitOnLock();
        Future<Void> future = threadpool.submit(asyncWaitOnLock);
        asyncWaitOnLock.lock.waitForWaitOnLockCall();
        requestProxyList.abortAllRequests();
        asyncWaitOnLock.lock.signalWaitOnLockToContinue();
        try {
            future.get();
        } catch (ExecutionException e) {
            assertEquals(RequestProxyEvent.REQUEST_EXECUTE_ABORT_REASON, e.getCause().getMessage());
        }
    }

}
