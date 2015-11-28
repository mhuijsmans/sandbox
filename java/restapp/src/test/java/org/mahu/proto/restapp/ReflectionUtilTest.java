package org.mahu.proto.restapp;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.mahu.proto.restapp.model.ProcessTask;
import org.mahu.proto.restapp.model.annotation.ProcessTaskResult;
import org.mahu.proto.restapp.util.ReflectionUtil;
import org.mahu.proto.restapp.util.ReflectionUtil.ReflectionUtilException;

public class ReflectionUtilTest {

	public static class A implements ProcessTask {

		public int hi;

		@ProcessTaskResult
		public enum Result {
			OK, NOK
		}

		@Override
		public Enum<?> execute() throws ProcessTaskException {
			return null;
		}
	}

	@Test
	public void testFirstEnumFound() throws ReflectionUtilException {
		Enum<?> e = ReflectionUtil.GetEnumForProcessTaskImpl(
				ReflectionUtilTest.A.class.getName(), "OK");
		assertTrue(e == A.Result.OK);
	}
	
	@Test
	public void testLastEnumFound() throws ReflectionUtilException {
		Enum<?> e = ReflectionUtil.GetEnumForProcessTaskImpl(
				ReflectionUtilTest.A.class.getName(), "NOK");
		assertTrue(e == A.Result.NOK);
	}


	@Test
	public void testCaseInsensitive() throws ReflectionUtilException {
		//
		Enum<?> e = ReflectionUtil.GetEnumForProcessTaskImpl(
				ReflectionUtilTest.A.class.getName(), "ok");
		assertTrue(e == A.Result.OK);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testClassIsNotProcessTask() throws ReflectionUtilException {
		//
		ReflectionUtil.GetProcessTask(ReflectionUtilTest.class.getName());
	}	

}
