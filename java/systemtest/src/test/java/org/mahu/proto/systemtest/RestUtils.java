package org.mahu.proto.systemtest;

import javax.ws.rs.core.MediaType;

import org.mahu.proto.jerseyjunittools.RestResource;
import org.mahu.proto.jerseyjunittools.RestResourceUri;

public class RestUtils {

	public static  RestResource<String> startCallToRunApplications() {
		RestResource<String> resource = new RestResource<String>(
				new RestResourceUri(CommonConst.getAdAppBaseURI(),
						CommonConst.AD_CONTEXT), String.class);
		resource.setMediaType(MediaType.TEXT_PLAIN);
		//
		resource.doGet();
		return resource;
	}

}
