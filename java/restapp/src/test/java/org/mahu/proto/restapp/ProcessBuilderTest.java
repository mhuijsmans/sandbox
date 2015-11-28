package org.mahu.proto.restapp;

import static org.junit.Assert.assertTrue;
import static org.mahu.proto.restapp.AssertP.assertNodeInstanceOf;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mahu.proto.restapp.model.Node;
import org.mahu.proto.restapp.model.ProcessDefinition;
import org.mahu.proto.restapp.model.ProcessTask;
import org.mahu.proto.restapp.model.impl.ProcessBuilder;
import org.mahu.proto.restapp.model.impl.ProcessBuilderException;
import org.mahu.proto.restapp.task.EndTask;
import org.mahu.proto.restapp.task.TestTaskNull;
import org.mahu.proto.restapp.task.TestTaskOkNok;

public class ProcessBuilderTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	/**
	 * Process name null
	 */
	@Test
	public void errorProcessnameNull() throws ProcessBuilderException {
		// preperation
		// test
		exception.expect(ProcessBuilderException.class);
		new ProcessBuilder(null);
	}
	
	/**
	 * Process name length 0
	 */
	@Test
	public void errorProcessnameLengthZero() throws ProcessBuilderException {
		// preperation
		// test
		exception.expect(ProcessBuilderException.class);
		new ProcessBuilder("");
	}	
	

	/**
	 * Process definition without any data
	 */
//	@Test
//	public void errorNothingDefined() throws ProcessBuilderException {
//		// preperation
//		ProcessBuilder pb = new ProcessBuilder();
//		// test
//		exception.expect(ProcessBuilderException.class);
//		pb.getProcessDefinition();
//	}

	/**
	 * Process 2nd firstTask defined
	 */
	@Test
	public void errorDuplicateFirst() throws ProcessBuilderException {
		// preperation
		String name = "process1";
		ProcessBuilder pb = new ProcessBuilder(name);
		pb.firstTask("first", TestTaskOkNok.class);
		// test
		exception.expect(ProcessBuilderException.class);
		pb.firstTask("first", TestTaskOkNok.class);
	}

	/**
	 * Process uses a results twice 
	 */
	@Test
	public void errorDuplicateResult() throws ProcessBuilderException {
		// preperation
		String name = "process1";
		ProcessBuilder pb = new ProcessBuilder(name);
		Node node = pb.firstTask("first", TestTaskOkNok.class)
		//
				.when(TestTaskOkNok.Result.OK).next("second");
		// test
		exception.expect(IllegalStateException.class);
		node.when(TestTaskOkNok.Result.OK).next("third");
	}
	
	/**
	 * Process has a next task that is unknown
	 */
	@Test
	public void errorUnknownNextTask() throws ProcessBuilderException {
		// preperation
		String name = "process1";
		ProcessBuilder pb = new ProcessBuilder(name);
		pb.firstTask("first", TestTaskOkNok.class) //
				.when(TestTaskOkNok.Result.OK).next("done")
				.when(TestTaskOkNok.Result.NOK).next("Done");
		pb.addTask("done", EndTask.class);
		// test
		exception.expect(ProcessBuilderException.class);
		pb.buildProcessDefinition();
	}
	
	/**
	 * Process has when() but no next task
	 */
	@Test
	public void errorWhenButNoNextTask() throws ProcessBuilderException {
		// preperation
		String name = "process1";
		ProcessBuilder pb = new ProcessBuilder(name);
		Node node = pb.firstTask("first", TestTaskOkNok.class);
		node.when(TestTaskOkNok.Result.OK);
		node.when(TestTaskOkNok.Result.NOK).next("Done");
		pb.addTask("done", EndTask.class);
		// test
		exception.expect(ProcessBuilderException.class);
		pb.buildProcessDefinition();
	}	
	
	/**
	 * Process has node name is null
	 */
	@Test
	public void errorNodeNameNull() throws ProcessBuilderException {
		// preperation
		String name = "process1";
		ProcessBuilder pb = new ProcessBuilder(name);
		// test
		exception.expect(ProcessBuilderException.class);		
		pb.addTask(null, TestTaskOkNok.class);
	}	
	
	/**
	 * Process has node name length 0
	 */
	@Test
	public void errorNodeNameLengthZero() throws ProcessBuilderException {
		// preperation
		String name = "process1";
		ProcessBuilder pb = new ProcessBuilder(name);
		// test
		exception.expect(ProcessBuilderException.class);		
		pb.addTask("", TestTaskOkNok.class);
	}	
	
	/**
	 * Process has duplicate node name
	 */
	@Test
	public void errorDuplicateName() throws ProcessBuilderException {
		// preperation
		String name = "process1";
		ProcessBuilder pb = new ProcessBuilder(name);
		pb.addTask("first", TestTaskOkNok.class);
		// test
		exception.expect(ProcessBuilderException.class);		
		pb.addTask("first", TestTaskOkNok.class);
	}
	
	
	/**
	 * Trying to connect null result
	 */
	@Test
	public void errorWhenWithNullResult() throws ProcessBuilderException {
		// preperation
		String name = "process1";
		ProcessBuilder pb = new ProcessBuilder(name);
		// test
		exception.expect(ProcessBuilderException.class);		
		pb.addTask("first", TestTaskNull.class).when(ProcessTask.Result.Null);
	}	
	
	/**
	 * Process has no first task
	 */
	@Test
	public void errorNoFirstTask() throws ProcessBuilderException {
		// preperation
		String name = "process1";
		ProcessBuilder pb = new ProcessBuilder(name);
		pb.addTask("first", TestTaskOkNok.class)
				//
				.when(TestTaskOkNok.Result.OK).next("second")
				.when(TestTaskOkNok.Result.NOK).next("third");
		pb.addTask("second", TestTaskNull.class).isFinal();
		pb.addTask("third", TestTaskNull.class).isFinal();
		// test
		exception.expect(ProcessBuilderException.class);		
		pb.buildProcessDefinition();
	}	
	
	/**
	 * Process has a (end) task that is not referenced.
	 */
	@Test
	public void errorOrphanedTask() throws ProcessBuilderException {
		// preperation
		String name = "process1";
		ProcessBuilder pb = new ProcessBuilder(name);
		pb.firstTask("first", TestTaskOkNok.class)
				//
				.when(TestTaskOkNok.Result.OK).next("second")
				.when(TestTaskOkNok.Result.NOK).next("second");
		pb.addTask("second", EndTask.class).isFinal();
		pb.addTask("third", EndTask.class).isFinal();
		// test
		exception.expect(ProcessBuilderException.class);		
		pb.buildProcessDefinition();
	}
	
	/**
	 * Test that detects that ProcessTask is not public 
	 * (needed to create instance)
	 */
	static class NonePublicTask implements ProcessTask {

		@Override
		public Enum<?> execute() {
			return ProcessTask.Result.Null;
		}
	}	
	@Test
	public void errorNonePublicTask() throws ProcessBuilderException {
		// preperation
		String name = "process1";
		ProcessBuilder pb = new ProcessBuilder(name);
		// test
		exception.expect(ProcessBuilderException.class);		
		pb.firstTask("first", NonePublicTask.class);
	}	
	
	/**
	 * Test that detects that ProcessTask does not have a default constructor 
	 */
	static class NoDefaultCtorTask implements ProcessTask {
		
		public NoDefaultCtorTask(final int i) {
			// ignore
		}

		@Override
		public Enum<?> execute() {
			return null;
		}
	}	
	@Test
	public void errorNoDefaultCtorTask() throws ProcessBuilderException {
		// preperation
		String name = "process1";
		ProcessBuilder pb = new ProcessBuilder(name);
		// test
		exception.expect(ProcessBuilderException.class);		
		pb.firstTask("first", NoDefaultCtorTask.class);
	}	

	/**
	 * Process has orphaned task that references itself
	 */
	@Test
	public void errorProcessWithOrphanedSelfReferencingTask() throws ProcessBuilderException {
		// preperation
		String name = "process1";
		ProcessBuilder pb = new ProcessBuilder(name);
		// test
		exception.expect(ProcessBuilderException.class);		
		pb.addTask("third", TestTaskOkNok.class)		//
		.when(TestTaskOkNok.Result.OK).next("third");
	}
	
	/**
	 * Process has task that references itself
	 */
	@Test
	public void errorProcessSelfReferencingTask() throws ProcessBuilderException {
		String name = "process1";
		ProcessBuilder pb = new ProcessBuilder(name);
		// test
		exception.expect(ProcessBuilderException.class);		
		pb.firstTask("first", TestTaskOkNok.class)//
		.when(TestTaskOkNok.Result.OK).next("first");
	}
	
	/**
	 * Process tries to connect Null
	 */
	@Test
	public void errorNullIsNotAllowedinWhen() throws ProcessBuilderException {
		String name = "process1";
		ProcessBuilder pb = new ProcessBuilder(name);
		pb.addTask("last", EndTask.class);
		// test
		exception.expect(ProcessBuilderException.class);		
		pb.firstTask("first", TestTaskNull.class)//
		.when(ProcessTask.Result.Null).next("last");
	}	
	
	/**
	 * Process can not use Next when it has defined own results.
	 */
	@Test
	public void errorNextNotAllowedWhenTaskHasOwnResults() throws ProcessBuilderException {
		String name = "process1";
		ProcessBuilder pb = new ProcessBuilder(name);
		// test
		exception.expect(ProcessBuilderException.class);		
		pb.firstTask("first", TestTaskOkNok.class) //
		.when(ProcessTask.Result.Next);
	}	

	@Test
	public void processThreeTasks() throws ProcessBuilderException {
		// preperation
		String name = "process1";
		ProcessBuilder pb = new ProcessBuilder(name);
		pb.firstTask("first", TestTaskOkNok.class)
				//
				.when(TestTaskOkNok.Result.OK).next("second")
				.when(TestTaskOkNok.Result.NOK).next("third");
		pb.addTask("second", TestTaskNull.class).isFinal();
		pb.addTask("third", TestTaskNull.class).isFinal();
		// test
		ProcessDefinition prodDef = pb.buildProcessDefinition();
		assertTrue(prodDef.getName().equals(name));
		assertNodeInstanceOf(prodDef.getFirstNode(), TestTaskOkNok.class);
	}

}
