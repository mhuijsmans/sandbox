package org.mahu.proto.jersey.lifecycle;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;

public class RestService implements IRestService {

	@Context
	public ServletContext myContext;

	@Override
	public String getInfo1() {
		System.out.println("RestService.getInfo1");			
		myContext.setAttribute(Const.HELLO, Const.HELLO);
		return Const.HELLO;
	}
	
	@Override
	public String getInfo2() {
		System.out.println("RestService.getInfo2");					
		Object hello = myContext.getAttribute(Const.HELLO);
		return hello != null ? (String)hello : Const.UNKNOWN;
	}	

}
