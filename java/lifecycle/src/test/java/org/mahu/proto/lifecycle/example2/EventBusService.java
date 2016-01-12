package org.mahu.proto.lifecycle.example2;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.mahu.proto.lifecycle.ILifeCycleService;
import org.mahu.proto.lifecycle.example2.EventLog.Event;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.inject.Inject;

public class EventBusService implements IEventBus, ILifeCycleService {
    
    private final static int TASKSCOMPLETED_TIMEOUT_IN_SEC = 30;

    private static final String EVENTBUS_NAME_MAIN = "main";

    private final EventBusUncaughtExceptionHandler eventBusUncaughtExceptionHandler;
    private AsyncEventBus mainEventBus;
    private ExecutorService mainExecutor;

    @Inject
    public EventBusService(final EventBusUncaughtExceptionHandler eventBusUncaughtExceptionHandler) {
        this.eventBusUncaughtExceptionHandler = eventBusUncaughtExceptionHandler;
    }

    @Override
    public void start() {
        EventLog.log(Event.start, this);
        mainExecutor = Executors.newSingleThreadExecutor(namedEventBusThreadFactory(EVENTBUS_NAME_MAIN));
        mainEventBus = new AsyncEventBus(mainExecutor, eventBusUncaughtExceptionHandler);
    }

    @Override
    public boolean stop() {
        EventLog.log(Event.stop, this);
        return shutdown(mainExecutor);
    }

    @Override
    public void abort() {
        EventLog.log(Event.abort, this);
        shutdownNow(mainExecutor);
    }

    private boolean shutdown(final ExecutorService executor) {
        if (executor.isShutdown() == false) {
            executor.shutdown();
            try {
                assertTrue(executor.awaitTermination(TASKSCOMPLETED_TIMEOUT_IN_SEC, TimeUnit.SECONDS));
            } catch (InterruptedException e) {
                // When interrupted, there must be a reason, so stop wait
            }
        }
        installDummySubscriber();
        return executor.isTerminated();
    }

    private void shutdownNow(final ExecutorService executor) {
        if (executor.isShutdown() == false) {
            executor.shutdownNow();
        }
        installDummySubscriber();
    }

    private ThreadFactory namedEventBusThreadFactory(final String name) {
        // %%d resolves to %d after the String.format. This %d will be formatted
        // by ThreadFactoryBuilder.setNameFormat() (see javadoc)
        return new ThreadFactoryBuilder().setNameFormat(String.format("EventBus-%s-%%d", name)).build();
    }

    @Override
    public void post(Object event) {
        mainEventBus.post(event);
    }

    @Override
    public void register(Object object) {
        mainEventBus.register(object);
    }

    @Override
    public void unregister(Object object) {
        try {
            // Due to timing and threading it is possible that from 2 thread
            // unregister is called. One for stop() and one for abort(). This is
            // a problem for every registered EventBus service.
            // This general problem is solved here.
            mainEventBus.unregister(object);
        } catch (IllegalArgumentException e) {
            // Expected exception
        }
    }

    @Override
    public boolean isTerminated() {
        return mainExecutor.isTerminated();
    }

    @Override
    public boolean isShutdown() {
        return mainExecutor.isShutdown();
    }

    @Override
    public boolean awaitTermination(final long timeout, final TimeUnit unit) throws InterruptedException {
        return mainExecutor.awaitTermination(timeout, unit);
    }
    
    /**
     * 
     */
    private void installDummySubscriber() {
        mainEventBus.register(new DummySubscriber());
    }

}
