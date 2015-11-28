package org.mahu.proto.restappext.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mahu.proto.restapp.engine.SessionId;
import org.mahu.proto.restapp.engine.SessionIdManager;
import org.mahu.proto.restappext.event.SingleStepListener;
import org.mahu.proto.restappext.event.StartSessionCompletedEvent;
import org.mahu.proto.restappext.event.StartSessionEvent;
import org.mahu.proto.restappext.event.StartSessionWithListenerEvent;
import org.mahu.proto.restappext.event.WorkflowSingleStepOneStepEvent;
import org.mahu.proto.restappext.eventbus.AsyncEventBus;

import com.google.common.eventbus.Subscribe;

public class RequestServiceImpl implements RequestService {

	protected final static Logger LOGGER = LogManager.getLogger(RequestServiceImpl.class.getName());

	static class ExecutingRequests {
		private final Map<SessionId, SessionId> executingRequests = new HashMap<>();

		synchronized void put(final SessionId id) {
			executingRequests.put(id, id);
		}

		synchronized void remove(final SessionId id) {
			executingRequests.remove(id);
		}

		synchronized boolean exists(final SessionId id) {
			return executingRequests.containsKey(id);
		}
	}

	private final AsyncEventBus eventBus;
	private final ExecutingRequests executingRequests = new ExecutingRequests();

	public RequestServiceImpl(AsyncEventBus event) {
		this.eventBus = event;
	}

	@Override
	public SessionId StartSession(final RequestTarget requestTarget) {
		return StartSession(requestTarget, null);
	}

	@Override
	public SessionId StartSession(final RequestTarget requestTarget,
			Map<String, Object> data) {
		LOGGER.debug("ENTER: requestTarget=" + requestTarget);
		SessionId id = SessionIdManager.Create();
		eventBus.Post(new StartSessionEvent(id, requestTarget.GetNameSystem(),
				requestTarget.GetNameProtocol(), false, data));
		executingRequests.put(id);
		LOGGER.debug("LEAVE: requestid=" + id);
		return id;
	}

	@Override
	public SessionId StartSessionWithSingleStep(
			final RequestTarget requestTarget, final SingleStepListener listener) {
		LOGGER.debug("ENTER: nameProtocol=" + requestTarget);
		SessionId id = SessionIdManager.Create();
		eventBus.Post(new StartSessionWithListenerEvent(id, requestTarget
				.GetNameSystem(), requestTarget.GetNameProtocol(), listener));
		executingRequests.put(id);
		LOGGER.debug("LEAVE: requestid=" + id);
		return id;
	}

	@Subscribe
	public void HandleEvent(final StartSessionCompletedEvent event) {
		LOGGER.debug("event=" + event);
		SessionIdManager.SetIsTerminated(event.GetSessionId(), event);
		executingRequests.remove(event.GetSessionId());
	}

	@Override
	public RequestService.RequestResult WaitForCompletion(final SessionId id)
			throws InterruptedException {
		LOGGER.debug("ENTER: requestid=" + id);
		try {
			final StartSessionCompletedEvent event = (StartSessionCompletedEvent) id
					.WaitForCompletion();
			LOGGER.debug("requestid=" + id + " event=" + event);
			RequestService.RequestResult result = new RequestResultImpl(event);
			LOGGER.debug("LEAVE: requestid=" + id + " result=" + result);
			return result;
		} catch (InterruptedException e) {
			// Just interested in logging, thus rethrow the exception
			LOGGER.debug("interrupted: requestid=" + id);
			throw e;
		}
	}

	@Override
	public void Step(final SessionId id) {
		LOGGER.debug("ENTER: requestid=" + id);
		eventBus.Post(new WorkflowSingleStepOneStepEvent(id));
		LOGGER.debug("LEAVE: requestid=" + id);
	}

	@Override
	public boolean Exists(SessionId id) {
		return executingRequests.exists(id);
	}

	static class RequestResultImpl implements RequestService.RequestResult {
		private final Result result;
		private final String details;

		public RequestResultImpl(final StartSessionCompletedEvent event) {
			switch (event.GetResult()) {
			case OK:
				this.result = Result.TERMINATED;
				break;
			case NORESOURCES:
				this.result = Result.NORESOURCES;
				break;
			case ERROR:
			default:
				this.result = Result.ABORTED;
			}
			this.details = event.GetDetails();
		}

		@Override
		public Result GetResult() {
			return result;
		}

		@Override
		public String GetDetails() {
			return details;
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof RequestService.RequestResult) {
				RequestService.RequestResult rr = (RequestService.RequestResult) o;
				return result == rr.GetResult();
			}
			return false;
		}

		@Override
		public String toString() {
			switch (result) {
			case EXECUTING:
				return "RequestResult=EXECUTING";
			case NORESOURCES:
				return "RequestResult=NORESOURCES";
			case TERMINATED:
				return "RequestResult=TERMINATED";
			case ABORTED:
				return "RequestResult=ABORTED";
			default:
				return "RequestResult=" + result;
			}
		}

	}
}
