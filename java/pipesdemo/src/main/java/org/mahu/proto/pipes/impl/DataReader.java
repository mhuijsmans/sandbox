package org.mahu.proto.pipes.impl;

import java.util.logging.Logger;

import org.mahu.proto.pipes.api.Data;
import org.mahu.proto.pipes.api.DataHandler;

public class DataReader {
	private final static Logger LOGGER = Logger.getLogger(DataReader.class
			.getName());

	public DataReader() {
	}

	public void SetNext(DataHandler next) {
		this.next = next;
	}

	public void start(int nrOfItemToRead) {
		while (nrOfItemToRead > 0) {
			LOGGER.info("Read and forwarding data, cntr=" + cntr++);
			next.Handle(new Data());
			nrOfItemToRead--;
		}
	}

	private DataHandler next;
	private int cntr = 0;
}
