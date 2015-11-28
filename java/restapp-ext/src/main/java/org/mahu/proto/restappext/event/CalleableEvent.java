package org.mahu.proto.restappext.event;

import java.util.concurrent.Callable;

import org.mahu.proto.restapp.engine.SessionId;

public class CalleableEvent extends Event {
	private final Callable<Void> task;

	public CalleableEvent(SessionId id, Callable<Void> task) {
		super(id);
		this.task = task;
	}

	public Callable<Void> GetTask() {
		return task;
	}
}
