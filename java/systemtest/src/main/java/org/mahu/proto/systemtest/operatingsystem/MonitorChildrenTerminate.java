package org.mahu.proto.systemtest.operatingsystem;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.mahu.proto.systemtest.baseapplication.MonitoredChild;

public class MonitorChildrenTerminate implements Callable<Void> {

	private static final Logger log = Logger
			.getLogger(MonitorChildrenTerminate.class.getName());

	private final List<MonitoredChild> children;

	public MonitorChildrenTerminate(final List<MonitoredChild> children) {
		this.children = children;
	}

	@Override
	public Void call() {
		try {
			while (true) {
				TimeUnit.SECONDS.sleep(1);
				for (MonitoredChild child : children) {
					if (child.isStateRunning()) {
						if (child.futureTask.isDone()) {
							log.info("Child terminated :" +child.child.getExitValue());
							child.setStateTerminated();
						}
					}
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {

		}
		return null;
	}

}