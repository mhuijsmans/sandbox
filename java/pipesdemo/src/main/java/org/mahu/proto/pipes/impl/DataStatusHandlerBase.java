package org.mahu.proto.pipes.impl;

import org.mahu.proto.pipes.api.Data;
import org.mahu.proto.pipes.api.DataHandler;
import org.mahu.proto.pipes.api.Status;
import org.mahu.proto.pipes.api.StatusHandler;

public class DataStatusHandlerBase implements DataHandler,
		StatusHandler {

	public StatusHandler SetNext(StatusHandler next) {
		nextStatusHandler = next;
		return this;
	}

	public DataHandler SetNext(DataHandler next) {
		nextDataHandler = next;
		return this;
	}
	
	public DataStatusHandlerBase SetDataAndStatusHandler(Object next) {
		SetNext((DataHandler)next);
		SetNext((StatusHandler)next);
		return this;
	}	

	protected DataHandler nextDataHandler;
	protected StatusHandler nextStatusHandler;
	public void Handle(Status status) {
		throw new UnsupportedOperationException();
	}

	public void Handle(Data data) {
		throw new UnsupportedOperationException();
	}

}
