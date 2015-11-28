package org.mahu.proto.executionqueuetest;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * A ExecutionQueue for tasks. All tasks are executed in sequence.
 * Tasks execute asynchronous, i.e. execution of tasks is not done in the
 * thread of the caller. Tasks shall be concurrency ready, i.e. through
 * use of final. Read book Concurrent programming in Java by Doug Lee
 * on how to do this.    
 */
public class SingleThreadExecutionQueue {
	
	private static final Logger log = Logger.getLogger(SingleThreadExecutionQueue.class.getName());

	/**
	 * Listener that get notification an start&completion of a task
	 */
	public interface ExecutionListener {
		public void preExecute(final Runnable command);

		public void postExecute(final Runnable command);
	}

	/**
	 * Max timeout used for graceful shutdown. After this time, a forced shutdown is done.
	 */
	public static final int SHUTDOWN_TIMEOUT_IN_SECONDS = 5;

	private final ExecutorService executor;
	private final String name;
	private ExecutionListener listener;

	public SingleThreadExecutionQueue(final String name) {
		this(Executors.newSingleThreadExecutor(), name);
	}

	public SingleThreadExecutionQueue(final String name,
			final UncaughtExceptionHandler uncaughtExceptionHandler) {
		this(Executors.newSingleThreadExecutor(), name);
		setUncaughtExecutionHandler(uncaughtExceptionHandler);
	}

	/**
	 * Execute a task
	 */
	public void execute(final Runnable task) {
		if (!executor.isShutdown()) {
			if (listener != null) {
				executor.execute(new TaskWrapper(task, listener));
			} else {
				executor.execute(task);
			}
		} else {
			log.warning("Executing on a shutdown queue is not allowed: "+name);
		}
	}

	/**
	 * Initiate a shutdown. First gracefull. After timeout, forced shutdown.
	 */
	public void shutdown() {
		log.fine("Shutdown started: " + name);
		executor.shutdown();
		boolean isShutdown = false;
		try {
			isShutdown = executor.awaitTermination(
					SingleThreadExecutionQueue.SHUTDOWN_TIMEOUT_IN_SECONDS,
					TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			log.warning("Gracfeful termination was interrupted: " + name);
		}
		if (!isShutdown) {
			log.warning("Forced shutdown: " + name);
			executor.shutdownNow();
		}
		log.fine("Shutdown completed SingleThreadExecutionQueue: " + name);
	}

	/**
	 * Add a listener. Only a single listener is supported. Once set, it can not be undone.
	 */
	public void setListener(ExecutionListener listener) {
		checkNotNull(listener);
		this.listener = listener;
	}
	
	private SingleThreadExecutionQueue(final ExecutorService executor,
			final String name) {
		checkNotNull(name);
		this.executor = executor;
		this.name = "SingleThreadExecutionQueue[" + name + "]";
		log.info("Starting " + this.name);
		Thread.currentThread().setName(this.name);
	}	

	private void setUncaughtExecutionHandler(
			final UncaughtExceptionHandler uncaughtExceptionHandler) {
		checkNotNull(uncaughtExceptionHandler);
		Thread.currentThread().setUncaughtExceptionHandler(
				uncaughtExceptionHandler);
	}

	/**
	 * Innerclass used when a listener is active
	 */
	static class TaskWrapper implements Runnable {
		private final Runnable task;
		private final ExecutionListener listener;

		TaskWrapper(final Runnable task,
				ExecutionListener listener) {
			checkNotNull(task);
			checkNotNull(listener);
			this.task = task;
			this.listener = listener;
		}

		@Override
		public void run() {
			listener.postExecute(task);
			task.run();
			listener.postExecute(task);
		}
	}
}
