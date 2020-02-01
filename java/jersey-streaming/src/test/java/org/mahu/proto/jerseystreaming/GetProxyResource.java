package org.mahu.proto.jerseystreaming;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path("proxy")
@Produces(MediaType.APPLICATION_OCTET_STREAM)
public class GetProxyResource {

	@Context
	UriInfo uri;

	private final IStreamingProxyGetClient getProxyClient;

	public GetProxyResource() {
		this(new StreamingProxyGetClient());
	}

	GetProxyResource(IStreamingProxyGetClient getProxyClient) {
		this.getProxyClient = getProxyClient;
	}

	@GET
	public Response streamData(@Context HttpHeaders httpHeaders) throws IOException {
		final int port = uri.getBaseUri().getPort();
		// Log.log("Proxy: serverport=" + port);

		final String targetPath = "http://127.0.0.1:" + port + "/" + "abc";

		return getProxyClient.proxy(targetPath, httpHeaders);

//		} catch (Exception e) {
//			Log.log("ProxyResource: " + e.getMessage());
//			e.printStackTrace();
//			throw e;
//		} 
	}

}
