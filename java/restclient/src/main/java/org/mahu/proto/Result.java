package org.mahu.proto;

public class Result<T> {

	/**
	 * @return responseBody, will throw exception when not available
	 */
	public T getResponse() {
		return null;
	}

	/**
	 * @return http status code return by server. Will throw exception when
	 *         httpStatusCode is not available, e.g. because of timeout or
	 *         connection problems
	 */
	public int getHttpStatusCode() {
		return 0;
	}

	/**
	 * @return true if HttpResponse is received, with possibly a response body. The
	 *         content of the response is undefined, i.e. can be success or error.
	 */
	public boolean isHttpResponseReceived() {
		return true;
	}

	/**
	 * @return true if problem occured establishing a connnection or established
	 *         connection was lost
	 */
	public boolean isConnctionProblem() {
		return false;
	}

}
