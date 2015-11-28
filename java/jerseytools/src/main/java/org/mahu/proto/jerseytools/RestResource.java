package org.mahu.proto.jerseytools;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.ConnectException;
import java.util.logging.Logger;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlRootElement;

import org.mahu.proto.commons.DebugUtil;

public class RestResource<R> {

	private final static Logger LOGGER = Logger.getLogger(RestResource.class
			.getName());

	// From JavaDoc
	// Signals that an error occurred while attempting to connect a socket to a
	// remote address and port.
	// Typically, the connection was refused remotely (e.g., no process is
	// listening on the remote address/port).
	public final static int RESPONSE_CODE_OTHER_ERROR = -1;
	public final static int RESPONSE_CODE_CONNECTION_EXCEPTION = -2;
	public final static int RESPONSE_CODE_CONNECTION_TIMEOUT = -3;
	public final static int RESPONSE_CODE_CONNECTION_LOST = -4;

	private R data;
	private int responseCode;
	private int length = -1;
	private WebTarget target;
	private final Class<R> cls;
	private String mediaType = MediaType.TEXT_XML;

	public RestResource(final RestResourceUri resource, final Class<R> aCls) {
		checkNotNull(resource);
		checkNotNull(aCls);
		Client client = ClientBuilder.newClient();
		target = client.target(resource.getBaseurl()).path(
				resource.getResourcePath());
		cls = aCls;
		setTimeoutVaklues();
	}

	public RestResource(final WebTarget aTarget, final Class<R> aCls) {
		checkNotNull(aTarget);
		checkNotNull(aCls);
		target = aTarget;
		cls = aCls;
		setTimeoutVaklues();
	}

	private void setTimeoutVaklues() {
		// todo: connect and read timeout
		// target.property(ClientProperties.CONNECT_TIMEOUT, 1000);
		// target.property(ClientProperties.READ_TIMEOUT, 1000);
	}

	public void setMediaType(final String aMediaType) {
		checkNotNull(aMediaType);
		mediaType = aMediaType;
	}

	public void doGet() {
		callHttpMethod(new RestMethod() {
			@Override
			public Response invoke() {
				return target.request(mediaType).get();
			}

		});
	}

	public void doPut(final Object objectToPut) {
		checkNotNull(objectToPut);
		callHttpMethod(new RestMethod() {
			@Override
			public Response invoke() {
				return target.request(mediaType).put(
						Entity.entity(objectToPut, MediaType.TEXT_XML));
			}
		});
	}

	public void doPost() {
		doPost(new PostDummy());
	}

	public void doPost(final Object objectToPost) {
		checkNotNull(objectToPost);
		callHttpMethod(new RestMethod() {
			@Override
			public Response invoke() {
				return target.request().post(
						Entity.entity(objectToPost, MediaType.TEXT_XML));
			}
		});
	}

	public void doDelete() {
		callHttpMethod(new RestMethod() {
			@Override
			public Response invoke() {
				return target.request(mediaType).delete();
			}

		});
	}

	public R getData() {
		return data;
	}

	public int getResponseCode() {
		return responseCode;
	}
	
	public int getLength() {
		return length;
	}	

	interface RestMethod {
		public Response invoke();
	}

	private void callHttpMethod(final RestMethod method) {
		checkNotNull(method);
		data = null;
		Response response = null;
		try {
			response = method.invoke();
			processResponse(response);
			length = response.getLength();
		} catch (ProcessingException e) {
			// The root cause can be several levels deep
			Throwable rootcause = getRootcause(e);
			/**
			 * This exception can mean many things, but also a problem with the
			 * processing of the response.
			 */
			if (rootcause instanceof ConnectException) {
				responseCode = RESPONSE_CODE_CONNECTION_EXCEPTION;
			} else if (rootcause instanceof java.net.SocketTimeoutException) {
				responseCode = RESPONSE_CODE_CONNECTION_TIMEOUT;
			} else if (rootcause instanceof java.net.SocketException) {
				// SocketReset or HttpProxy can not be reached
				//
				// todo: I also get this when SDSS/Proxy is not running
				// Does this mean some action is needed?
				log(e);
				responseCode = RESPONSE_CODE_CONNECTION_LOST;
			} else {
				log(e);
				responseCode = RESPONSE_CODE_OTHER_ERROR;
			}
		} catch (Exception e) {
			log(e);
			responseCode = RESPONSE_CODE_OTHER_ERROR;
		}
	}

	private void processResponse(Response response) {
		responseCode = response.getStatus();
		if (responseCode == 200) {
			// Reading data also closes response
			// Todo: read data may be invalid. Who closes?
			// Todo: is 200 the only / right way to process the response entity
			data = response.readEntity(cls);
		} else {
			response.close();
		}
	}

	private void log(final Exception e) {
		StringBuilder sb = DebugUtil.createReport(e);
		LOGGER.warning(sb.toString());
	}

	private static Throwable getRootcause(final Throwable e) {
		return (e.getCause() != null) ? getRootcause(e.getCause()) : e;
	}

	@XmlRootElement(name = "PostDummy")
	private static class PostDummy {
	}

}
