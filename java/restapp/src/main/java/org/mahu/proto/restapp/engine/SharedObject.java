package org.mahu.proto.restapp.engine;

/**
 * The class holds data that set & get from different threads. Make sure that
 * the data that is added is either (preferred) immutable (all variables are
 * final private) or that all methods that expose state are synchronized.
 */
public class SharedObject {

	private Object object;

	public synchronized void Set(final Object object) {
		if (this.object == null) {
			// Object must be set, before notify to ensure that an entity
			// that has called WaitForCompletion() will find the data present
			// when it wakes up.
			this.object = object;
			notify();
		}
	}

	public synchronized Object WaitForDataSet() throws InterruptedException {
		if (this.object == null) {
			wait();
		};
		return object;
	}
}