package org.mahu.proto.jersey.lifecycle;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;

public class RestartRestService implements IRestartRestService {

	@Context
	public ServletContext myContext;

	@Override
	public String restart() {
		System.out.println("RestartRestService.restart");			
		return Const.OK;
	}	

}
