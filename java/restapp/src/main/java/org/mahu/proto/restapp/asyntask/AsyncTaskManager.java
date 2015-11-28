package org.mahu.proto.restapp.asyntask;

import java.util.concurrent.Callable;

import org.mahu.proto.restapp.engine.SessionId;

public interface AsyncTaskManager {

	// TODO: replace Callable by own interface
	public void Submit(final SessionId id, final Callable<Void> task);

}
