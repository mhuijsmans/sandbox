package org.mahu.proto.jnitest.cpptaskexecutor;

import org.mahu.proto.jnitest.model.ResultData;
import org.mahu.proto.jnitest.model.Settings;

public class Task {

	public enum TaskIds {
		NO_OP_TASK(0), TASK1(1), TASK2(2);

		private final int id;

		TaskIds(int id) {
			this.id = id;
		}

		public int getValue() {
			return id;
		}
	}

	public final int taskId;

	public final Settings settings;
	public final ResultData resultData;

	public Task(final TaskIds taskId, final Settings settings,
			final ResultData resultData) {
		this.taskId = taskId.getValue();
		this.settings = settings;
		this.resultData = resultData;
	}

}
