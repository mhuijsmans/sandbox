package org.mahu.proto.restapp.task;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.mahu.proto.restapp.engine.JobList;
import org.mahu.proto.restapp.engine.SessionId;
import org.mahu.proto.restapp.engine.impl.JobImpl;
import org.mahu.proto.restapp.model.Node;
import org.mahu.proto.restapp.model.PausableTask;
import org.mahu.proto.restapp.model.ProcessTask;

public class TestTaskPause implements PausableTask, ProcessTask {

	enum State {
		initial, pauzed, ready, cancelled
	}

	private State state = State.initial;

	@Inject
	private Node node;

	@Inject
	private SessionId sessionId;

	@Inject
	private JobList jobExecutor;

	@Override
	public Enum<?> execute() throws ProcessTaskException {
		switch (state) {
		case initial:
			state = State.pauzed;
			(new Thread() {
				public void run() { 
					try {
						TimeUnit.MILLISECONDS.sleep(100);
						continueExecution();
					} catch (InterruptedException e) {
					}
				}
			}).start();
			return PausableTask.Result.Pauze;
		case pauzed:
			return ProcessTask.Result.Next;
		default:
			throw new ProcessTask.ProcessTaskException("Invalid state: "
					+ state);
		}
	}

	public synchronized void continueExecution() {
		switch (state) {
		case pauzed:
			jobExecutor.add(new JobImpl(sessionId, node, this));
		case cancelled:
			break;
		default:
			// todo: design for reportig exceptions
			throw new RuntimeException("Invalid state: " + state);
		}
	}

	@Override
	public synchronized void cancel() {
		switch (state) {
		case pauzed:
			state = State.cancelled;
			break;
		default:
			break;
		}
	}
	 
}