package org.mahu.proto.lifecycle;

public interface IEventBus {

    /**
     * Posts an event to all registered subscribers. This method will return
     * immediately.
     *
     * @param event
     *            event to post.
     */
    public void post(Object event);

    /**
     * Registers all subscriber methods on {@code object} to receive events. A
     * subscriber is a method with the @Subscribe annotation
     *
     * @param object
     *            object whose subscriber methods should be registered.
     */
    public void register(Object object);

    /**
     * Unregisters all subscriber methods on a registered {@code object}.
     *
     * @param object
     *            object whose subscriber methods should be unregistered.
     * @throws IllegalArgumentException
     *             if the object was not previously registered.
     */
    public void unregister(Object object);

}
