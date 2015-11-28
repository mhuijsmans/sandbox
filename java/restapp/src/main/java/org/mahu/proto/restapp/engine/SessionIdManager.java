package org.mahu.proto.restapp.engine;

import java.util.concurrent.atomic.AtomicLong;

public class SessionIdManager {

	private final static AtomicLong cntr = new AtomicLong();

	public static SessionId Create() {
		return new SessionIdImpl(cntr.incrementAndGet());
	}

	public static void SetIsTerminated(SessionId id, final Object resultData) {
		((SessionIdImpl) id).SetIsTerminated(resultData);
	}

}
