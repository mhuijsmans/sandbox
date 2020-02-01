package org.mahu.proto.jersey.lifecycle;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class MyAppServletContextListener2 implements ServletContextListener {
	
    // Is triggered when the web application is starting the initialization. 
	// This will be invoked before any of the filters and servlets are initialized.
	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		setIMain(arg0.getServletContext(), new Main2());
		getIMain(arg0.getServletContext()).init();
		System.out.println("ServletContextListener started");	
	}
	
	// Is triggered when the ServletContext is about to be destroyed. 
	// This will be invoked after all the servlets and filters have been destroyed.
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		System.out.println("ServletContextListener destroyed");
		getIMain(arg0.getServletContext()).destroy();
	}
	
	public static void setIMain(final ServletContext context, IMain main) {
		context.setAttribute(getKeyIMain(), main);
	}	
	
	public static IMain getIMain(final ServletContext context) {
		return (IMain)context.getAttribute(getKeyIMain());
	}
	
	public static IMain removeIMain(final ServletContext context) {
		IMain main = getIMain(context);
		context.removeAttribute(getKeyIMain());
		return main;
	}	
	
	public static String getKeyIMain() {
		return IMain.class.getName();
	}	
	
}
