package org.mahu.proto.pipes.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import org.mahu.proto.pipes.api.Data;
import org.mahu.proto.pipes.api.Status;

public class DataSequencer extends DataStatusHandlerBase {

	private final static Logger LOGGER = Logger.getLogger(DataSequencer.class
			.getName());

	public void Handle(Status status) {
		LOGGER.info("handle-status");
		while (!queuedData.isEmpty()) {
			nextDataHandler.Handle(queuedData.remove(0));
		}
	}

	public void Handle(Data data) {
		LOGGER.info("handle-data");
		queuedData.add(data);
	}

	private List<Data> queuedData = new LinkedList<Data>();
}
