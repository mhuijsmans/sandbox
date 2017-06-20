package org.mahu.proto.lifecycle;

public interface ILifeCycleManager {

    public static enum LifeCycleState {
        // Init is the state when the the LifeCycleManager is created.
        // In this start a Service can be started. On successful startup
        // of services, state will become running.
        // If services fail to start, state will become fatal
        INIT,
        // In this state anything can happens, restart, shutdown, fatal
        RUNNING,
        // final state: shutdown request received.
        SHUTDOWN,
        // final state
        FATAL
    }

    /**
     * Startup of the services in own thread. This method can be called only
     * once.
     */
    void start();

    /**
     * Shutdown services and return when that has completed. This method can be
     * called only once.
     */
    void shutdown();

    /**
     * @return current status
     */
    ILifeCycleManagerStatus getStatus();

    /**
     * @return the number of active service
     */
    // TODO: why is this not pat of ILifeCycleManagerStatus ?
    int getActiveServiceCount();
}
