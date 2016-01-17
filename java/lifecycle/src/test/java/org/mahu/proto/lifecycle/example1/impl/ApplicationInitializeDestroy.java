package org.mahu.proto.lifecycle.example1.impl;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.mahu.proto.lifecycle.IApiBroker;
import org.mahu.proto.lifecycle.IServiceLifeCycleControl;
import org.mahu.proto.lifecycle.impl.ApiBroker;
import org.mahu.proto.lifecycle.impl.ServiceLifeCycleControl;

public class ApplicationInitializeDestroy implements ServletContextListener {

	@Override
	public void contextInitialized(final ServletContextEvent sce) {
		final ApiBroker broker = new ApiBroker();
		final ModuleBindings1 moduleBindings = new ModuleBindings1();
		final IServiceLifeCycleControl serviceLifeCycleManager = new ServiceLifeCycleControl(broker, moduleBindings);
		sce.getServletContext().setAttribute(IApiBroker.class.getName(), broker);
		sce.getServletContext().setAttribute(IServiceLifeCycleControl.class.getName(), serviceLifeCycleManager);
	}

	@Override
	public void contextDestroyed(final ServletContextEvent sce) {
		sce.getServletContext().getAttribute(ApplicationInitializeDestroy.class.getName());
	}
}
