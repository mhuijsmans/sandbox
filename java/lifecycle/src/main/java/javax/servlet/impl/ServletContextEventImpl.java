package javax.servlet.impl;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

public class ServletContextEventImpl implements ServletContextEvent {
	
	private final ServletContextImpl servletContext;
	
	public ServletContextEventImpl(final ServletContextImpl servletContext) {
		this.servletContext = servletContext;
	}

	@Override
	public ServletContext getServletContext() {
		return servletContext;
	}

}
