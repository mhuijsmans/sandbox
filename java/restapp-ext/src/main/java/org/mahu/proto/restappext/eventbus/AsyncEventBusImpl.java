package org.mahu.proto.restappext.eventbus;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.eventbus.SubscriberExceptionContext;
import com.google.common.eventbus.SubscriberExceptionHandler;

/**
 * AsyncEvent implementation with following properties. (1) number of parallel
 * executing events can be specified (2) it has an handler to catch exceptions
 * that are not caught by Subscribers.
 */
public class AsyncEventBusImpl implements AsyncEventBus {

	protected final static Logger LOGGER = LogManager.getLogger(AsyncEventBusImpl.class.getName());

	private final ExecutorService executor;
	private final SubscriberExceptionHandler subscriberExceptionHandler;
	private final DeadEventSubscriber deadEventSubscriber = new DeadEventSubscriber();
	private final com.google.common.eventbus.AsyncEventBus delegate;	

	// TODO; how to get exceptions caught by ExecutorService;

	/**
	 * EventBus with 1 thread. Thus allow events are executed in sequence.
	 * Typical usage is the main application, where events shall be executed in
	 * sequence. That properly is required for deterministic behavior of the
	 * application.
	 * 
	 * @param name
	 *            of eventbus. Name is used only for logging.
	 */
	public AsyncEventBusImpl(String name) {
		this(name, 1);
	}

	/**
	 * EventBus with 1 thread. Thus allow events are executed in sequence.
	 * Typical usage is a worker pool where non-correlated events can execute in
	 * parallel.
	 * 
	 * @param name
	 *            of eventsbus
	 * @param nrOfWorkerThreads
	 */
	public AsyncEventBusImpl(String name, int nrOfWorkerThreads) {
		LOGGER.info("name=" + name + " nrOfThreads=" + nrOfWorkerThreads);
		this.executor = Executors.newFixedThreadPool(nrOfWorkerThreads);
		this.subscriberExceptionHandler = new DefaultSubscriberExceptionHandler(); 		
		this.delegate = new com.google.common.eventbus.AsyncEventBus( executor, subscriberExceptionHandler);
		this.delegate.register(deadEventSubscriber);
	}

	public void Register(Object obj) {
		LOGGER.info("obj=" + obj.getClass().getName());
		delegate.register(obj);
	}

	public void Post(Object obj) {
		LOGGER.debug("obj=" + obj.getClass().getName());
		delegate.post(obj);
	}

	public void StopNow() {
		LOGGER.debug("ENTER");
		executor.shutdownNow();
		LOGGER.debug("LEAVE");
	}

	public DeadEventSubscriber GetDeadEventSubscriber() {
		return deadEventSubscriber;
	}

	class DefaultSubscriberExceptionHandler implements
			SubscriberExceptionHandler {

		@Override
		public void handleException(Throwable arg0,
				SubscriberExceptionContext arg1) {
			LOGGER.error("Exception in subscriber=" + arg1.getSubscriber()
					+ " method=" + arg1.getSubscriberMethod().getName()
					+ " event=" + arg1.getEvent().getClass().getName(), arg0);
		}

	}
}
