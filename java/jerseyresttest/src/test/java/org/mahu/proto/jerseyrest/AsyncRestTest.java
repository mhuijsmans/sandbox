package org.mahu.proto.jerseyrest;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.LinkedList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.core.MediaType;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mahu.proto.commons.Chrono;
import org.mahu.proto.commons.Now;
import org.mahu.proto.jerseyjunittools.RestResourceBase;
import org.mahu.proto.jerseyjunittools.RestServiceRule;
import org.mahu.proto.jerseyjunittools.annotation.RestResourceInTest;

/**
 * Different test for async REST
 */
public class AsyncRestTest {

	private final static String GET_RESPONSE = "JAX-RS-GET";
	@Rule
	public RestServiceRule restService = new RestServiceRule();

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Path("helloworld")
	// Note that class must be public
	public static class HelloWorldResource {
		/**
		 * Method handling HTTP GET requests, returning a "text/plain" media
		 * type.
		 */
		@GET
		@Produces(MediaType.TEXT_PLAIN)
		public String doGet() {
			System.out.println(HelloWorldResource.class.getSimpleName()
					+ ".GET");
			return GET_RESPONSE;
		}
	}
	
	@Path("helloworldWaitXSec")
	// Note that class must be public
	public static class HelloWorldResourceOneSec {

		@GET
		@Produces(MediaType.TEXT_PLAIN)
		public String doGet() throws InterruptedException {
			System.out.println(HelloWorldResource.class.getSimpleName()
					+ ".GET " + Now.asString());
			TimeUnit.SECONDS.sleep(1);
			return GET_RESPONSE;
		}
		
		@GET
		@Path("{maxWait}")
		@Produces(MediaType.TEXT_PLAIN)
		public String doGet(@PathParam("maxWait") int maxWait) throws InterruptedException {
			System.out.println(HelloWorldResource.class.getSimpleName()
					+ ".GET sleeping " +maxWait+" ms; " + Now.asString());
			TimeUnit.MILLISECONDS.sleep(maxWait);
			return GET_RESPONSE+"-"+maxWait;
		}		
	}

	// The text case code is based on: https://java.net/jira/browse/JERSEY-2162
	@Test
	@RestResourceInTest(resource = HelloWorldResource.class)
	public void testMethodGetAsync() {
		final ReentrantLock lock = new ReentrantLock();
		final Condition responseReceived = lock.newCondition();
		RestResourceBase restBase = restService.getRestResourceBase();
		Future<String> _response = restBase.target("helloworld").request()
				.async().get(new InvocationCallback<String>() {
					public void completed(final String response) {
						System.out.println("COMPLETED");
						lock.lock();
						try {
							responseReceived.signalAll();
						} finally {
							lock.unlock();
						}
					}

					public void failed(final Throwable throwable) {
						System.out.println("FAILED");
						lock.lock();
						try {
							responseReceived.signalAll();
						} finally {
							lock.unlock();
						}
					}
				});
		lock.lock();
		try {
			System.out.println("Waiting for response.");
			responseReceived.await();
			System.out.println("Response received.");
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Test with port for which there is no service (using asserts)
	 * 
	 * @throws InterruptedException
	 * 
	 * @throws ExecutionException
	 */
	@Test
	@RestResourceInTest(resource = HelloWorldResource.class)
	public void testErrorNotConnectedPort1() throws InterruptedException,
			ExecutionException {
		// preperation
		RestResourceBase restBase = createRestResourceWithNotConnectedPort();
		Future<String> _response = asyncGet(restBase, "helloworld");
		// test
		try {
			System.out.println("Waiting for response.");
			// Blocking wait until future has finished
			String respValue = _response.get();
			System.out.println("Response received.");
		} catch (InterruptedException e) {
			System.out.println("Response InterruptedException received.");
			fail();
		} catch (ExecutionException e) {
			System.out.println("Response exception received."
					+ e.getCause().getMessage());
			Throwable cause = e.getCause();
			if (cause instanceof javax.ws.rs.ProcessingException) {
				cause = cause.getCause();
			}
			System.out.println("Response exception cause."
					+ e.getCause().getClass());
			assertTrue(cause instanceof java.net.ConnectException);
		}
	}

	/**
	 * Test with port for which there is no service (uses ConnectionMatcher to test exception)
	 * 
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	@Test
	@RestResourceInTest(resource = HelloWorldResource.class)
	public void testErrorNotConnectedPort2() throws InterruptedException,
			ExecutionException {
		// preperation
		RestResourceBase restBase = createRestResourceWithNotConnectedPort();
		Future<String> _response = asyncGet(restBase, "helloworld");
		// test
		System.out.println("Waiting for response.");
		exception.expect(new ConnectionExceptionMatcher());
		// Future.get blocks until result is available.
		_response.get();
	}
	
	/**
	 * Test if MAX rest call will execute in parallel to some resource via SAME connection
	 * 
	 * Impression for this and next test is that the number of outstanding request depends on number
	 * of available server threads, which seem to be around 8.
	 * SAME performs slightly better than different. 
	 * 
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	@Test
	@RestResourceInTest(resource = HelloWorldResourceOneSec.class)
	public void testParallelCallsViaSameConnection() throws InterruptedException,
			ExecutionException {
		// preperation
		int MAX = 30;
		RestResourceBase restBase = restService.getRestResourceBase();
		Chrono chrono = new Chrono();
		LinkedList<Future<String>> list = new LinkedList<Future<String>>();
		for(int i=1; i<MAX; i++) {
			list.add(asyncGet(restBase, "helloworldWaitXSec/"+(100*i)));
		}
		// test
		System.out.println("Waiting for response.");
		// Future.get() blocks until completed; 
		for(int i=1; i<MAX; i++) {		
			Future<String> f = list.removeFirst();
			assertTrue(f.get().equals(GET_RESPONSE+"-"+(100*i)));
		}
		System.out.println("All responses received in "+chrono.elapsedMs()+" ms");
	}	
	
	/**
	 * Test if MAX rest call will execute in parallel to some resource via DIFFERENT connections
	 * Impression: see SAME connection
	 * 
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	@Test
	@RestResourceInTest(resource = HelloWorldResourceOneSec.class)
	public void testParallelCallsViaDifferentConnections() throws InterruptedException,
			ExecutionException {
		// preperation
		int MAX = 30;
		Chrono chrono = new Chrono();
		LinkedList<Future<String>> list = new LinkedList<Future<String>>();
		for(int i=1; i<MAX; i++) {
			RestResourceBase restBase = restService.getRestResourceBase();
			list.add(asyncGet(restBase, "helloworldWaitXSec/"+(100*i)));
		}
		// test
		System.out.println("Waiting for response.");
		// Future.get() blocks until completed; 
		for(int i=1; i<MAX; i++) {		
			Future<String> f = list.removeFirst();
			assertTrue(f.get().equals(GET_RESPONSE+"-"+(100*i)));
		}
		System.out.println("All responses received in "+chrono.elapsedMs()+" ms");
	}

	private RestResourceBase createRestResourceWithNotConnectedPort() {
		return new RestResourceBase(restService.getBaseURI(),restService.getBaseURI().getPort() + 1000);
	}

	public class ConnectionExceptionMatcher extends
			TypeSafeMatcher<ExecutionException> {
		ExecutionException exception;
		Throwable cause;

		@Override
		protected boolean matchesSafely(final ExecutionException anException) {
			exception = anException;
			cause = anException.getCause();
			if (cause instanceof javax.ws.rs.ProcessingException) {
				cause = cause.getCause();
			}
			return cause instanceof java.net.ConnectException;
		}

		@Override
		public void describeTo(Description description) {
			if (cause == null) {
				description.appendText(" cause missing, exception: "
						+ exception.getMessage());
			} else {
				description
						.appendText(" java.net.ConnectException was not found instead: "
								+ cause.getClass().getName());
			}
		}
	}

	private Future<String> asyncGet(final RestResourceBase restBase, final String path) {
		Future<String> _response = restBase.target(path).request().async()
				.get(new InvocationCallback<String>() {
					public void completed(final String response) {
						System.out.println("COMPLETED");
					}

					public void failed(final Throwable throwable) {
						System.out.println("FAILED: " + throwable.getMessage());
					}
				});
		return _response;
	}

}
