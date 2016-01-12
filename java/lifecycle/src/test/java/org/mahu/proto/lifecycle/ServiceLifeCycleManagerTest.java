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
import org.junit.Test;
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
import org.mahu.proto.lifecycle.impl.RequestProxyService;
import org.mahu.proto.lifecycle.impl.ServiceLifeCycleManager;

public class ServiceLifeCycleManagerTest {

    ApiBroker broker;
    AbstractServiceModule moduleBindings;
    ServiceLifeCycleManager serviceLifeCycleManager;
    
//    @Rule
//    public Timeout globalTimeout = Timeout.seconds(10);      

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
        assertEquals(new LogEntry(Event.start, RequestProxyService.class), EventLog.get(1));
        assertEquals(new LogEntry(Event.start, RequestService.class), EventLog.get(2));
    }

    @Test
    public void stop_afterStart_allServicesAreStoppedInCorrectOrder() {
        serviceLifeCycleManager.startServices();

        serviceLifeCycleManager.stopServices();

        assertEquals(6, EventLog.size());
        assertEquals(new LogEntry(Event.start, EventBusService.class), EventLog.get(0));
        assertEquals(new LogEntry(Event.start, RequestProxyService.class), EventLog.get(1));
        assertEquals(new LogEntry(Event.start, RequestService.class), EventLog.get(2));
        assertEquals(new LogEntry(Event.stop, RequestService.class), EventLog.get(3));
        assertEquals(new LogEntry(Event.stop, RequestProxyService.class), EventLog.get(4));
        assertEquals(new LogEntry(Event.stop, EventBusService.class), EventLog.get(5));
    }

    @Test
    public void abort_afterStart_allServicesAreAbortedInCorrectOrder() {
        serviceLifeCycleManager.startServices();

        serviceLifeCycleManager.abortServices();

        assertEquals(6, EventLog.size());
        assertEquals(new LogEntry(Event.start, EventBusService.class), EventLog.get(0));
        assertEquals(new LogEntry(Event.start, RequestProxyService.class), EventLog.get(1));
        assertEquals(new LogEntry(Event.start, RequestService.class), EventLog.get(2));
        assertEquals(new LogEntry(Event.abort, RequestService.class), EventLog.get(3));
        assertEquals(new LogEntry(Event.abort, RequestProxyService.class), EventLog.get(4));
        assertEquals(new LogEntry(Event.abort, EventBusService.class), EventLog.get(5));
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
    public void ISessionRequest_servicesStarted_correctResponse() {
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
    public void ISessionRequest_servicesNotStarted_correctResponse() {
        EventLog.clear();

        Optional<ISessionRequest> request = broker.resolve(ISessionRequest.class);
        assertFalse(request.isPresent());
    }

    @Test
    public void IErrorRequest_process_exceptionThrown() {
        serviceLifeCycleManager.startServices();
        EventLog.clear();

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
    public void IErrorRequest_processThrowsException_exceptionReceived() {
        serviceLifeCycleManager.startServices();
        EventLog.clear();

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
    public void IErrorRequest_processCrossesServicesStopped_postRejectedExceptionReceived() throws InterruptedException {
        serviceLifeCycleManager.startServices();
        EventLog.clear();

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
    
    @Test
    public void IErrorRequest_processCausingUncaughtException_exceptionOnNextCall() {
        serviceLifeCycleManager.startServices();
        EventLog.clear();

        Optional<IErrorRequest> request = broker.resolve(IErrorRequest.class);
        assertTrue(request.isPresent());

        assertEquals(IErrorRequest.RESPONSE_OK, request.get().process(IErrorRequest.Test.causeUncaughtException));
    }     

    // Test that requested get's processed by dead event handler.
}
