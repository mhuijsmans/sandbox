package org.mahu.proto.systemtest;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.mahu.proto.systemtest.persub.PresenceListener;
import org.mahu.proto.systemtest.persub.PresenceNotifier;

public class PresenceTest {

	@Test
	public void manualTest() throws InterruptedException {
		PresenceListener presenceListener = new PresenceListener();
		FutureTask<Void> presenceListenerFuture = new FutureTask<Void>(
				presenceListener);
		FutureTask<Void> presenceNotifierFuture = new FutureTask<Void>(
				new PresenceNotifier("Hi"));
		ExecutorService executor = Executors.newFixedThreadPool(2);
		executor.execute(presenceListenerFuture);
		executor.execute(presenceNotifierFuture);
		TimeUnit.SECONDS.sleep(5);
	}

}
