package org.mahu.proto.restappext.eventbus;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.Subscribe;

public class DeadEventSubscriber {
	
	protected final static Logger LOGGER = LogManager.getLogger(DeadEventSubscriber.class.getName());
	
	private final AtomicInteger deadEventCntr  = new AtomicInteger() ;
	
	@Subscribe
	public void HandleEvent(DeadEvent event) {
		Object originalEvent = event.getEvent();
		LOGGER.info("ENTER: event=" + originalEvent.getClass().getName());
		deadEventCntr.incrementAndGet();
	}
	
	public int GetDeadEventCntr() {
		return deadEventCntr.intValue();
	}

}
