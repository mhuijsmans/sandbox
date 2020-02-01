package org.mahu.proto.jersey.proxy.inject;

import javax.ws.rs.core.Response;

import com.google.inject.AbstractModule;

/**
 * The ISgseApiRequestPreProcessor creates a request and executes it. The
 * implementation uses a SgseApi state machine to execute it. The SgseApi state
 * machine will check if a request is allowed prior to execution.
 *
 * The implementation used a DI framework to create a request specific object
 * graph.
 */
public interface IRequestPreProcessor {

	// @formatter:off
	/**
	 * Execute a request using the provided interface. The interface shall have one
	 * or more methods that return a Response object. The method name and arguments
	 * are free to choose. The typical use case is for a REST interface with a
	 * request body.
	 *
	 * The interface shall have the @RequestProperties annotation.
	 *
	 * Example of an interface IRequestExample with @RequestProperties annotation
	 * where tag is set to SgseApiRequestId.EXAMPLE. The implementation for the
	 * interface is provided by the ApplicationModule. <code>
	 * &#64;RequestProperties(tag = SgseApiRequestId.EXAMPLE)
	 * interface IRequestExample {
	 *   Response doSomething(RequestData requestData);
	 * }
	 *
	 * ISgseApiRequestPreProcessor processor = ..
	 * RequestData requestData = ..
	 * processor.with(IRequestExample.class).doSomething(requestData);
	 * </code>
	 *
	 * @param class of a requestInterface
	 * @return an object or type T
	 */
	// @formatter:on
	<T> T with(Class<T> requestInterface);
}
