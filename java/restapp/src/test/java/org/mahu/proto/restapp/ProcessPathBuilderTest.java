package org.mahu.proto.restapp;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mahu.proto.restapp.model.ProcessTask;
import org.mahu.proto.restapp.model.impl.ProcessBuilder;
import org.mahu.proto.restapp.model.impl.ProcessBuilderException;
import org.mahu.proto.restapp.model.impl.ProcessPathBuilder;
import org.mahu.proto.restapp.task.EndTask;
import org.mahu.proto.restapp.task.TestTaskNext;
import org.mahu.proto.restapp.task.TestTaskNull;

public class ProcessPathBuilderTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	/**
	 * Test that fork with no pocessPath is not allowed
	 */
	@Test
	public void errorNoProcessPaths() throws ProcessBuilderException {
		// Preparation
		String name = "process1";
		ProcessBuilder pb = new ProcessBuilder(name);
		pb.firstTask("first", TestTaskNext.class) //
				.when(ProcessTask.Result.Next).next("fork");
		// test
		exception.expect(ProcessBuilderException.class);
		pb.addFork("fork", new ProcessPathBuilder[] {});
	}

	/**
	 * Test that a processPath can be used only once (using same fork)
	 */
	@Test
	public void errorDuplicateProcessPath() throws ProcessBuilderException {
		// Preparation
		String name = "process1";
		ProcessBuilder pb = new ProcessBuilder(name);
		pb.firstTask("first", TestTaskNext.class) //
				.when(ProcessTask.Result.Next).next("fork");
		ProcessPathBuilder path1 = pb.createProcessPath("test");
		// test
		exception.expect(ProcessBuilderException.class);
		pb.addFork("fork", new ProcessPathBuilder[] { path1, path1 });
	}
	
	/**
	 * Test that a processPath can be used only once (using different forks)
	 */
	@Test
	public void errorProcessPathUsedTwice() throws ProcessBuilderException {
		// Preparation
		String name = "process1";
		ProcessBuilder pb = new ProcessBuilder(name);
		pb.firstTask("first", TestTaskNext.class) //
				.when(ProcessTask.Result.Next).next("fork");
		ProcessPathBuilder path1 = pb.createProcessPath("test");
		pb.addFork("fork1", new ProcessPathBuilder[] { path1 });		
		// test
		exception.expect(ProcessBuilderException.class);
		pb.addFork("fork2", new ProcessPathBuilder[] { path1 });
	}
	
	/**
	 * Test that defind but not used prcessPath generates an error 
	 */
	@Test
	public void errorProcessPathNotUsed() throws ProcessBuilderException {
		// Preparation
		String name = "process1";
		ProcessBuilder pb = new ProcessBuilder(name);
		pb.firstTask("first", EndTask.class);
		pb.createProcessPath("test");		
		// test
		exception.expect(ProcessBuilderException.class);
		pb.buildProcessDefinition();
	}		
	
	/**
	 * Test that when processPath name not unique generates an error 
	 */
	@Test
	public void errorProcessPathNameNotUnique() throws ProcessBuilderException {
		// Preparation
		String name = "process1";
		ProcessBuilder pb = new ProcessBuilder(name);
		pb.createProcessPath("test");		
		// test
		exception.expect(ProcessBuilderException.class);
		pb.createProcessPath("test");
	}		

	/**
	 * Test of basic fork-join
	 */
	@Test
	public void processForkJoin() throws ProcessBuilderException {
		// Preparation
		String name = "process1";
		ProcessBuilder pb = new ProcessBuilder(name);
		pb.firstTask("first", TestTaskNext.class) //
				.when(ProcessTask.Result.Next).next("fork");
		ProcessPathBuilder path1 = pb.createProcessPath("path1");
		ProcessPathBuilder path2 = pb.createProcessPath("path2");
		pb.addFork("fork", new ProcessPathBuilder[] { path1, path2 })
				.withJoin("join").next("last");
		path1.firstTask("1.1", TestTaskNull.class) //
				.when(ProcessTask.Result.Next).next("join");
		path2.firstTask("2.1", TestTaskNull.class) //
				.when(ProcessTask.Result.Next).next("join");
		pb.addTask("last", EndTask.class);
		//
		pb.buildProcessDefinition();
	}
	
	/**
	 * Test that names can be re-used in process paths
	 */
	@Test
	public void namesHaveLocalScope() throws ProcessBuilderException {
		// Preparation
		String name = "process1";
		ProcessBuilder pb = new ProcessBuilder(name);
		pb.firstTask("first", TestTaskNull.class) //
				.when(ProcessTask.Result.Next).next("second");
		pb.addTask("second", TestTaskNull.class) //
				.when(ProcessTask.Result.Next).next("fork");
		ProcessPathBuilder path1 = pb.createProcessPath("path1");
		ProcessPathBuilder path2 = pb.createProcessPath("path2");
		pb.addFork("fork", new ProcessPathBuilder[] { path1, path2 })
				.withJoin("join").next("last");
		path1.firstTask("1.1", TestTaskNull.class) //
				.when(ProcessTask.Result.Next).next("second");
		path1.addTask("second", TestTaskNull.class) //
				.when(ProcessTask.Result.Next).next("join");
		path2.firstTask("2.1", TestTaskNull.class) //
				.when(ProcessTask.Result.Next).next("second");
		path2.addTask("second", TestTaskNull.class) //
				.when(ProcessTask.Result.Next).next("join");
		pb.addTask("last", EndTask.class);
		// test
		pb.buildProcessDefinition();
	}	

}
