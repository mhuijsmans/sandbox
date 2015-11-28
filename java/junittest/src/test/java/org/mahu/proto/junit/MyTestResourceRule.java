package org.mahu.proto.junit;

/**
 * Before and After are called after every method annotated with $Test
 */
public class MyTestResourceRule extends org.junit.rules.ExternalResource {

	@Override
	protected void after() {
		System.out.println("after");		
	}

	@Override
	protected void before() throws Throwable {
		System.out.println("before");		
	}

}
