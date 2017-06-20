package org.mahu.proto.lifecycle.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mahu.proto.lifecycle.ILifeCycleManager.LifeCycleState;
import org.mahu.proto.lifecycle.IServiceLifeCycleControl;
import org.mockito.Mockito;

public class UncaughtExceptionTaskTest {

    private final static String THREAD_NAME = "UncaughtExceptionTaskTest.thread";

    private LifeCycleTaskContext context;
    private LifeCycleManagerStatus status;
    private LifeCycleManagerExecutorService executorService;
    private IServiceLifeCycleControl serviceLifeCycleControl;

    @Before
    public void createObjects() {
        context = Mockito.mock(LifeCycleTaskContext.class);

        status = new LifeCycleManagerStatus();
        when(context.getStatus()).thenReturn(status);

        executorService = Mockito.mock(LifeCycleManagerExecutorService.class);
        when(context.getExecutorService()).thenReturn(executorService);

        serviceLifeCycleControl = Mockito.mock(IServiceLifeCycleControl.class);
        when(context.getServiceLifeCycleControl()).thenReturn(Optional.of(serviceLifeCycleControl));
    }

    @After
    public void deleteObjects() {
        context = null;
        status = null;
        executorService = null;
        serviceLifeCycleControl = null;
    }

    @Test
    public void run_lifeCycleThreadLifeCycleTaskException_fatalTaskPosted() {
        String providedName = LifeCycleManagerExecutorService.THREAD_NAME;
        Throwable rootcause = Mockito.mock(Throwable.class);
        LifeCycleTask faultyTask = Mockito.mock(LifeCycleTask.class);
        Throwable throwable = new LifeCycleTaskException(rootcause, faultyTask.getClass());
        UncaughtExceptionTask task = new UncaughtExceptionTask(context, providedName, throwable);

        task.run();

        verify(executorService).asyncExecute(any(FatalTask.class));
        assertEquals(LifeCycleState.FATAL, status.getState());
    }

    @Test
    public void run_lifeCycleThreadOtherException_fatalTaskPosted() {
        String providedName = LifeCycleManagerExecutorService.THREAD_NAME;
        Throwable throwable = new Throwable();
        UncaughtExceptionTask task = new UncaughtExceptionTask(context, providedName, throwable);

        task.run();

        assertEquals(LifeCycleState.FATAL, status.getState());
        verify(serviceLifeCycleControl).abortServices();
        verify(executorService).shutdownNow();
    }

    @Test
    public void run_ServiceThread_StateRunning_Exception_restartTaskPosted() {
        status.setState(LifeCycleState.RUNNING);
        String providedName = THREAD_NAME;
        Throwable throwable = new Throwable();
        UncaughtExceptionTask task = new UncaughtExceptionTask(context, providedName, throwable);

        task.run();

        assertEquals(LifeCycleState.RUNNING, status.getState());
        verify(executorService).asyncExecute(any(ServicesRestartTask.class));
    }
    
    @Test
    public void run_ServiceThread_StateInit_Exception_nothingPosted() {
        status.setState(LifeCycleState.INIT);
        UncaughtExceptionTask task = new UncaughtExceptionTask(context, THREAD_NAME, new Throwable());

        task.run();

        assertEquals(LifeCycleState.INIT, status.getState());
        verify(executorService, never()).asyncExecute(any(Runnable.class));
    }
    
    @Test
    public void run_ServiceThread_StateFatal_Exception_nothingPosted() {
        status.setState(LifeCycleState.FATAL);
        UncaughtExceptionTask task = new UncaughtExceptionTask(context, THREAD_NAME, new Throwable());

        task.run();

        assertEquals(LifeCycleState.FATAL, status.getState());
        verify(executorService, never()).asyncExecute(any(Runnable.class));
    }
    
    @Test
    public void run_ServiceThread_StateShutdown_Exception_nothingPosted() {
        status.setState(LifeCycleState.SHUTDOWN);
        UncaughtExceptionTask task = new UncaughtExceptionTask(context, THREAD_NAME, new Throwable());

        task.run();

        assertEquals(LifeCycleState.SHUTDOWN, status.getState());
        verify(executorService, never()).asyncExecute(any(Runnable.class));
    }    

}
