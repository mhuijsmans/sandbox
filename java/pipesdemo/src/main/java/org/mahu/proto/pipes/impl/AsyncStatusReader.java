package org.mahu.proto.pipes.impl;

import java.util.logging.Logger;

import org.mahu.proto.pipes.api.Data;
import org.mahu.proto.pipes.api.Status;

public class AsyncStatusReader extends DataStatusHandlerBase {

	private final static Logger LOGGER = Logger.getLogger(AsyncStatusReader.class
			.getName());

	public void Handle(Data data) {
		LOGGER.info("handle-data");
        (new StatusThread()).start();		
		nextDataHandler.Handle(data);
	}
	
	public class StatusThread extends Thread {

	    public void run() {
	        try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
			}
	        nextStatusHandler.Handle(new Status());
	    }
	}
	
	
}
