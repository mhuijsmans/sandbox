package org.mahu.proto.restapp.asyntask;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import org.mahu.proto.restapp.engine.SessionId;

public class AsyncTaskManagerImpl implements AsyncTaskManager {
	private final int nrOfThreads;
	private ExecutorService pool;

	public AsyncTaskManagerImpl(final int nrOfThreads) {
		this.nrOfThreads = nrOfThreads;
	}

	public void Submit(final SessionId id, final Callable<Void> task) {
		if (pool == null) {
			pool = Executors.newFixedThreadPool(nrOfThreads);
		}
		pool.submit(new FutureTask<Void>(task));
	}

	public void cancelAndClear() {
		if (pool != null) {
			pool.shutdownNow();
		}
		pool = null;
	}
}
