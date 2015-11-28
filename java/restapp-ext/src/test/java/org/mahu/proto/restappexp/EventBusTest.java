package org.mahu.proto.restappexp;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.CountDownLatch;

import org.junit.Test;
import org.mahu.proto.restappext.eventbus.AsyncEventBusImpl;

import com.google.common.eventbus.Subscribe;

public class EventBusTest {

	/**
	 * A promise of the eventbus is that a hander will receive one events at
	 * same time. UNLESS, it has set an Annotation to handle concurrency.
	 * 
	 * @throws InterruptedException
	 */
	@Test(timeout = 2000)
	public void post_twoEvents_eventsExecutedSequentially()
			throws InterruptedException {
		// preperation
		AsyncEventBusImpl eventBus = new AsyncEventBusImpl("test", 4);
		TestSubscriber sub = new TestSubscriber();
		eventBus.Register(sub);
		// test
		CountDownLatch cdl1 = new CountDownLatch(1);
		CountDownLatch cdl2 = new CountDownLatch(1);
		eventBus.Post(cdl1);
		eventBus.Post(cdl2);
		Thread.sleep(1000);
		cdl1.countDown();
		cdl2.countDown();
		sub.cdl.await();
		// verify
		assertEquals("11", sub.sb.toString());
	}

	class TestSubscriber {
		CountDownLatch cdl = new CountDownLatch(2);
		private int cntr = 0;
		private StringBuffer sb = new StringBuffer();

		@Subscribe
		public void HandleEvent(CountDownLatch event)
				throws InterruptedException {
			System.out
					.println("##### TestSubscriber.HandleEvent(CountDownLatch event) ENTER");
			Incr();
			System.out
					.println("##### TestSubscriber.HandleEvent(CountDownLatch event) wait");
			event.await();
			Decr();
			System.out
					.println("##### TestSubscriber.HandleEvent(CountDownLatch event) LEAVE");
			cdl.countDown();
		}

		synchronized void Incr() {
			cntr++;
			sb.append(cntr);
		}

		synchronized void Decr() {
			cntr--;
		}
	}

	@Test
	public void post_DerivedEvent_eventReceivedForBase()
			throws InterruptedException {
		// preperation
		AsyncEventBusImpl eventBus = new AsyncEventBusImpl("test", 4);
		TestDerivedSubscriber sub = new TestDerivedSubscriber();
		eventBus.Register(sub);
		// test & verify
		eventBus.Post(new BaseEvent());
		Thread.sleep(500);
		assertEquals(1, sub.cntrBaseEvent);
		assertEquals(0, sub.cntrDerivedEvent);
		// test & verify
		eventBus.Post(new DerivedEvent());
		Thread.sleep(500);
		assertEquals(2, sub.cntrBaseEvent);
		assertEquals(1, sub.cntrDerivedEvent);
	}

	class BaseEvent {
	}

	class DerivedEvent extends BaseEvent {
	}

	class TestDerivedSubscriber {
		int cntrBaseEvent = 0;
		int cntrDerivedEvent = 0;

		@Subscribe
		public void HandleEvent(BaseEvent event) {
			cntrBaseEvent++;
		}

		@Subscribe
		public void HandleEvent(DerivedEvent event) {
			cntrDerivedEvent++;
		}

	}

	@Test
	public void post_exceptionInSubscriber_exceptionReceivedBySubscriber()
			throws InterruptedException {
		// preperation
		AsyncEventBusImpl eventBus = new AsyncEventBusImpl("exception");
		TestThrowExceptionSubscriber sub = new TestThrowExceptionSubscriber();
		eventBus.Register(sub);
		// test & verify
		eventBus.Post(new String());
		Thread.sleep(500);
	}

	class TestThrowExceptionSubscriber {
		@Subscribe
		public void HandleEvent(String event) {
			throw new RuntimeException("TestThrowExceptionSubscriber");
		}
	}

}
