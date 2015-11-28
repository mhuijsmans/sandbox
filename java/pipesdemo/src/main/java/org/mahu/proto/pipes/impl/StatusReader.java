package org.mahu.proto.pipes.impl;

import java.util.logging.Logger;

import org.mahu.proto.pipes.api.Data;
import org.mahu.proto.pipes.api.Status;

public class StatusReader extends DataStatusHandlerBase {

	private final static Logger LOGGER = Logger.getLogger(StatusReader.class
			.getName());

	public void Handle(Data data) {
		LOGGER.info("handle-data");
		nextDataHandler.Handle(data);
		nextStatusHandler.Handle(new Status());
	}
}
