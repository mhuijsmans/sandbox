package org.mahu.proto.restapp;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

import org.junit.Test;
import org.mahu.proto.restapp.asyntask.AsyncTaskManager;
import org.mahu.proto.restapp.asyntask.AsyncTaskManagerImpl;
import org.mahu.proto.restapp.engine.SessionId;
import org.mahu.proto.restapp.engine.SessionIdManager;

public class AsyncTaskManagerImplTest {

	static class AsyncTask implements Callable<Void> {
		private final CountDownLatch cdl = new CountDownLatch(1);

		@Override
		public Void call() {
			System.out.println("AsyncTaskManagerTest.call ENTER()");
			cdl.countDown();
			System.out.println("AsyncTaskManagerTest.call LEAVE()");
			return null;
		}

		void WaitUntilCalled() throws InterruptedException {
			System.out.println("AsyncTaskManagerTest.WaitUntilCalled()");
			cdl.await();
		}
	}

	@Test(timeout = 2000)
	public void submit_task_taskInvoked() throws InterruptedException {
		AsyncTaskManager asyncTaskManager = new AsyncTaskManagerImpl(2);
		AsyncTask task = new AsyncTask();
		SessionId id = SessionIdManager.Create();
		// execute
		asyncTaskManager.Submit(id, task);
		// Verify
		task.WaitUntilCalled();
	}
}
