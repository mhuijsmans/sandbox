package org.mahu.proto.lifecycle;

public interface ILifeCycleManager {
    
    public static enum LifeCycleState {
        // Init is the state when the the LifeCycleManager is created.
        // In this start a Services can be started. On successful startup
        // of services, state will become running.
        // If services fail to start, state will become fatal 
        init, 
        // In this state anything can happens, restart, shutdown, fatal
        running, 
        // final state: shutdown request received.
        shutdown, 
        // final state
        fatal 
    }
    
    /**
     * Startup of the services in own thread.
     * This method can be called only once.
     * @return 
     */
    void start();
    
    /**
     * Shutdown services and return when that has completed. 
     * This method can be called only once.
     */
    void shutdown();
    
    ILifeCycleManagerStatus getStatus();

    int getActiveServiceCount();
}
