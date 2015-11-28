package org.mahu.proto.resttomcat;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.ResourceConfig;
 
// This application will be detected, but it was not clear for me what path I could next use to access the resource.
// So I commented it.
// @ApplicationPath("myapp")
public class RestApplication extends ResourceConfig {
 
  public RestApplication() {
    register(HelloResource.class);
  }
}
