package org.mahu.proto.lifecycle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.mahu.proto.lifecycle.ILifeCycleManager.LifeCycleState;
import org.mahu.proto.lifecycle.example2.ExceptionInStartService;
import org.mahu.proto.lifecycle.example2.IErrorRequest;
import org.mahu.proto.lifecycle.example2.ISessionRequest;
import org.mahu.proto.lifecycle.example2.ModuleBindings3;
import org.mahu.proto.lifecycle.impl.ApiBroker;
import org.mahu.proto.lifecycle.impl.LifeCycleManager;

public class LifeCycleManagerTest {

    private static final String MSG = "message";

    private final static int TESTCASE_TIMEOUT_IN_MS = 30 * 1000;

    @Rule
    public Timeout globalTimeout = Timeout.seconds(30);

    ApiBroker broker;
    ModuleBindings3 moduleBindings;
    ILifeCycleManager lifeCycleManager;

    @Before
    public void createObjects() {
        broker = new ApiBroker();
        moduleBindings = new ModuleBindings3();
        lifeCycleManager = new LifeCycleManager(broker, moduleBindings);
    }

    @After
    public void deleteObjects() {
        broker = null;
        moduleBindings = null;
        lifeCycleManager = null;
    }

    @Test
    public void initialStatusIsInit() {
        assertEquals(LifeCycleState.init, lifeCycleManager.getStatus().getState());
        assertEquals(0, lifeCycleManager.getStatus().getServicesStartCount());
        assertEquals(0, lifeCycleManager.getStatus().getExceptionCount());
    }

    @Test
    public void startup_noError_stateIsRunning() throws InterruptedException, ExecutionException {
        lifeCycleManagerStartUp();
        assertEquals(LifeCycleState.running, lifeCycleManager.getStatus().getState());
        assertEquals(1, lifeCycleManager.getStatus().getServicesStartCount());
        assertTrue(lifeCycleManager.getStatus().getServicesStartCount() > 0);
    }
    
    @Test
    public void startup_exceptionInStartedService_servicesAbortedAndStateIsFatal() throws InterruptedException, ExecutionException {
        moduleBindings.bindAsSingleton(ExceptionInStartService.class);
        lifeCycleManager.startUp();
        TestUtils.pollingWait(() -> lifeCycleManager.getStatus().getExceptionCount() == 1, TESTCASE_TIMEOUT_IN_MS);
        
        TestUtils.pollingWait(() -> lifeCycleManager.getStatus().getState() == LifeCycleState.fatal, TESTCASE_TIMEOUT_IN_MS);           
        assertEquals(LifeCycleState.fatal, lifeCycleManager.getStatus().getState());
        assertEquals(0, lifeCycleManager.getStatus().getServicesStartCount());
    }
    
    @Test
    public void shutdown_afterStartup_stateIsXYZ() throws InterruptedException, ExecutionException {
        lifeCycleManagerStartUp();
        assertEquals(LifeCycleState.running, lifeCycleManager.getStatus().getState());

        assertTrue(lifeCycleManager.getActiveServiceCount() > 0);
        lifeCycleManager.shutdown();
    }    

    @Test
    public void resolve_stateInit_resolvedFailed() {
        assertFalse(broker.resolve(ISessionRequest.class).isPresent());
    }

    @Test
    public void executeRequest_startRunning_correctResponse() throws InterruptedException, ExecutionException {
        lifeCycleManagerStartUp();
        Optional<ISessionRequest> request = broker.resolve(ISessionRequest.class);

        assertTrue(request.isPresent());
        assertEquals(ISessionRequest.RESPONSE + MSG, request.get().process(MSG));
    }

    @Test
    public void executeRequest_exception_exceptionReceivedAndServiceNotRestarted() throws InterruptedException, ExecutionException {
        lifeCycleManagerStartUp();
        Optional<IErrorRequest> request = broker.resolve(IErrorRequest.class);

        assertTrue(request.isPresent());
        try {
            request.get().process(IErrorRequest.Test.throwException);
            fail("Invoked method shall throw exception");
        } catch (RuntimeException e) {
            assertEquals(IErrorRequest.THROW_EXCEPTION_MSG, e.getMessage());
        }
        assertEquals(LifeCycleState.running, lifeCycleManager.getStatus().getState());
    }
    
    @Test
    public void executeRequest_uncaughtException_requestIsOkButServiceAreRestarted() throws InterruptedException, ExecutionException {
        lifeCycleManagerStartUp();
        Optional<IErrorRequest> request = broker.resolve(IErrorRequest.class);
        assertEquals(0, lifeCycleManager.getStatus().getExceptionCount());

        assertEquals(IErrorRequest.RESPONSE_OK, request.get().process(IErrorRequest.Test.causeUncaughtException));
        TestUtils.pollingWait(() -> lifeCycleManager.getStatus().getExceptionCount() == 1, TESTCASE_TIMEOUT_IN_MS);
        assertEquals(1, lifeCycleManager.getStatus().getExceptionCount());
        TestUtils.pollingWait(() -> lifeCycleManager.getStatus().getServicesStartCount() == 2, TESTCASE_TIMEOUT_IN_MS);
        assertEquals(2, lifeCycleManager.getStatus().getServicesStartCount());
        assertEquals(LifeCycleState.running, lifeCycleManager.getStatus().getState());
    }
    
    @Test
    public void executeRequest_uncaughtException_afterRestartServicesAreAvailable() throws InterruptedException, ExecutionException {
        lifeCycleManagerStartUp();
        TestUtils.pollingWait(() -> lifeCycleManager.getStatus().getServicesStartCount() == 1, TESTCASE_TIMEOUT_IN_MS);
        Optional<IErrorRequest> request1 = broker.resolve(IErrorRequest.class);
        assertEquals(IErrorRequest.RESPONSE_OK, request1.get().process(IErrorRequest.Test.causeUncaughtException));
        TestUtils.pollingWait(() -> lifeCycleManager.getStatus().getServicesStartCount() == 2, TESTCASE_TIMEOUT_IN_MS);
        
        Optional<ISessionRequest> request2 = broker.resolve(ISessionRequest.class);
        assertTrue(request2.isPresent());
        assertEquals(ISessionRequest.RESPONSE + MSG, request2.get().process(MSG));        
        }    
    
    private void lifeCycleManagerStartUp() {
        final int servicesStartCount = lifeCycleManager.getStatus().getServicesStartCount();
        lifeCycleManager.startUp();
        TestUtils.pollingWait(() -> lifeCycleManager.getStatus().getServicesStartCount() != servicesStartCount, TESTCASE_TIMEOUT_IN_MS);        
    }

}
