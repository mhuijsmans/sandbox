package org.mahu.proto.rmitest;

import static com.google.common.base.Preconditions.checkNotNull;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Logger;

public class TaskExecutorImpl implements TaskExecutor {

	private static final Logger log = Logger.getLogger(TaskExecutorImpl.class
			.getName());

	private Registry registry;
	private final static String NAME_PUBLISHED_SERVICE = "TaskExecutor";

	public TaskExecutorImpl() {
		super();
	}

	public <T> T executeTask(Task<T> t) {
		checkNotNull(t);
		return t.execute();
	}

	public void bindTaskExecutorService() throws RemoteException {
		setSecurityManager();
		TaskExecutor engine = new TaskExecutorImpl();
		TaskExecutor stub = (TaskExecutor) UnicastRemoteObject.exportObject(
				engine, 0);
		//
		registry = getRegistry();
		registry.rebind(NAME_PUBLISHED_SERVICE, stub);
		log.info("Service TaskExecutor binding to name: "
				+ NAME_PUBLISHED_SERVICE);
	}

	public void unBindTaskExecutorService() throws RemoteException {
		registry = getRegistry();
		try {
			registry.unbind(NAME_PUBLISHED_SERVICE);
			log.info("Service TaskExecutor unbinding from name: "
					+ NAME_PUBLISHED_SERVICE);
		} catch (NotBoundException e) {
			log.warning("Exception when unbinding Service TaskExecutor from name: "
					+ NAME_PUBLISHED_SERVICE);
		}
	}

	public TaskExecutor lookupTaskExecutorServiceInRegistry()
			throws RemoteException, NotBoundException {
		Registry registry = getRegistry();
		TaskExecutor comp = (TaskExecutor) registry
				.lookup(NAME_PUBLISHED_SERVICE);
		return comp;
	}

	protected void setSecurityManager() {
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
	}

	private Registry getRegistry() throws RemoteException {
		Registry registry = LocateRegistry.getRegistry();
		return registry;
	}
}
