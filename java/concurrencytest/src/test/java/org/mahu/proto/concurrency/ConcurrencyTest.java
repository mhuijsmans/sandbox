package org.mahu.proto.concurrency;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

/**
 * Test for playing with Thread.interrupt for different type of sleep
 */
public class ConcurrencyTest {

	static class MyRunnable1 implements Runnable {
		private boolean isReady = false;
		private final int waitInMs;
		
		MyRunnable1(final int aWaitInMs) {
			waitInMs = aWaitInMs;
		}

		public void run() {
			try {
				TimeUnit.MILLISECONDS.sleep(waitInMs);
			} catch (InterruptedException e) {
				System.out.println("MyRunnable.InterruptedException");
			} finally {
				iAmReady();
			}
		}

		public synchronized void iAmReady() {
			isReady = true;
			notify();
		}

		public synchronized void waitForReady()
				throws InterruptedException {
			if (!isReady) {
				wait();
			}
		}
	};
	
	static class MyRunnable2 implements Runnable {
		private boolean isReady = false;
		private final int waitInMs;
		
		MyRunnable2(final int aWaitInMs) {
			waitInMs = aWaitInMs;
		}

		public void run() {
			try {
				Thread.sleep(waitInMs);
			} catch (InterruptedException e) {
				System.out.println("MyRunnable.InterruptedException");
			} finally {
				iAmReady();
			}
		}

		public synchronized void iAmReady() {
			isReady = true;
			notify();
		}

		public synchronized void waitForReady()
				throws InterruptedException {
			if (!isReady) {
				wait();
			}
		}
	};	
	
	static class MyRunnable3 implements Runnable {
		private boolean isReady = false;
		private final int waitInMs;
		private Object lock = new Object();
		
		MyRunnable3(final int aWaitInMs) {
			waitInMs = aWaitInMs;
		}

		public void run() {
			try {
				Thread.sleep(waitInMs);
			} catch (InterruptedException e) {
				System.out.println("MyRunnable.InterruptedException");
			} finally {
				iAmReady();
			}
		}

		public void iAmReady() {
			synchronized (lock) {
				isReady = true;
				lock.notify();				
			}
		}

		public synchronized void waitForReady()
				throws InterruptedException {
			synchronized (lock) {
				if (!isReady) {
					lock.wait();
				}				
			}			
		}
	};		
	
	// This test explores outcome of Thread.interrupted when there is a short time active
	@Test(timeout = 1000)
	public void testWithShortTimeout() throws InterruptedException {

		MyRunnable1 r = new MyRunnable1(700);

		Thread thread = new Thread(r);
		thread.start();
		TimeUnit.MILLISECONDS.sleep(500);
		thread.interrupt();
		r.waitForReady();
		assertTrue(true);
	}	

	// This test explores outcome of Thread.interrupted when there is a time active and TimeUnit.sleep is used
	@Test(timeout = 1000)
	public void testWithLongTimeoutUsingTimeUnit() throws InterruptedException {

		MyRunnable1 r = new MyRunnable1(10000);

		Thread thread = new Thread(r);
		thread.start();
		TimeUnit.MILLISECONDS.sleep(500);
		thread.interrupt();
		r.waitForReady();
		assertTrue(true);
	}
	
	// This test explores outcome of Thread.interrupted when there is a time active and Thread.sleep is used
	@Test(timeout = 1000)
	public void testWithLongTimeoutUsingThreadSleep() throws InterruptedException {

		MyRunnable2 r = new MyRunnable2(10000);

		Thread thread = new Thread(r);
		thread.start();
		TimeUnit.MILLISECONDS.sleep(500);
		thread.interrupt();
		r.waitForReady();
		assertTrue(true);
	}	
	
	// This test explores outcome of Thread.interrupted when there is a time active and Object is used to sleep/wait.
	@Test(timeout = 1000)
	public void testWithLongTimeoutUsingObjectLock() throws InterruptedException {

		MyRunnable3 r = new MyRunnable3(10000);

		Thread thread = new Thread(r);
		thread.start();
		TimeUnit.MILLISECONDS.sleep(500);
		thread.interrupt();
		r.waitForReady();
		assertTrue(true);
	}	
}
