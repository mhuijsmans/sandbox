package org.mahu.proto.lifecycle;

public interface ILifeCycleService {

    /**
     * Start of service. On return service has started.
     */    
    void start();
    
    /**
     * Gracefull terminate, on return service is stopped. Can take some time.
     * @return 
     */
    boolean stop();
  
    /**
     * Hard stop of service. On return service is stopped.
     */    
    void abort();
    
}
