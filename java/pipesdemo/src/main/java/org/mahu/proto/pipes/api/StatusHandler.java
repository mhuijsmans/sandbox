package org.mahu.proto.pipes.api;

public interface StatusHandler {
	public void Handle(Status status);
	public StatusHandler SetNext(StatusHandler next);
}
