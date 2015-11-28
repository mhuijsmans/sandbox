package org.mahu.proto.restappext.service;

import java.util.Map;

import org.mahu.proto.restapp.engine.SessionId;
import org.mahu.proto.restappext.event.EventConst;
import org.mahu.proto.restappext.event.SingleStepListener;

public interface RequestService {

	public class RequestTarget {
		private final String nameSystem;
		private final String nameProtocol;

		public RequestTarget(final String nameProtocol) {
			this.nameSystem = EventConst.LOCALSYSTEM;
			this.nameProtocol = nameProtocol;
		}

		public RequestTarget(final String nameSystem, final String nameProtocol) {
			this.nameSystem = nameSystem;
			this.nameProtocol = nameProtocol;
		}

		public String GetNameSystem() {
			return nameSystem;
		}

		public String GetNameProtocol() {
			return nameProtocol;
		}

		public String toString() {
			return "RequestTarget[system=" + nameSystem + ",protocol="
					+ nameProtocol + "]";
		}

	}

	public interface RequestResult {
		public enum Result {
			EXECUTING, TERMINATED, ABORTED, NORESOURCES
		};

		public Result GetResult();

		public String GetDetails();
	}

	/**
	 * Start a new Session
	 * 
	 * @param nameProtocol
	 * @return
	 */
	public SessionId StartSession(final RequestTarget requestTarget);

	public SessionId StartSession(final RequestTarget requestTarget,
			Map<String, Object> data);

	public SessionId StartSessionWithSingleStep(
			final RequestTarget requestTarget, final SingleStepListener listener);

	public RequestResult WaitForCompletion(final SessionId id)
			throws InterruptedException;

	public void Step(final SessionId id);

	public boolean Exists(final SessionId id);
}
