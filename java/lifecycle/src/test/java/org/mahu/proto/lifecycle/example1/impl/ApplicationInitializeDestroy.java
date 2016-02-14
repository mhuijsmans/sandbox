package org.mahu.proto.lifecycle.example1.impl;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.mahu.proto.lifecycle.IApiBroker;
import org.mahu.proto.lifecycle.ILifeCycleManager;
import org.mahu.proto.lifecycle.impl.ApiBroker;
import org.mahu.proto.lifecycle.impl.LifeCycleManager;

public class ApplicationInitializeDestroy implements ServletContextListener {

	@Override
	public void contextInitialized(final ServletContextEvent sce) {
		final ApiBroker broker = new ApiBroker();
		final ModuleBindings1 moduleBindings = new ModuleBindings1();
		ILifeCycleManager lifeCycleManager = new LifeCycleManager(broker, moduleBindings);		
		/**
		 * Make the IAPiBroker service available such that REST / WebSocket services can access the public API's. 
		 */
		sce.getServletContext().setAttribute(IApiBroker.class.getName(), broker);
		/**
		 * Register the service that will control the actual life cycle of software..
		 * This service can be stopped again.
		 */
		sce.getServletContext().setAttribute(ILifeCycleManager.class.getName(), lifeCycleManager);
		lifeCycleManager.start();
	}

	@Override
	public void contextDestroyed(final ServletContextEvent sce) {
	    ILifeCycleManager lifeCycleManager = (ILifeCycleManager)sce.getServletContext().getAttribute(ILifeCycleManager.class.getName());
	    lifeCycleManager.shutdown();
	}
}
