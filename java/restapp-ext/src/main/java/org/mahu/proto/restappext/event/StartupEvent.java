package org.mahu.proto.restappext.event;

/**
 * The startup event is typically posted as the first event on an EventBus.
 * It is upto the user of the integrator eventBus to post this event. 
 * Startup event is typically used is combination with the ShutdownEvent.
 * The intended use of StartupEvent to start services.
 */
public class StartupEvent {

}
