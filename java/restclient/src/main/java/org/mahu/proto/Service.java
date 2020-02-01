package org.mahu.proto;

public class Service<T> {
	
	static final Service<String> A = new Service<>(HttpStatus.HTTP_OK, String.class);
	static final Service<String> B = new Service<>(HttpStatus.HTTP_NO_CONTENT, String.class);
	
	// Service contains following properties
	// Expected HttpStatusCode for success
	private final HttpStatus httpStatus;
	// Request Class (if applicable)
	// Response Class (if applicable
	private final Class<?> responseClass;
	// Timeout in sec, 0 means no timeout
	// 
	
	Service(HttpStatus httpStatus, Class<?> responseClass) {
		this.httpStatus = httpStatus;
		this.responseClass = responseClass;
	}
	
	public HttpStatus getHttpStatus() {
		return httpStatus;
	}

	public Class<?> getResponseClass() {
		return responseClass;
	}	

}
