package org.mahu.proto.restapp.task;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.mahu.proto.restapp.engine.JobList;
import org.mahu.proto.restapp.engine.SessionId;
import org.mahu.proto.restapp.engine.impl.JobImpl;
import org.mahu.proto.restapp.model.Node;
import org.mahu.proto.restapp.model.PausableTask;
import org.mahu.proto.restapp.model.ProcessTask;

public class TestTaskPauseTooLong implements PausableTask, ProcessTask {

	enum State {
		initial, pauzed, ready, cancelled
	}

	private State state = State.initial;
	private Thread thread;

	@Inject
	private Node node;

	@Inject
	private SessionId sessionId;

	@Inject
	private JobList jobList;

	@Override
	public Enum<?> execute() throws ProcessTaskException {
		switch (state) {
		case initial:
			state = State.pauzed;
			thread = new Thread() {
				public void run() {
					try {
						TimeUnit.MILLISECONDS.sleep(10000);
						continueExecution();
					} catch (InterruptedException e) {
					}
				}
			};
			thread.start();
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
			// TODO: this needs to be a wrapped in a
			// ContinueExecutionNotification().
			jobList.add(new JobImpl(sessionId, node, this));
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
			thread.interrupt();
			state = State.cancelled;
			break;
		default:
			break;
		}
	}

}