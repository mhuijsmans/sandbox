package org.mahu.proto.restappextra.remoteservice;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.mahu.proto.jerseytools.RestResource;
import org.mahu.proto.jerseytools.RestResourceUri;
import org.mahu.proto.restappextra.config.SystemConfigurationImpl;

@Path("/v")
public class VRESTService {

	private SystemConfigurationImpl config = new SystemConfigurationImpl(
			org.mahu.proto.restappextra.TestConst.SYSTEM1_VPORT,
			org.mahu.proto.restappextra.TestConst.SYSTEM1_SPORT);

	@GET
	@Path("/ld")
	@Produces(MediaType.TEXT_PLAIN)
	public Response getld() {
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		System.out.println("### " + name + " ###");
		//
		BL(name);
		return Response.ok("hello").build();
	}

	@GET
	@Path("/ed")
	@Produces(MediaType.TEXT_PLAIN)
	public Response geted() {
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		System.out.println("### " + name + " ###");
		TL(name);		
		return Response.ok("hello").build();
	}

	@GET
	@Path("/sed")
	@Produces(MediaType.TEXT_PLAIN)
	public Response getsed() {
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		System.out.println("### " + name + " ###");
		return Response.ok("hello").build();
	}

	@GET
	@Path("/bc")
	@Produces(MediaType.TEXT_PLAIN)
	public Response getbc() {
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		System.out.println("#####################################\n" + name
				+ ".get()\n#####################################");
		return Response.ok("hello").build();
	}
	
	private void BL(String name) {
		{
		RestResourceUri uri = new RestResourceUri(config.GetSBaseUrl(), "s/bl");
		RestResource<String> resource = new RestResource<String>(uri,
				String.class);
		System.out.println(name + " sending http request, uri=" + uri);
		resource.setMediaType(MediaType.TEXT_PLAIN);
		//
		resource.doPost();
		System.out.println(name + " http.response.status="
				+ resource.getResponseCode());
		}
		{
			RestResourceUri uri = new RestResourceUri(config.GetSBaseUrl(),
					"s/bl");
			RestResource<String> resource = new RestResource<String>(uri,
					String.class);
			System.out.println(name + " sending http request, uri=" + uri);
			resource.setMediaType(MediaType.TEXT_PLAIN);
			//
			resource.doGet();
			System.out.println(name + " http.response.status="
					+ resource.getResponseCode());
		}
	}
	
	private void TL(String name) {
		{
		RestResourceUri uri = new RestResourceUri(config.GetSBaseUrl(), "s/tl");
		RestResource<String> resource = new RestResource<String>(uri,
				String.class);
		System.out.println(name + " sending http request, uri=" + uri);
		resource.setMediaType(MediaType.TEXT_PLAIN);
		//
		resource.doPost();
		System.out.println(name + " http.response.status="
				+ resource.getResponseCode());
		}
		{
			RestResourceUri uri = new RestResourceUri(config.GetSBaseUrl(),
					"s/tl");
			RestResource<String> resource = new RestResource<String>(uri,
					String.class);
			System.out.println(name + " sending http request, uri=" + uri);
			resource.setMediaType(MediaType.TEXT_PLAIN);
			//
			resource.doGet();
			System.out.println(name + " http.response.status="
					+ resource.getResponseCode());
		}
	}	

}
