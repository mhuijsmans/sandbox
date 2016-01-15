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

    private class AsyncWaitOnLock implements Callable<Void> {
        final MeetUpLock lock = new MeetUpLock();
        final PublicServiceKey<ISessionRequest> key = requestService.getPublicServiceKey();

        @Override
        public Void call() {
            this.key.object.waitOnLock(lock);
            return null;
        }
    }

    private class AsyncProcess implements Callable<String> {
        final PublicServiceKey<ISessionRequest> key = requestService.getPublicServiceKey();

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
     * This test in included because certain errors were only detected when
     * called test were only detected when run many times. That is causes by
     * threading / timing.
     */
    @Test
    public void manyCalls_to_process_abortPostedRequest_exception() throws InterruptedException, ExecutionException {
        for (int i = 0; i < 100; i++) {
            process_abortPostedRequest_exception();
        }

    }

    /**
     * This test case deal with a abort of 2 requests. One request that is
     * executing on the EventBus when the calls is aborted. The request is posted (thus
     * queued on EventBus queue), ready to execute on EventBus, but is not started yet.
     */
    @Test
    public void process_abortPostedRequest_exception() throws InterruptedException, ExecutionException {
        final AsyncWaitOnLock asyncWaitOnLock = new AsyncWaitOnLock();
        final Future<Void> futureWaitOnLock = threadpool.submit(asyncWaitOnLock);
        asyncWaitOnLock.lock.waitForWaitOnLockCall();

        final AsyncProcess asyncProcess = new AsyncProcess();
        final Future<String> futureProcess = threadpool.submit(asyncProcess);
        while (requestProxyList.size() != 2) {
            TimeUnit.NANOSECONDS.sleep(1);
        }

        assertEquals(2, requestProxyList.abortAllRequests());
        asyncWaitOnLock.lock.signalWaitOnLockToContinue();

        try {
            futureProcess.get();
            fail("futureProcess shall throw exception because of abort");
        } catch (ExecutionException e) {
            assertEquals(RequestProxyEvent.REQUEST_EXECUTE_ABORT_REASON, e.getCause().getMessage());
        }

        try {
            futureWaitOnLock.get();
            fail("futureWaitOnLock shall throw exception because of abort");
        } catch (ExecutionException e) {
            assertEquals(RequestProxyEvent.REQUEST_EXECUTE_ABORT_REASON, e.getCause().getMessage());
        }
    }

}
