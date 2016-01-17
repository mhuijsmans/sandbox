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
import org.mahu.proto.lifecycle.example2.ErrorService;
import org.mahu.proto.lifecycle.example2.IErrorRequest;
import org.mahu.proto.lifecycle.example2.ISessionRequest;
import org.mahu.proto.lifecycle.example2.ModuleBindings2;
import org.mahu.proto.lifecycle.example2.RequestService;
import org.mahu.proto.lifecycle.impl.AbstractServiceModule;
import org.mahu.proto.lifecycle.impl.ApiBroker;
import org.mahu.proto.lifecycle.impl.RequestProxyDispatchService;
import org.mahu.proto.lifecycle.impl.ServiceLifeCycleControl;

public class ServiceLifeCycleControlTest {
    
    // The number of ILifeCycleService defined in used ModuleBindings class.
    private final static int NR_OF_SERVICES = 4;

    ApiBroker broker;
    AbstractServiceModule moduleBindings;
    ServiceLifeCycleControl serviceLifeCycleControl;

    @Rule
    public Timeout globalTimeout = Timeout.seconds(30);

    @Before
    public void createServiceLifeCycleManager() {
        broker = new ApiBroker();
        moduleBindings = new ModuleBindings2();
        serviceLifeCycleControl = new ServiceLifeCycleControl(broker, moduleBindings);
    }

    @After
    public void deleteServiceLifeCycleManager() {
        serviceLifeCycleControl.stopServices();
        serviceLifeCycleControl = null;
    }

    @Test
    public void startServices_allServiceStartedInCorrectOrder() {
        serviceLifeCycleControl.startServices();

        assertEquals(NR_OF_SERVICES, serviceLifeCycleControl.getStartedServicesCount());
        assertEquals(EventBusService.class, serviceLifeCycleControl.getStartedServiceClass(0));
        assertEquals(RequestProxyDispatchService.class, serviceLifeCycleControl.getStartedServiceClass(1));
        assertEquals(RequestService.class, serviceLifeCycleControl.getStartedServiceClass(2));
        assertEquals(ErrorService.class, serviceLifeCycleControl.getStartedServiceClass(3));
    }

    @Test
    public void startServices_serviceAreAlreadyStarted_requestIgnored() {
        serviceLifeCycleControl.startServices();

        assertEquals(NR_OF_SERVICES, serviceLifeCycleControl.getStartedServicesCount());

        serviceLifeCycleControl.startServices();
        assertEquals(NR_OF_SERVICES, serviceLifeCycleControl.getStartedServicesCount());
    }

    @Test
    public void stop_afterStart_allServicesAreStoppedInCorrectOrder() {
        serviceLifeCycleControl.startServices();

        serviceLifeCycleControl.stopServices();

        assertEquals(0, serviceLifeCycleControl.getStartedServicesCount());
        assertEquals(NR_OF_SERVICES, serviceLifeCycleControl.getStoppedServicesCount());
        assertEquals(ErrorService.class, serviceLifeCycleControl.getStoppedServiceClass(0));
        assertEquals(RequestService.class, serviceLifeCycleControl.getStoppedServiceClass(1));
        assertEquals(RequestProxyDispatchService.class, serviceLifeCycleControl.getStoppedServiceClass(2));
        assertEquals(EventBusService.class, serviceLifeCycleControl.getStoppedServiceClass(3));
    }

    @Test
    public void stop_serviceAreAlreadyStopped_requestIgnored() {
        serviceLifeCycleControl.startServices();
        serviceLifeCycleControl.stopServices();
        assertEquals(NR_OF_SERVICES, serviceLifeCycleControl.getStoppedServicesCount());
        
        serviceLifeCycleControl.stopServices();
        assertEquals(NR_OF_SERVICES, serviceLifeCycleControl.getStoppedServicesCount());
    }

    @Test
    public void abort_afterStart_allServicesAreAbortedInCorrectOrder() {
        serviceLifeCycleControl.startServices();

        serviceLifeCycleControl.abortServices();

        assertEquals(0, serviceLifeCycleControl.getStartedServicesCount());
        assertEquals(NR_OF_SERVICES, serviceLifeCycleControl.getStoppedServicesCount());
        assertEquals(ErrorService.class, serviceLifeCycleControl.getStoppedServiceClass(0));
        assertEquals(RequestService.class, serviceLifeCycleControl.getStoppedServiceClass(1));
        assertEquals(RequestProxyDispatchService.class, serviceLifeCycleControl.getStoppedServiceClass(2));
        assertEquals(EventBusService.class, serviceLifeCycleControl.getStoppedServiceClass(3));
    }

    @Test
    public void abort_serviceAreAlreadyStopped_requestIgnored() {
        serviceLifeCycleControl.startServices();
        serviceLifeCycleControl.stopServices();
        assertEquals(0, serviceLifeCycleControl.getStartedServicesCount());
        assertEquals(NR_OF_SERVICES, serviceLifeCycleControl.getStoppedServicesCount());

        serviceLifeCycleControl.abortServices();
        assertEquals(NR_OF_SERVICES, serviceLifeCycleControl.getStoppedServicesCount());
    }

    @Test
    public void abort_serviceAreAlreadyAborted_requestIgnored() {
        serviceLifeCycleControl.startServices();
        serviceLifeCycleControl.abortServices();
        assertEquals(0, serviceLifeCycleControl.getStartedServicesCount());
        assertEquals(NR_OF_SERVICES, serviceLifeCycleControl.getStoppedServicesCount());

        serviceLifeCycleControl.abortServices();
        assertEquals(NR_OF_SERVICES, serviceLifeCycleControl.getStoppedServicesCount());
    }

    @Test
    public void startServices_serviceRegisteredInBroker() {
        serviceLifeCycleControl.startServices();

        assertNotNull(broker.resolve(ISessionRequest.class).get());
    }

    @Test
    public void stop_afterStart_serviceUnregisteredInBroker() {
        serviceLifeCycleControl.startServices();

        serviceLifeCycleControl.stopServices();

        assertEquals(Optional.empty(), broker.resolve(ISessionRequest.class));
    }

    @Test
    public void resolve_ISessionRequestServiceIsRegistered_serviceIsFound() {
        serviceLifeCycleControl.startServices();

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
        serviceLifeCycleControl.startServices();

        Optional<ISessionRequest> request = broker.resolve(ISessionRequest.class);
        assertTrue(request.isPresent());
        String result = request.get().process(new String());

        assertEquals(ISessionRequest.RESPONSE, result);
    }

    @Test
    public void process_IErrorRequestServiceIsAvailable_serviceResponseIsReceived() {
        serviceLifeCycleControl.startServices();

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
        serviceLifeCycleControl.startServices();

        Optional<IErrorRequest> request = broker.resolve(IErrorRequest.class);
        assertTrue(request.isPresent());
        serviceLifeCycleControl.stopServices();

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
        serviceLifeCycleControl.startServices();

        Optional<IErrorRequest> request = broker.resolve(IErrorRequest.class);
        assertTrue(request.isPresent());

        assertEquals(IErrorRequest.RESPONSE_OK, request.get().process(IErrorRequest.Test.causeUncaughtException));

    }

}
