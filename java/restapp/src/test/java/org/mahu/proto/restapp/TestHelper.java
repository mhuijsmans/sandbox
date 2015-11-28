package org.mahu.proto.restapp;

import org.mahu.proto.restapp.model.ProcessDefinition;
import org.mahu.proto.restapp.model.ProcessTask;
import org.mahu.proto.restapp.model.impl.ProcessBuilder;
import org.mahu.proto.restapp.model.impl.ProcessBuilderException;
import org.mahu.proto.restapp.model.impl.ProcessPathBuilder;
import org.mahu.proto.restapp.task.EndTask;
import org.mahu.proto.restapp.task.TestTaskNext;
import org.mahu.proto.restapp.task.TestTaskPauseWithFuture2;

public class TestHelper {

	/**
	 * process: <task>
	 */
	public static ProcessDefinition createEndTaskProcess(
			final String aName) throws ProcessBuilderException {
		ProcessBuilder pb = new ProcessBuilder(aName);
		return pb.buildProcessDefinition();
	}

	/**
	 * process: <task> <task>
	 */
	public static ProcessDefinition createTwoTasksProcess(final String aName)
			throws ProcessBuilderException {
		ProcessBuilder pb = new ProcessBuilder(aName);
		pb.firstTask("first", TestTaskNext.class) //
				.when(ProcessTask.Result.Next).next("last");
		pb.addTask("last", EndTask.class);
		return pb.buildProcessDefinition();
	}

	// ...................... <task>
	// process: <task> <fork> ...... <join> <last>
	// ...................... <task>
	public static ProcessDefinition createForkJoinProcess(final String aName)
			throws ProcessBuilderException {
		ProcessBuilder pb = new ProcessBuilder(aName);
		pb.firstTask("first", TestTaskNext.class) //
				.when(ProcessTask.Result.Next).next("fork");
		ProcessPathBuilder path1 = pb.createProcessPath("path1");
		ProcessPathBuilder path2 = pb.createProcessPath("path2");
		pb.addFork("fork", new ProcessPathBuilder[] { path1, path2 })
				.withJoin("join").next("last");
		path1.firstTask("1.1", TestTaskNext.class) //
				.when(ProcessTask.Result.Next).next("join");
		path2.firstTask("2.1", TestTaskNext.class) //
				.when(ProcessTask.Result.Next).next("join");
		pb.addTask("last", EndTask.class);
		return pb.buildProcessDefinition();
	}
	
	/**
	 * process: <pausable-task> <task>
	 */	
	public static ProcessDefinition createPausableTaskProcess(final String aName)
			throws ProcessBuilderException {
		ProcessBuilder pb = new ProcessBuilder(aName);
		pb.firstTask("first", TestTaskPauseWithFuture2.class) //
				.when(ProcessTask.Result.Next).next("last");
		pb.addTask("last", EndTask.class);
		return pb.buildProcessDefinition();
	}	
	

}
