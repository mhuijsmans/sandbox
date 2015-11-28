package org.mahu.proto.commons;

public class WaitForTrue {

	private volatile boolean flag = false;

	public synchronized boolean waitForTrue(final int maxWaitInSec) {
		if (!flag) {
			try {
				if (maxWaitInSec>0) {
				wait(maxWaitInSec);
				} else {
					wait();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return flag;
	}

	public synchronized void setTrue() {
		flag = true;
		notifyAll();
	}

	public boolean value() {
		return flag;
	}
}
