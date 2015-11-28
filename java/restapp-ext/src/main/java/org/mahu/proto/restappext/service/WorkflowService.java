package org.mahu.proto.restappext.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mahu.proto.restapp.engine.SessionId;
import org.mahu.proto.restapp.engine.WorkflowEngine;
import org.mahu.proto.restapp.engine.WorkflowEngine.State;
import org.mahu.proto.restapp.engine.impl.WorkflowEngineImpl;
import org.mahu.proto.restappext.event.Event;
import org.mahu.proto.restappext.event.StartSessionCompletedEvent;
import org.mahu.proto.restappext.event.StartSessionCompletedEvent.Result;
import org.mahu.proto.restappext.event.StartWorkflowSessionEvent;
import org.mahu.proto.restappext.event.WorkflowFutureReadyEvent;
import org.mahu.proto.restappext.event.WorkflowSessionAbortEvent;
import org.mahu.proto.restappext.event.WorkflowSingleStepOneStepEvent;
import org.mahu.proto.restappext.event.WorkflowSingleStepPauzeEvent;
import org.mahu.proto.restappext.eventbus.AsyncEventBus;

import com.google.common.eventbus.Subscribe;

public class WorkflowService implements WorkflowEngine.Listener {

	protected final static Logger LOGGER = LogManager.getLogger(WorkflowService.class.getName());

	private final AsyncEventBus eventBus;

	private Map<SessionId, WorkflowServiceConfig> executingWorkflows = new HashMap<>();
	private Map<String, Object> services = new HashMap<>();

	static class WorkflowServiceConfig {
		final SessionId id;
		final WorkflowEngine engine;
		final boolean isSingleStep;

		WorkflowServiceConfig(final SessionId id, final WorkflowEngine engine,
				final boolean isSingleStep) {
			this.id = id;
			this.engine = engine;
			this.isSingleStep = isSingleStep;
		}
	}

	public WorkflowService(final AsyncEventBus eventBus) {
		this.eventBus = eventBus;
	}

	/**
	 * Add a Object that can be used in Dependency injection of the tasks &
	 * taskServices. This data is used for all requests that are received.
	 * 
	 * @param cls
	 *            , the name of the interface/class
	 * @param obj
	 *            , he object
	 */
	public void AddAnnotatableService(final Class<?> cls, Object obj) {
		services.put(cls.getName(), obj);
	}

	@Subscribe
	public void HandleEvent(final StartWorkflowSessionEvent event) {
		final SessionId id = event.GetSessionId();
		LOGGER.debug("ENTER event=StartWorkflowSessionEvent id=" + id);
		final boolean isSingleStep = event.IsSingleStep();
		//
		Map<String, Object> data = new HashMap<String, Object>();
		data.put(SessionId.class.getName(), id);
		data.putAll(services);
		// Add request specific data.
		if (event.GetData() != null) {
			data.putAll(event.GetData());
		}
		//
		WorkflowEngine engine = new WorkflowEngineImpl();
		engine.Init(id, event.GetProcessDefinition(), data, this);
		WorkflowServiceConfig config = new WorkflowServiceConfig(id, engine,
				isSingleStep);
		executingWorkflows.put(id, config);
		//
		if (config.isSingleStep) {
			eventBus.Post(new WorkflowSingleStepPauzeEvent(config.id));
		} else {
			ExecuteJobs(event, config);
		}
		LOGGER.debug("LEAVE event=" + event.getClass().getName());
	}

	@Subscribe
	public void HandleEvent(final WorkflowSingleStepOneStepEvent event) {
		SessionId id = event.GetSessionId();
		LOGGER.debug("HandleEvent(WorkflowSingleStepOneStepEvent) ENTER id="
				+ id);
		WorkflowServiceConfig config = executingWorkflows.get(id);
		if (config != null) {
			ExecuteJob(event, config);
		} else {
			// workflow has terminated while processing the ProcessTaskFuture.
			LOGGER.debug("HandleEvent(WorkflowSingleStepOneStepEvent) id=" + id
					+ " is gone, dropping the event");
		}
		LOGGER.debug("HandleEvent(WorkflowSingleStepOneStepEvent) LEAVE id="
				+ id);
	}

	/**
	 * A future has completed and resulted in new Job added (to end of JobList)
	 * in the workflow engine.
	 */
	@Subscribe
	public void HandleEvent(final WorkflowFutureReadyEvent event) {
		SessionId id = event.GetSessionId();
		LOGGER.debug("HandleEvent(WorkflowFutureReadyEvent) ENTER id=" + id);
		WorkflowServiceConfig config = executingWorkflows.get(id);
		if (config != null) {
			if (config.isSingleStep) {
				// If state is pauzed, there process is waiting for an external
				// event to continue.
				// That mean for singleStep to send a Event allowing external
				// user to know that system can continue.
				if (config.engine.GetState() == State.EXECUTING_PAUZED) {
					LOGGER.info("HandleEvent(WorkflowFutureReadyEvent) singleStep & state==EXECUTING_PAUZED sending WorkflowSingleStepPauzeEvent id="
							+ id);
					eventBus.Post(new WorkflowSingleStepPauzeEvent(config.id));
				} else {
					LOGGER.info("HandleEvent(WorkflowFutureReadyEvent) singleStep & state!=EXECUTING_PAUZED do nothing id="
							+ id);
				}
			} else {
				ExecuteJobs(event, config);
			}
		} else {
			// workflow has terminated while processing the ProcessTaskFuture.
			LOGGER.debug("HandleEvent(WorkflowFutureReadyEvent) id=" + id
					+ " is gone, dropping the event");
		}
		LOGGER.debug("HandleEvent(WorkflowFutureReadyEvent) LEAVE id=" + id);
	}

	@Subscribe
	public void HandleEvent(final WorkflowSessionAbortEvent event) {
		SessionId id = event.GetSessionId();
		WorkflowServiceConfig config = executingWorkflows.get(id);
		if (config != null) {
			LOGGER.debug("HandleEvent(WorkflowSessionAbortEvent) LEAVE id="
					+ id + " aborting session");
			RemoveSessionAndSendSessionCompletedEvent(event, config,
					Result.ERROR, "WorkflowSessionAbortEvent received");
		} else {
			LOGGER.debug("HandleEvent(WorkflowSessionAbortEvent) LEAVE id="
					+ id + " session is gone, ignoring message");
		}
	}

	/**
	 * Notification from a ProcessTaskFuture that it has completed. A
	 * ProcessTaskFuture typically doesn't execute on the mainEventBus. It
	 * typically executes on another thread, eventBus, i.e. an event needs to be
	 * posted to continue execution of the process on the eventBus configured
	 * for this service.
	 */
	// WorkflowEngine2.Observer
	@Override
	public void ProcessTaskFutureHasCompleted(final SessionId id) {
		LOGGER.debug("ProcessTaskFutureHasCompleted() event=StartWorkflowSessionEvent id="
				+ id);
		// TODO: For single step this is slightly more tricky, because the end
		// user is controlling the execution and we do not want external event
		// to trigger execution.
		eventBus.Post(new WorkflowFutureReadyEvent(id));
	}

	private void ExecuteJobs(final Event event,
			final WorkflowServiceConfig config) {
		LOGGER.debug("ExecuteJobs(Event, WorkflowServiceConfig) ENTER id="
				+ config.id);
		//
		final WorkflowEngine.State state = config.engine.ExecuteJobs();
		switch (state) {
		case ABORTED:
			RemoveSessionAndSendSessionCompletedEvent(event, config,
					Result.ERROR,
					"ExecuteJobs detected state=ABORTED, cause unknown");
			break;
		case EXECUTING_PAUZED:
			// TODO: start a timer to guard lifetime of of service
			// Make use of counter in WorkflowServiceConfig to have a unique
			// TimeId(SessionId,cntr);
			// Cntr is stepped each time that executeJob is invoked.
			break;
		case TERMINATED:
			RemoveSessionAndSendSessionCompletedEvent(event, config, Result.OK);
			break;
		case EXECUTING_JOBS_EXIST:
		default:
			LOGGER.error("WorkFlowengine implementation returned invalid state="
					+ state);
			RemoveSessionAndSendSessionCompletedEvent(event, config,
					Result.ERROR, "ExecuteJobs detected invalid state=" + state);
			break;
		}

		LOGGER.debug("ExecuteJobs(Event, WorkflowServiceConfig) LEAVE id="
				+ config.id + " state=" + state);
	}

	private void ExecuteJob(final Event event,
			final WorkflowServiceConfig config) {
		LOGGER.debug("ExecuteJob(WorkflowServiceConfig) ENTER id=" + config.id);
		final WorkflowEngine.State state = config.engine.ExecuteOneJob();
		switch (state) {
		case ABORTED:
			RemoveSessionAndSendSessionCompletedEvent(event, config,
					Result.ERROR,
					"ExecuteJob detected state=ABORTED, cause unknown");
			break;
		case EXECUTING_PAUZED:
			// Session life cycle is managed elsewhere.
			break;
		case TERMINATED:
			RemoveSessionAndSendSessionCompletedEvent(event, config, Result.OK);
			break;
		case EXECUTING_JOBS_EXIST:
			eventBus.Post(new WorkflowSingleStepPauzeEvent(config.id));
			break;
		default:
			LOGGER.error("WorkFlowengine implementation returned invalid state="
					+ state);
			RemoveSessionAndSendSessionCompletedEvent(event, config,
					Result.ERROR, "ExecuteJob detected invalid state=" + state);
		}
		LOGGER.debug("ExecuteJob(WorkflowServiceConfig) LEAVE event=StartWorkflowSessionEvent id="
				+ config.id);
	}

	private void RemoveSessionAndSendSessionCompletedEvent(final Event event,
			final WorkflowServiceConfig config, final Result result) {
		RemoveSessionAndSendSessionCompletedEvent(event, config, result, null);
	}

	private void RemoveSessionAndSendSessionCompletedEvent(final Event event,
			final WorkflowServiceConfig config, final Result result,
			final String details) {
		LOGGER.debug("id=" + config.id + " result=" + result);
		executingWorkflows.remove(config.id);
		eventBus.Post(new StartSessionCompletedEvent(event, result, details));
	}

}
