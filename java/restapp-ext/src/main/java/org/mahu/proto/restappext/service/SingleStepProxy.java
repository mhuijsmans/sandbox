package org.mahu.proto.restappext.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mahu.proto.restapp.engine.SessionId;
import org.mahu.proto.restappext.event.SingleStepListener;
import org.mahu.proto.restappext.event.StartSessionCompletedEvent;
import org.mahu.proto.restappext.event.StartSessionWithListenerEvent;
import org.mahu.proto.restappext.event.WorkflowSingleStepPauzeEvent;
import org.mahu.proto.restappext.eventbus.AsyncEventBus;
import org.mahu.proto.restappext.service.SingleStepProxy.NotifyListenerEvent.EventType;

import com.google.common.eventbus.Subscribe;

public class SingleStepProxy {

	private static final Logger LOGGER = LogManager.getLogger(SingleStepProxy.class
			.getName());

	private final AsyncEventBus workerEventBus;

	/**
	 * Class that holds data about a single step session
	 */
	class SingleStepSession {
		private final SessionId id;
		private int stepCntr = 0;
		private final SingleStepListener listener;

		SingleStepSession(final SessionId id, final SingleStepListener listener) {
			this.id = id;
			this.listener = listener;
		}

		SessionId getSessionId() {
			return id;
		}

		int IncrStepCntr() {
			return stepCntr;
		}

		int GetStepCntr() {
			return stepCntr;
		}

		SingleStepListener GetSingleStepListener() {
			return listener;
		}
	}

	static class NotifyListenerEvent {
		private final SingleStepListener listener;
		private EventType eventType;

		enum EventType {
			READYTOSTEP, SESSIONCOMPLETED
		};

		NotifyListenerEvent(final SingleStepListener listener,
				final EventType eventType) {
			this.listener = listener;
			this.eventType = eventType;
		}
	}

	private final Map<SessionId, SingleStepSession> sessions = new HashMap<>();

	public SingleStepProxy(final AsyncEventBus workerEventBus) {
		this.workerEventBus = workerEventBus;
	}

	@Subscribe
	public void HandleEvent(StartSessionWithListenerEvent event) {
		LOGGER.info("ENTER: nameProtocol=" + event.GetProtocolName());
		if (event.IsSingleStep()) {
			final SessionId key = event.GetSessionId();
			sessions.put(key,
					new SingleStepSession(key, event.GetSingleStepListener()));
		}
		LOGGER.info("ENTER: nameProtocol=" + event.GetProtocolName());
	}

	@Subscribe
	public void HandleEvent(WorkflowSingleStepPauzeEvent event) {
		final SessionId key = event.GetSessionId();
		SingleStepSession session = sessions.get(key);
		if (session != null) {
			session.IncrStepCntr();
			workerEventBus.Post(new NotifyListenerEvent(session.listener,
					NotifyListenerEvent.EventType.READYTOSTEP));
		}
	}

	@Subscribe
	public void HandleEvent(StartSessionCompletedEvent event) {
		final SessionId key = event.GetSessionId();
		SingleStepSession session = sessions.remove(key);
		if (session != null) {
			workerEventBus.Post(new NotifyListenerEvent(session.listener,
					NotifyListenerEvent.EventType.SESSIONCOMPLETED));
		}
	}

	@Subscribe
	public void HandleEvent(NotifyListenerEvent event) {
		if (event.eventType == EventType.READYTOSTEP) {
			event.listener.ReadyToStep();
		} else {
			event.listener.SessionCompleted();
		}
	}
}
