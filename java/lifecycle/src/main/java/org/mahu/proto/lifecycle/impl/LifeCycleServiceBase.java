package org.mahu.proto.lifecycle.impl;

import org.mahu.proto.lifecycle.IEventBus;
import org.mahu.proto.lifecycle.ILifeCycleService;

//@formatter:off
/**
 * This class provides a base class for a LifeCycle services.
 * It has the following properties:
 * - at start it registers itself with the eventBus and set's a flag that it is active.
 * - at stop it unregisters itself from the eventBus and set's a flag that it is not active.
 * 
 * It provides method to derived class to determine if service is active.
 * This method shall only be used when need. Example:
 * - a request to get data, shall be executed.
 * - a request that post and event and start of actions in time shall first check if the 
 *   service is still active.      
 */
//@formatter:on
public abstract class LifeCycleServiceBase implements ILifeCycleService {

    public static String SERVICE_UNAVAILABLE = "Requested service temporarily not available";
    private final IEventBus eventBus;
    // isActive is volatile because it accessed in ServiceThread and EventBusThread 
    private volatile boolean isActive;


    public LifeCycleServiceBase(final IEventBus eventBus) {
        this.eventBus = eventBus;
        isActive = false;
    }

    @Override
    public void start() {
        isActive = true;
        eventBus.register(this);
    }

    @Override
    public void stop() {
        serviceIsNoLongerActive();
    }

    @Override
    public void abort() {
        serviceIsNoLongerActive();
    }

    /**
     * If in time a XyzEvent is posted and at same time stop()/abort() is
     * called, then it is still possible that RequestProxyEvent is delivered.
     * The reason is that at time of posting the event is added to the EventBus
     * event queue.
     * 
     * This method can be used to verify that the service is active meaning
     * start has been called and stop has not been called. When not active it
     * will throw a RuntimeException.
     */
    protected void verifyServiceIsActive() {
        if (!isActive) {
            throw new RuntimeException(SERVICE_UNAVAILABLE);
        }
    }
    
    protected void eventBusPost(final Object event) {
        eventBus.post(event);
    }

    private void serviceIsNoLongerActive() {
        isActive = false;
        eventBus.unregister(this);
    }

}
