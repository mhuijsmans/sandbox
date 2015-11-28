package org.mahu.proto.pipes.impl;

import java.util.logging.Logger;

import org.mahu.proto.pipes.api.Data;
import org.mahu.proto.pipes.api.DataHandler;

public class DataSink implements DataHandler {

	private final static Logger LOGGER = Logger.getLogger(DataSink.class
			.getName());

	public  DataHandler SetNext(DataHandler next) {
		throw new UnsupportedOperationException();
	}

	public void Handle(Data data) {
		LOGGER.info("Handle-data");
	}
}
