package org.mahu.proto.lifecycle;

import javax.servlet.impl.ServletContextEventImpl;
import javax.servlet.impl.ServletContextImpl;

import org.junit.Test;
import org.mahu.proto.lifecycle.example1.impl.ApplicationInitializeDestroy;

public class InitDestroyTest {

	@Test
	public void contextInitialized() {
		ServletContextImpl servletContext = new ServletContextImpl();
		ServletContextEventImpl event = new ServletContextEventImpl(servletContext);
		ApplicationInitializeDestroy appInitDestroy = new ApplicationInitializeDestroy();
		appInitDestroy.contextInitialized(event);
	}
	
	@Test
	public void contextDestroy() {
		ServletContextImpl servletContext = new ServletContextImpl();
		ServletContextEventImpl event = new ServletContextEventImpl(servletContext);
		ApplicationInitializeDestroy appInitDestroy = new ApplicationInitializeDestroy();
		appInitDestroy.contextInitialized(event);
		
		appInitDestroy.contextDestroyed(event);
	}	

}
