package org.mahu.proto.jerseytools;

import java.net.URI;

/**
 * This class holds the address of a RestResource as it,s 2 main components,
 * - the base and the resource path
 */
public class RestResourceUri {

	private String baseUrl;
	private String resourcePath;

	public RestResourceUri(final String aBaseUrl, final String aResourcePath) {
		resourcePath = aResourcePath;
		baseUrl = aBaseUrl;
	}
	
	public RestResourceUri(final URI aBaseUrl, final String aResourcePath) {
		resourcePath = aResourcePath;
		baseUrl = aBaseUrl.toString();
	}	

	public String getBaseurl() {
		return baseUrl;
	}

	public String getResourcePath() {
		return resourcePath;
	}
	
	public String toString() {
		return baseUrl+"/"+resourcePath;
	}

}
