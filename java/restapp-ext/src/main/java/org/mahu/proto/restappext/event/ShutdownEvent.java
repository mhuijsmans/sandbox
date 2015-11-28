package org.mahu.proto.restappext.event;

/**
 * The ShutdownEvent event can be used to signal that the EventBus will stop processing.
 * It is upto the user of the eventBus integrator  to post this event. 
 * Startup event is typically used is combination with the ShutdownEvent.
 * The intended use of ShutdownEvent to stop services.
 */
public class ShutdownEvent {

}
