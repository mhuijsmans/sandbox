package org.mahu.proto.systemtest;

import java.net.URI;

import javax.ws.rs.core.UriBuilder;

public class CommonConst {
	
	public final static int AD_PORT = 8000;
	public final static int SD_PORT = 8001;
	public final static String AD_CONTEXT =  "ad";
	public final static String SD_CONTEXT =  "sd";
	
	public static URI getSdAppBaseURI() {
		return UriBuilder.fromUri("http://localhost/").port(SD_PORT).build();
	}

	public static URI getAdAppBaseURI() {
		return UriBuilder.fromUri("http://localhost/").port(AD_PORT).build();
	}

}
