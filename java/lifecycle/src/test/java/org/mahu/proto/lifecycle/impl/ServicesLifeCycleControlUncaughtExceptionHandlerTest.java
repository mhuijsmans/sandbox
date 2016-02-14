package org.mahu.proto.lifecycle.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.mockito.Mockito;

public class ServicesLifeCycleControlUncaughtExceptionHandlerTest {
    
    private final static int id = 0;

    @Test
    public void constructor_parentNotCalled() {
        LifeCycleManagerUncaughtExceptionHandler parent = Mockito.mock(LifeCycleManagerUncaughtExceptionHandler.class);
        new ServicesLifeCycleControlUncaughtExceptionHandler(parent,id);
        
        verify(parent, never()).uncaughtException(any(Thread.class) , any(Throwable.class));
    }

    @Test
    public void uncaughtException_calledOnce_parentCalledOnceWithCorrectAtgument() {
        LifeCycleManagerUncaughtExceptionHandler parent = Mockito.mock(LifeCycleManagerUncaughtExceptionHandler.class);
        ServicesLifeCycleControlUncaughtExceptionHandler handler = 
            new ServicesLifeCycleControlUncaughtExceptionHandler(parent, id);
        final Thread thread = new Thread();
        final Throwable throwable = new Throwable();
        handler.uncaughtException(thread, throwable);
        
        verify(parent, times(1)).forwardedFirstUncaughtException(id, thread, throwable);
    }
    
    @Test
    public void uncaughtException_calledTwice_parentCalledOnceWithCorrectAtgument() {
        LifeCycleManagerUncaughtExceptionHandler parent = Mockito.mock(LifeCycleManagerUncaughtExceptionHandler.class);
        ServicesLifeCycleControlUncaughtExceptionHandler handler = 
            new ServicesLifeCycleControlUncaughtExceptionHandler(parent, id);
        final Thread thread1 = new Thread();
        final Throwable throwable1 = new Throwable();
        handler.uncaughtException(thread1, throwable1);
        final Thread thread2 = new Thread();
        final Throwable throwable2 = new Throwable();
        handler.uncaughtException(thread2, throwable2);        
        
        verify(parent, times(1)).forwardedFirstUncaughtException(id, thread1, throwable1);
        verify(parent, times(1)).forwardNextUncaughtException(id, thread2, throwable2);
    }    
    
}
