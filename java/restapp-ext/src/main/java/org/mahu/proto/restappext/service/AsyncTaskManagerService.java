package org.mahu.proto.restappext.service;

import java.util.concurrent.Callable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mahu.proto.restapp.asyntask.AsyncTaskManager;
import org.mahu.proto.restapp.engine.SessionId;
import org.mahu.proto.restappext.event.CalleableEvent;
import org.mahu.proto.restappext.event.WorkflowSessionAbortEvent;
import org.mahu.proto.restappext.eventbus.AsyncEventBus;

import com.google.common.eventbus.Subscribe;

public final class AsyncTaskManagerService implements AsyncTaskManager {
	protected final static Logger LOGGER = LogManager.getLogger(AsyncTaskManagerService.class.getName());

	private final AsyncEventBus mainEventBus;
	private final AsyncEventBus workerEventBus;

	public AsyncTaskManagerService(final AsyncEventBus mainEventBus,
			final AsyncEventBus workerEventBus) {
		this.workerEventBus = workerEventBus;
		this.mainEventBus = mainEventBus;
	}

	@Override
	public void Submit(final SessionId id, Callable<Void> task) {
		LOGGER.debug("Submit() id={}", id);
		workerEventBus.Post(new CalleableEvent(id, task));
	}

	@Subscribe
	public void HandleEvent(final CalleableEvent event)
			throws InterruptedException {
		final SessionId id = event.GetSessionId();
		LOGGER.debug("CalleableEvent() ENTER id={}", id);
		// TODO: check guidelines on catching exception
		try {
			event.GetTask().call();
		} catch (RuntimeException e) {
			LOGGER.error("CalleableEvent() id="+id+" exception caught:", e);
			mainEventBus.Post(new WorkflowSessionAbortEvent(event));
		} catch (InterruptedException e) {
			// rethrow
			throw e;
		} catch (Exception e) {
			LOGGER.error("CalleableEvent() id="+id+" exception caught:", e);
			mainEventBus.Post(new WorkflowSessionAbortEvent(event));
		}
		LOGGER.debug("CalleableEvent() LEAVE id={}", event.GetSessionId());
	}

}