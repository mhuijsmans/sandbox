package org.mahu.proto.restappexp;

import java.util.concurrent.CountDownLatch;

import org.junit.Test;
import org.mahu.proto.restapp.engine.SessionIdManager;
import org.mahu.proto.restappext.event.LoadProtocolCompletedEvent;
import org.mahu.proto.restappext.event.LoadProtocolEvent;
import org.mahu.proto.restappext.event.StartSessionEvent;
import org.mahu.proto.restappext.eventbus.AsyncEventBusImpl;
import org.mahu.proto.restappext.service.ProtocolLoaderService;

import com.google.common.eventbus.Subscribe;

public class ProtocolLoaderServiceTest {

	@Test(timeout = 3000)
	public void loadProtocolEvent_eventSentTwice_protocolLoadedOnceNextFromCache()
			throws InterruptedException {
		// Preparation
		AsyncEventBusImpl eventBus = new AsyncEventBusImpl("main", 1);
		AsyncEventBusImpl workerEventBus = new AsyncEventBusImpl("worker", 4);
		ProtocolLoaderService service = new ProtocolLoaderService(eventBus,
				workerEventBus);
		TestClient client = new TestClient();
		eventBus.Register(service);
		workerEventBus.Register(service);
		eventBus.Register(client);
		// test
		StartSessionEvent event = new StartSessionEvent(
				SessionIdManager.Create(), TestConst.TEST_SCRIPT, false);
		LoadProtocolEvent lpEvent = new LoadProtocolEvent(event);
		eventBus.Post(lpEvent);
		eventBus.Post(lpEvent);
		client.cdl.await();
		// verify
		// TDODO: verify success
	}
	
	// TODO: test case for failed to load
	// TODO: test case multiple load request at same time.
	// TODO: Load invalid xml, e.g. no class defined for task.

	static class TestClient {
		CountDownLatch cdl = new CountDownLatch(1);

		@Subscribe
		public void HandleEvent(LoadProtocolCompletedEvent event) {
			cdl.countDown();
		}
	}

}
