package org.mahu.proto.lifecycle.impl;

import org.mahu.proto.lifecycle.ILifeCycleManager.LifeCycleState;
import org.mahu.proto.lifecycle.ILifeCycleManagerStatus;

final class LifeCycleManagerStatus implements ILifeCycleManagerStatus {
    
    /**
     * The variables listed below are volatile because they are accessed from different threads. 
     */
    private volatile LifeCycleState state = LifeCycleState.init; 
    private volatile int serviceStartCount = 0;
    private volatile int exceptionCount = 0;
    private volatile int forwardedExceptionCount = 0;

    @Override
    public LifeCycleState getState() {
        return state;
    }

    @Override
    public int getServicesStartCount() {
        return serviceStartCount;
    }
    
    @Override
    public int getUncaughtExceptionCount() {
        return exceptionCount;
    }
    
    @Override
    public int getForwardedUncaughtExceptionCount() {
        return forwardedExceptionCount;
    }  
    
    @Override    
    public boolean isState(final LifeCycleState newState) {
        return state == newState;
    }    

    void setState(final LifeCycleState newState) {
        state = newState;
    }

    void incrServiceStartCount() {
        serviceStartCount++;
    }

    public void incrExceptionCount() {
        exceptionCount++;
    }

    public void incrForwardedExceptionCount() {
        forwardedExceptionCount++;   
    }    
}
