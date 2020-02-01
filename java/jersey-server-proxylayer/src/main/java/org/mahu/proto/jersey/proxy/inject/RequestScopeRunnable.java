package org.mahu.proto.jersey.proxy.inject;

/**
 * Interface of @ExecutorUsingRequestScope, which encapsulates a Runnable to be executed with support for Guice ServletScopes.REQUEST objects.  
 */
public interface RequestScopeRunnable<T> {
	public T run();
}
