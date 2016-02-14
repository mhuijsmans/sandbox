package org.mahu.proto.lifecycle;

public interface IServiceLifeCycleControl {

    void startServices();
    
    void stopServices();    
    
    void abortServices();
    
    int getActiveServiceCount();    

    int getStartedServicesCount();
    
    Class<? extends ILifeCycleService> getStartedServiceClass(final int i);
    
    int getStoppedServicesCount();
    
    Class<? extends ILifeCycleService> getStoppedServiceClass(final int i);
}
