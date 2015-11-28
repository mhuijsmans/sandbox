package org.mahu.proto.pipes.impl;

import java.util.logging.Logger;

import org.mahu.proto.pipes.api.Data;
import org.mahu.proto.pipes.api.DataStatusCounter;
import org.mahu.proto.pipes.api.Status;

public class ValidDataCounter extends DataStatusHandlerBase implements DataStatusCounter {

	interface ValidDataCounterListener {
		void maxReached();
	}

	private final static Logger LOGGER = Logger
			.getLogger(ValidDataCounter.class.getName());

	public ValidDataCounter(int max) {
		this.max = max;
	}

	public void Handle(Status status) {
		LOGGER.info("handle-status");
		nextStatusHandler.Handle(status);
		totalStatusCounter++;
		if (toggle) {
			validDataCounter++;
		}
		toggle = !toggle;
	}

	public void Handle(Data data) {
		LOGGER.info("handle-data");
		nextDataHandler.Handle(data);
		totalDataCounter++;
	}

	public int GetValidDataCounter() {
		return validDataCounter;
	}

	public int GetDelta() {
		return max - validDataCounter;
	}

	public boolean IsMaxReached() {
		return validDataCounter >= max;
	}
	
	public boolean IsStatusKnowForAllData() {
		return totalDataCounter == totalStatusCounter;
	}

	private int validDataCounter = 0;
	private int totalDataCounter = 0;
	private int totalStatusCounter = 0;
	private final int max;
	private boolean toggle = true;
}
