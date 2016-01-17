package org.mahu.proto.lifecycle;

public interface ILifeCycleManager {
    
    public static enum LifeCycleState {
        init, startingUp, restarting, running, shutdown, fatal 
    }
    
    /**
     * Startup of the services 
     * @return 
     */
    public void startUp();
    
    /**
     * 
     */
    public void shutdown();
    
    public ILifeCycleManagerStatus getStatus();

    public int getActiveServiceCount();
    
}
