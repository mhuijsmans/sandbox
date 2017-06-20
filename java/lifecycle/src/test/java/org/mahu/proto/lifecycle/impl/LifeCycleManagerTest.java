package org.mahu.proto.lifecycle.impl;

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
import org.mahu.proto.lifecycle.ILifeCycleManager;
import org.mahu.proto.lifecycle.ILifeCycleManager.LifeCycleState;
import org.mahu.proto.lifecycle.ILifeCycleManagerStatus;
import org.mahu.proto.lifecycle.TestUtils;
import org.mahu.proto.lifecycle.example2.ExceptionInStartService;
import org.mahu.proto.lifecycle.example2.IErrorRequest;
import org.mahu.proto.lifecycle.example2.ISessionRequest;
import org.mahu.proto.lifecycle.example2.ModuleBindings3;

public class LifeCycleManagerTest {

    private static final String MSG = "message";

    private final static int POLLINGWAIT_TIMEOUT_IN_MS = 10 * 1000;

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
        assertEquals(LifeCycleState.INIT, lifeCycleManager.getStatus().getState());
        assertEquals(0, lifeCycleManager.getStatus().getServicesStartCount());
        assertEquals(0, lifeCycleManager.getStatus().getUncaughtExceptionCount());
    }

    @Test
    public void startup_noError_stateIsRunning() throws InterruptedException, ExecutionException {
        lifeCycleManagerStartUp();
        waitForServicesRunning();
        assertEquals(1, lifeCycleManager.getStatus().getServicesStartCount());
        assertTrue(lifeCycleManager.getStatus().getServicesStartCount() > 0);
    }

    @Test
    public void shutdown_afterStartup_noErrorAndStateIsShutdown() throws InterruptedException, ExecutionException {
        lifeCycleManagerStartUp();
        waitForServicesRunning();
        lifeCycleManager.shutdown();
        waitForServicesState(LifeCycleState.SHUTDOWN);
    }

    /**
     * During shutdown there can be an exception in a service that shall be stopped
     */
    @Test
    public void shutdown_successfulStartupButExceptionInStopService_shutdownOkAndExceptionDetected()
            throws InterruptedException, ExecutionException {
        lifeCycleManagerStartUp();
        waitForServicesRunning();
        assertEquals(0, lifeCycleManager.getStatus().getUncaughtExceptionCount());

        Optional<IErrorRequest> request = broker.resolve(IErrorRequest.class);
        request.get().process(IErrorRequest.Test.throwExceptionInStopService);
        lifeCycleManager.shutdown();
        waitForServicesState(LifeCycleState.SHUTDOWN);
        assertEquals(1, lifeCycleManager.getStatus().getUncaughtExceptionCount());
    }

    @Test
    public void startup_exceptionInStartedService_servicesAbortedAndStateIsFatal()
            throws InterruptedException, ExecutionException {
        moduleBindings.bindAsSingleton(ExceptionInStartService.class);
        lifeCycleManager.start();
        TestUtils.pollingWait(() -> lifeCycleManager.getStatus().getUncaughtExceptionCount() == 1,
                POLLINGWAIT_TIMEOUT_IN_MS);

        waitForServicesState(LifeCycleState.FATAL);
        assertEquals(LifeCycleState.FATAL, lifeCycleManager.getStatus().getState());
        assertEquals(1, lifeCycleManager.getStatus().getServicesStartCount());
    }

    /**
     * When startup has failed, shutdown shall still be possible.
     */
    @Test
    public void shutdown_exceptionInStartedService_servicesAbortedAndStateIsFatal()
            throws InterruptedException, ExecutionException {
        moduleBindings.bindAsSingleton(ExceptionInStartService.class);
        lifeCycleManager.start();
        TestUtils.pollingWait(() -> lifeCycleManager.getStatus().getUncaughtExceptionCount() == 1,
                POLLINGWAIT_TIMEOUT_IN_MS);

        waitForServicesState(LifeCycleState.FATAL);
        assertEquals(LifeCycleState.FATAL, lifeCycleManager.getStatus().getState());
        assertEquals(1, lifeCycleManager.getStatus().getServicesStartCount());

        lifeCycleManager.shutdown();
        waitForServicesState(LifeCycleState.SHUTDOWN);
    }

    @Test
    public void shutdown_afterStartup_stateIsXYZ() throws InterruptedException, ExecutionException {
        lifeCycleManagerStartUp();
        waitForServicesRunning();

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
    public void executeRequest_exception_exceptionReceivedAndServiceNotRestarted()
            throws InterruptedException, ExecutionException {
        lifeCycleManagerStartUp();
        Optional<IErrorRequest> request = broker.resolve(IErrorRequest.class);

        assertTrue(request.isPresent());
        try {
            request.get().process(IErrorRequest.Test.throwException);
            fail("Invoked method shall throw exception");
        } catch (RuntimeException e) {
            assertEquals(IErrorRequest.THROW_EXCEPTION_MSG, e.getMessage());
        }
        assertEquals(LifeCycleState.RUNNING, lifeCycleManager.getStatus().getState());
    }

    @Test
    public void executeRequest_uncaughtException_requestIsOkButServiceAreRestarted()
            throws InterruptedException, ExecutionException {
        lifeCycleManagerStartUp();
        Optional<IErrorRequest> request = broker.resolve(IErrorRequest.class);
        assertEquals(0, lifeCycleManager.getStatus().getUncaughtExceptionCount());

        assertEquals(IErrorRequest.RESPONSE_OK, request.get().process(IErrorRequest.Test.causeUncaughtException));
        waitForForwardedUncaughtExceptionCount(1);
        assertEquals(1, lifeCycleManager.getStatus().getForwardedUncaughtExceptionCount());
        waitForServiceStartCount(2);

        assertEquals(2, lifeCycleManager.getStatus().getServicesStartCount());
        assertEquals(LifeCycleState.RUNNING, lifeCycleManager.getStatus().getState());
    }

    @Test
    public void executeRequest_uncaughtException_afterRestartServicesAreAvailable()
            throws InterruptedException, ExecutionException {
        lifeCycleManagerStartUp();
        waitForServiceStartCount(1);
        Optional<IErrorRequest> request1 = broker.resolve(IErrorRequest.class);
        assertEquals(IErrorRequest.RESPONSE_OK, request1.get().process(IErrorRequest.Test.causeUncaughtException));
        waitForServiceStartCount(2);
        waitForForwardedUncaughtExceptionCount(1);

        Optional<ISessionRequest> request2 = broker.resolve(ISessionRequest.class);
        assertTrue(request2.isPresent());
        assertEquals(ISessionRequest.RESPONSE + MSG, request2.get().process(MSG));
    }

    @Test
    public void executeRequest_severealUncaughtExceptions_afterRestartServicesAreAvailable()
            throws InterruptedException, ExecutionException {
        lifeCycleManagerStartUp();
        waitForServiceStartCount(1);
        final int MAX_EXCEPTIONS = 50;
        for (int i = 0; i < MAX_EXCEPTIONS; i++) {
            final int expectedStartCount = lifeCycleManager.getStatus().getServicesStartCount() + 1;
            final int expectedForwardedUncaughtExceptionCount = lifeCycleManager.getStatus()
                    .getForwardedUncaughtExceptionCount() + 1;
            Optional<IErrorRequest> request1 = broker.resolve(IErrorRequest.class);
            assertEquals(IErrorRequest.RESPONSE_OK, request1.get().process(IErrorRequest.Test.causeUncaughtException));
            waitForServiceStartCount(expectedStartCount);
            waitForForwardedUncaughtExceptionCount(expectedForwardedUncaughtExceptionCount);

            Optional<ISessionRequest> request2 = broker.resolve(ISessionRequest.class);
            assertTrue(request2.isPresent());
            assertEquals(ISessionRequest.RESPONSE + MSG, request2.get().process(MSG));
        }
    }

    /**
     * Uncaught exception results in abort of current services. Threads will be
     * stopped. Scheduled work will not not be executed. See
     * EventBusService.java for implementation of abort(). Due to
     * timing/threading it is possible that both exceptions become uncaught
     * exception or both. There is no robust way to write a test case for
     * something that sometimes happens and sometimes not, while at the same
     * time know for sure that both are correct.
     * 
     * @throws InterruptedException
     * @throws ExecutionException
     */
    @Test
    public void executeRequest_twoParallelUncaughtException_afterRestartServicesAreAvailable()
            throws InterruptedException, ExecutionException {
        lifeCycleManagerStartUp();
        waitForServiceStartCount(1);
        Optional<IErrorRequest> request1 = broker.resolve(IErrorRequest.class);
        assertEquals(0, lifeCycleManager.getStatus().getForwardedUncaughtExceptionCount());

        assertEquals(IErrorRequest.RESPONSE_OK, request1.get().process(IErrorRequest.Test.causeTwoUncaughtExceptions));
        waitForServiceStartCount(2);
        waitForForwardedUncaughtExceptionCountAtLeast(1);
        assertEquals(0, lifeCycleManager.getStatus().getUncaughtExceptionCount());

        Optional<ISessionRequest> request2 = broker.resolve(ISessionRequest.class);
        assertTrue(request2.isPresent());
        assertEquals(ISessionRequest.RESPONSE + MSG, request2.get().process(MSG));
    }

    @Test
    public void executeRequest_severalTwoParallelUncaughtException_afterRestartServicesAreAvailable()
            throws InterruptedException, ExecutionException {
        lifeCycleManagerStartUp();
        waitForServiceStartCount(1);
        assertEquals(0, lifeCycleManager.getStatus().getForwardedUncaughtExceptionCount());

        final int MAX_EXCEPTIONS = 50;
        for (int i = 0; i < MAX_EXCEPTIONS; i++) {
            final int expectedStartCount = lifeCycleManager.getStatus().getServicesStartCount() + 1;
            final int expectedForwardedUncaughtExceptionCount = lifeCycleManager.getStatus()
                    .getForwardedUncaughtExceptionCount() + 1;
            Optional<IErrorRequest> request1 = broker.resolve(IErrorRequest.class);

            assertEquals(IErrorRequest.RESPONSE_OK,
                    request1.get().process(IErrorRequest.Test.causeTwoUncaughtExceptions));
            waitForServiceStartCount(expectedStartCount);
            waitForForwardedUncaughtExceptionCountAtLeast(expectedForwardedUncaughtExceptionCount);
            assertEquals(0, lifeCycleManager.getStatus().getUncaughtExceptionCount());

            Optional<ISessionRequest> request2 = broker.resolve(ISessionRequest.class);
            assertTrue(request2.isPresent());
            assertEquals(ISessionRequest.RESPONSE + MSG, request2.get().process(MSG));
        }
    }

    private void lifeCycleManagerStartUp() {
        lifeCycleManager.start();
        waitForServicesRunning();
    }

    private void waitForServicesRunning() {
        waitForServicesState(LifeCycleState.RUNNING);
    }

    private void waitForServicesState(final LifeCycleState state) {
        final ILifeCycleManagerStatus status = lifeCycleManager.getStatus();
        TestUtils.pollingWait(() -> status.isState(state), POLLINGWAIT_TIMEOUT_IN_MS);
    }

    private void waitForServiceStartCount(final int value) {
        final ILifeCycleManagerStatus status = lifeCycleManager.getStatus();
        TestUtils.pollingWait(() -> status.getServicesStartCount() == value, POLLINGWAIT_TIMEOUT_IN_MS);
    }

    private void waitForForwardedUncaughtExceptionCount(final int value) {
        final ILifeCycleManagerStatus status = lifeCycleManager.getStatus();
        TestUtils.pollingWait(() -> status.getForwardedUncaughtExceptionCount() == value, POLLINGWAIT_TIMEOUT_IN_MS);
    }

    private void waitForForwardedUncaughtExceptionCountAtLeast(final int value) {
        final ILifeCycleManagerStatus status = lifeCycleManager.getStatus();
        try {
            TestUtils.pollingWait(() -> status.getForwardedUncaughtExceptionCount() >= value,
                    POLLINGWAIT_TIMEOUT_IN_MS);
        } catch (TestUtils.PollingWaitException t) {
            throw new TestUtils.PollingWaitException("waitForForwardedUncaughtExceptionCountAtLeast, expected=" + value
                    + ", current=" + status.getForwardedUncaughtExceptionCount());
        }
    }
}
