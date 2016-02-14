package org.mahu.proto.lifecycle;

import org.mahu.proto.lifecycle.ILifeCycleManager.LifeCycleState;

public interface ILifeCycleManagerStatus {
    
    /**
     * @return current state of LifeCycleManager
     */
    LifeCycleState getState();
    
    /**
     * @return is current state attached provided state
     */
    boolean isState(final LifeCycleState state);
    
    /**
     * @return number of times that service has been started. 
     * Both successful and not successful are counted.
     * Counter is stepped as last action if a procedure. 
     */
    int getServicesStartCount();

    /**
     * @return number of uncaught exceptions
     */
    int getUncaughtExceptionCount();
    
    /**
     * @return number of uncaught exceptions from services that have been forwarded. 
     * When an uncaught exception is detected, services are restarted.
     * Because service can use multiple threads, multiple exception can happen at the 
     * same time. ServersStartCunt indicated number of start/restarts.    
     */
    int getForwardedUncaughtExceptionCount();
}
