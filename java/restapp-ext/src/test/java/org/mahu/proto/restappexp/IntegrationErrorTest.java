package org.mahu.proto.restappexp;

import java.util.concurrent.RejectedExecutionException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mahu.proto.restappext.eventbus.AsyncEventBusImpl;

public class IntegrationErrorTest {

	protected final static Logger LOGGER = LogManager.getLogger(IntegrationErrorTest.class.getName());

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Test
	public void ctor_stopNow_noPostAllowed() throws InterruptedException {
		LOGGER.info("ENTER");
		AsyncEventBusImpl eventBus = new AsyncEventBusImpl("TEST");
		// test
		eventBus.StopNow();
		// verify
		exception.expect(RejectedExecutionException.class);
		eventBus.Post(new String());
		LOGGER.info("LEAVE");
	}

	@Test
	public void ctor_OneEventNextStopNow_noPostAllowed()
			throws InterruptedException {
		LOGGER.info("ENTER");
		AsyncEventBusImpl eventBus = new AsyncEventBusImpl("TEST");
		eventBus.Post(new String());
		// test
		eventBus.StopNow();
		// verify
		exception.expect(RejectedExecutionException.class);
		eventBus.Post(new String());
		LOGGER.info("LEAVE");
	}	

}
