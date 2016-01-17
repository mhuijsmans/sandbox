package org.mahu.proto.lifecycle;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.mahu.proto.lifecycle.example2.DummySubscriber;
import org.mahu.proto.lifecycle.example2.EventBusUncaughtExceptionProxy;
import org.mahu.proto.lifecycle.impl.ExecutorUtils;

import com.google.common.eventbus.AsyncEventBus;
import com.google.inject.Inject;

public class EventBusService implements IEventBus, ILifeCycleService {

    private final static int TASKSCOMPLETED_TIMEOUT_IN_SEC = 30;

    private static final String EVENTBUS_NAME = "eventBusThread";

    private final EventBusUncaughtExceptionProxy eventBusUncaughtExceptionProxy;
    private AsyncEventBus eventBus;
    private ExecutorService eventBusExecutor;
    private final IThreadFactoryFactory threadFactoryFactory;

    @Inject
    public EventBusService(final UncaughtExceptionHandler uncaughtExceptionHandler, final IThreadFactoryFactory threadFactoryFactory) {
        this.threadFactoryFactory = threadFactoryFactory;
        eventBusUncaughtExceptionProxy = new EventBusUncaughtExceptionProxy(uncaughtExceptionHandler);
    }

    @Override
    public void start() {
        eventBusExecutor = Executors.newSingleThreadExecutor(threadFactoryFactory.createNamedEventBusThreadFactory(EVENTBUS_NAME));
        eventBus = new AsyncEventBus(eventBusExecutor, eventBusUncaughtExceptionProxy);
    }

    @Override
    public void stop() {
        shutdown(eventBusExecutor);
    }

    @Override
    public void abort() {
        shutdownNow(eventBusExecutor);
    }

    private boolean shutdown(final ExecutorService executor) {
        if (executor.isShutdown() == false) {
            installDummySubscriber();
            ExecutorUtils.shutdown(eventBusExecutor);
        }
        return executor.isTerminated();
    }

    private void shutdownNow(final ExecutorService executor) {
        if (executor.isShutdown() == false) {
            executor.shutdownNow();
        }
        installDummySubscriber();
    }

    @Override
    public void post(Object event) {
        eventBus.post(event);
    }

    @Override
    public void register(Object object) {
        eventBus.register(object);
    }

    @Override
    public void unregister(Object object) {
        try {
            // Due to timing and threading it is possible that from 2 thread
            // unregister is called. One for stop() and one for abort(). This is
            // a problem for every registered EventBus service.
            // This general problem is solved here.
            eventBus.unregister(object);
        } catch (IllegalArgumentException e) {
            // Expected exception
        }
    }

    @Override
    public boolean isTerminated() {
        return eventBusExecutor.isTerminated();
    }

    @Override
    public boolean isShutdown() {
        return eventBusExecutor.isShutdown();
    }

    @Override
    public boolean awaitTermination(final long timeout, final TimeUnit unit) throws InterruptedException {
        return eventBusExecutor.awaitTermination(timeout, unit);
    }

    private void installDummySubscriber() {
        eventBus.register(new DummySubscriber());
    }

}
