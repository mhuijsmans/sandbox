package org.mahu.proto.systemtest.baseapplication;

import java.util.concurrent.FutureTask;

import org.mahu.proto.forkprocesstest.ChildProcess;

public class MonitoredChild {
	public final ChildProcess child;
	public final FutureTask<Integer> futureTask;
	
	enum State {Running, Terminated};	
	private State state = State.Running;

	public MonitoredChild(final ChildProcess child,
			final FutureTask<Integer> futureTask) {
		this.child = child;
		this.futureTask = futureTask;
	}
	
	public boolean isStateRunning() {
		return  state == State.Running;
	}
	
	public void setStateTerminated() {
		state = State.Terminated;
	}	

}
