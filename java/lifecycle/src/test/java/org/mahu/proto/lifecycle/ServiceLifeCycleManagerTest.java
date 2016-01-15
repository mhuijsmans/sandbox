package org.mahu.proto.lifecycle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Optional;
import java.util.concurrent.RejectedExecutionException;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.mahu.proto.lifecycle.example2.EventBusService;
import org.mahu.proto.lifecycle.example2.EventLog;
import org.mahu.proto.lifecycle.example2.EventLog.Event;
import org.mahu.proto.lifecycle.example2.EventLog.LogEntry;
import org.mahu.proto.lifecycle.example2.IErrorRequest;
import org.mahu.proto.lifecycle.example2.ISessionRequest;
import org.mahu.proto.lifecycle.example2.ModuleBindings2;
import org.mahu.proto.lifecycle.example2.RequestService;
import org.mahu.proto.lifecycle.impl.AbstractServiceModule;
import org.mahu.proto.lifecycle.impl.ApiBroker;
import org.mahu.proto.lifecycle.impl.RequestProxyDispatchService;
import org.mahu.proto.lifecycle.impl.ServiceLifeCycleManager;

public class ServiceLifeCycleManagerTest {

    ApiBroker broker;
    AbstractServiceModule moduleBindings;
    ServiceLifeCycleManager serviceLifeCycleManager;

    @Rule
    public Timeout globalTimeout = Timeout.seconds(30);

    @Before
    public void createServiceLifeCycleManager() {
        EventLog.clear();
        broker = new ApiBroker();
        moduleBindings = new ModuleBindings2();
        serviceLifeCycleManager = new ServiceLifeCycleManager(broker, moduleBindings);
    }

    @After
    public void deleteServiceLifeCycleManager() {
        serviceLifeCycleManager.stopServices();
        serviceLifeCycleManager = null;
    }

    @Test
    public void startServices_allServiceStartedInCorrectOrder() {
        serviceLifeCycleManager.startServices();

        assertEquals(3, EventLog.size());
        assertEquals(new LogEntry(Event.start, EventBusService.class), EventLog.get(0));
        assertEquals(new LogEntry(Event.start, RequestProxyDispatchService.class), EventLog.get(1));
        assertEquals(new LogEntry(Event.start, RequestService.class), EventLog.get(2));
    }

    @Test
    public void startServices_serviceAreAlreadyStarted_requestIgnored() {
        serviceLifeCycleManager.startServices();

        assertEquals(3, EventLog.size());

        serviceLifeCycleManager.startServices();
        assertEquals(3, EventLog.size());
    }

    @Test
    public void stop_afterStart_allServicesAreStoppedInCorrectOrder() {
        serviceLifeCycleManager.startServices();

        serviceLifeCycleManager.stopServices();

        assertEquals(6, EventLog.size());
        assertEquals(new LogEntry(Event.start, EventBusService.class), EventLog.get(0));
        assertEquals(new LogEntry(Event.start, RequestProxyDispatchService.class), EventLog.get(1));
        assertEquals(new LogEntry(Event.start, RequestService.class), EventLog.get(2));
        assertEquals(new LogEntry(Event.stop, RequestService.class), EventLog.get(3));
        assertEquals(new LogEntry(Event.stop, RequestProxyDispatchService.class), EventLog.get(4));
        assertEquals(new LogEntry(Event.stop, EventBusService.class), EventLog.get(5));
    }

    @Test
    public void stop_serviceAreAlreadyStopped_requestIgnored() {
        serviceLifeCycleManager.startServices();
        serviceLifeCycleManager.stopServices();
        assertEquals(6, EventLog.size());

        serviceLifeCycleManager.stopServices();
        assertEquals(6, EventLog.size());
    }

    @Test
    public void abort_afterStart_allServicesAreAbortedInCorrectOrder() {
        serviceLifeCycleManager.startServices();

        serviceLifeCycleManager.abortServices();

        assertEquals(6, EventLog.size());
        assertEquals(new LogEntry(Event.start, EventBusService.class), EventLog.get(0));
        assertEquals(new LogEntry(Event.start, RequestProxyDispatchService.class), EventLog.get(1));
        assertEquals(new LogEntry(Event.start, RequestService.class), EventLog.get(2));
        assertEquals(new LogEntry(Event.abort, RequestService.class), EventLog.get(3));
        assertEquals(new LogEntry(Event.abort, RequestProxyDispatchService.class), EventLog.get(4));
        assertEquals(new LogEntry(Event.abort, EventBusService.class), EventLog.get(5));
    }

    @Test
    public void abort_serviceAreAlreadyStopped_requestIgnored() {
        serviceLifeCycleManager.startServices();
        serviceLifeCycleManager.stopServices();
        assertEquals(6, EventLog.size());

        serviceLifeCycleManager.abortServices();
        assertEquals(6, EventLog.size());
    }

    @Test
    public void abort_serviceAreAlreadyAborted_requestIgnored() {
        serviceLifeCycleManager.startServices();
        serviceLifeCycleManager.abortServices();
        assertEquals(6, EventLog.size());

        serviceLifeCycleManager.abortServices();
        assertEquals(6, EventLog.size());
    }

    @Test
    public void startServices_serviceRegisteredInBroker() {
        serviceLifeCycleManager.startServices();

        assertNotNull(broker.resolve(ISessionRequest.class).get());
    }

    @Test
    public void stop_afterStart_serviceUnregisteredInBroker() {
        serviceLifeCycleManager.startServices();

        serviceLifeCycleManager.stopServices();

        assertEquals(Optional.empty(), broker.resolve(ISessionRequest.class));
    }

    @Test
    public void resolve_ISessionRequestServiceIsRegistered_serviceIsFound() {
        serviceLifeCycleManager.startServices();

        Optional<ISessionRequest> request = broker.resolve(ISessionRequest.class);

        assertTrue(request.isPresent());
    }

    @Test
    public void resolve_ISessionRequestServiceIsNotRegistered_serviceIsNotFound() {
        Optional<ISessionRequest> request = broker.resolve(ISessionRequest.class);

        assertFalse(request.isPresent());
    }

    @Test
    public void process_ISessionRequestServiceIsAvailable_correctResponse() {
        serviceLifeCycleManager.startServices();
        EventLog.clear();

        Optional<ISessionRequest> request = broker.resolve(ISessionRequest.class);
        assertTrue(request.isPresent());
        String result = request.get().process(new String());

        assertEquals(ISessionRequest.RESPONSE, result);
        assertEquals(1, EventLog.size());
        assertEquals(new LogEntry(Event.event, RequestService.class), EventLog.get(0));
    }

    @Test
    public void process_IErrorRequestServiceIsAvailable_serviceResponseIsReceived() {
        serviceLifeCycleManager.startServices();

        Optional<IErrorRequest> request = broker.resolve(IErrorRequest.class);
        assertTrue(request.isPresent());

        try {
            request.get().process(IErrorRequest.Test.throwException);
            fail("Expected exception");
        } catch (RuntimeException e) {
            assertEquals(IErrorRequest.THROW_EXCEPTION_MSG, e.getMessage());
        }
    }

    @Test
    public void process_IErrorRequestProcessCrossesServicesStopped_postRejectedExceptionReceived()
            throws InterruptedException {
        serviceLifeCycleManager.startServices();

        Optional<IErrorRequest> request = broker.resolve(IErrorRequest.class);
        assertTrue(request.isPresent());
        serviceLifeCycleManager.stopServices();

        try {
            request.get().process(IErrorRequest.Test.throwException);
            fail("Expected exception");
        } catch (RejectedExecutionException e) {
            assertTrue(true);
        }
    }

    /**
     * This test case verifies the following scenario. The invoked services post
     * a message on the EventBus. Processing of that message results in an
     * Exception that is caught by the EventBus.
     */
    @Test
    public void IErrorRequest_processCausingUncaughtException_exceptionOnNextCall() {
        serviceLifeCycleManager.startServices();
        EventLog.clear();

        Optional<IErrorRequest> request = broker.resolve(IErrorRequest.class);
        assertTrue(request.isPresent());

        assertEquals(IErrorRequest.RESPONSE_OK, request.get().process(IErrorRequest.Test.causeUncaughtException));

    }

}
