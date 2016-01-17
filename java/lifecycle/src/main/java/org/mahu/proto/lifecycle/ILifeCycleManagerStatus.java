package org.mahu.proto.lifecycle;

import org.mahu.proto.lifecycle.ILifeCycleManager.LifeCycleState;

public interface ILifeCycleManagerStatus {
    
    LifeCycleState getState();
    
    int getServicesStartCount();

    int getExceptionCount();

}
