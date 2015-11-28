package org.mahu.proto.restapp.task;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.mahu.proto.restapp.engine.JobList;
import org.mahu.proto.restapp.engine.SessionId;
import org.mahu.proto.restapp.engine.impl.JobImpl;
import org.mahu.proto.restapp.model.Node;
import org.mahu.proto.restapp.model.PausableTask;
import org.mahu.proto.restapp.model.ProcessTask;

public class TestTaskPauseWithNotifier implements PausableTask, ProcessTask {

	enum State {
		initial, pauzed, ready, cancelled
	}

	private State state = State.initial;
	private Exception exception;

	@Inject
	private Node node;

	@Inject
	private SessionId sessionId;

	@Inject
	private JobList jobList;

	@Inject
	private TestTaskPauseNotifier notifier;

	@Override
	public Enum<?> execute() throws ProcessTaskException {
		switch (state) {
		case initial:
			notifier.invoked();
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
			notifier.resume();
			if (exception != null) {
				throw new ProcessTask.ProcessTaskException(exception);
			} else {
				return ProcessTask.Result.Next;
			}
		case cancelled:
			return ProcessTask.Result.Null;
		default:
			throw new ProcessTask.ProcessTaskException(
					"TestTaskPauzeWithNotifier execute() in invalid state: "
							+ state);
		}
	}

	public synchronized void continueExecution() {
		switch (state) {
		case pauzed:
			notifier.readyToResume();
			jobList.add(new JobImpl(sessionId, node, this));
		case cancelled:
			break;
		default:
			exception = new IllegalStateException("Invalid state: " + state);
		}
	}

	@Override
	public synchronized void cancel() {
		switch (state) {
		case pauzed:
			notifier.cancelled();
			state = State.cancelled;
			break;
		default:
			break;
		}
	}

}