package org.mahu.proto.jersey.lifecycle;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class MyAppServletContextListener1 implements ServletContextListener {
	
    // Is triggered when the web application is starting the initialization. 
	// This will be invoked before any of the filters and servlets are initialized.
	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		final ServletContext context = arg0.getServletContext();
		System.out.println("ServletContextListener started, myContext set=" + (context !=null));	
	}	
	
	// Is triggered when the ServletContext is about to be destroyed. 
	// This will be invoked after all the servlets and filters have been destroyed.
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		System.out.println("ServletContextListener destroyed");
	}

}
