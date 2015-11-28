package org.mahu.proto.restapp.engine;

import org.mahu.proto.restapp.model.PausableTask;

public interface PausedTasksList {

	int size();

	void add(final PausableTask task);

	void remove(final PausableTask task);

	void cancelTasksAndCLear();

}