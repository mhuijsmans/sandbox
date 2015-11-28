package org.mahu.proto.pipes.impl;

import java.util.logging.Logger;

import org.mahu.proto.pipes.api.Data;
import org.mahu.proto.pipes.api.DataStatusCounter;
import org.mahu.proto.pipes.api.Status;
import org.mahu.proto.pipes.api.StatusHandler;

public class Serializer extends DataStatusHandlerBase {
	private final static Logger LOGGER = Logger.getLogger(Serializer.class
			.getName());

	public StatusHandler SetNext(StatusHandler next) {
		super.SetNext(next);
		if (!(next instanceof DataStatusCounter)) {
			throw new IllegalArgumentException();
		}
		return this;
	}

	public synchronized void Handle(Status status) {
		LOGGER.info("handle-status");
		nextStatusHandler.Handle(status);
		notify();
	}

	public synchronized void Handle(Data data) {
		LOGGER.info("handle-data");
		nextDataHandler.Handle(data);
	}

	public synchronized void WaitUntilStatusKnowForAllReadData() {
		LOGGER.info("WaitUntilStatusKnowForAllReadData");
		DataStatusCounter dataStatusCounter = (DataStatusCounter) nextStatusHandler;
		while (!dataStatusCounter.IsStatusKnowForAllData()) {
			try {
				while (!dataStatusCounter.IsStatusKnowForAllData()) {
					wait();
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
