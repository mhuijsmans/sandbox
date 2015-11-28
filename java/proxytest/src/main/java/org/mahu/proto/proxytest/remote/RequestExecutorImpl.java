package org.mahu.proto.proxytest.remote;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Logger;

public class RequestExecutorImpl implements RequestExecutor {

	private static final Logger log = Logger
			.getLogger(RequestExecutorImpl.class.getName());

	private final Class<?>[] classes;
	private Registry registry;
	private final static String NAME_PUBLISHED_SERVICE = "RequestExecutor";
	
	public RequestExecutorImpl(final Class<?>[] classes) {
		checkNotNull(classes);
		checkArgument(classes.length>0);		
		this.classes = classes;
	}

	@Override
	public <T> T executeTask(Request<T> t) throws RemoteException {
		log.fine("executeTask "+t);		
		return t.execute(classes);
	}

	public void bindRequestExecutorService() throws RemoteException {
		setSecurityManager();
		RequestExecutor stub = (RequestExecutor) UnicastRemoteObject
				.exportObject(this, 0);
		//
		registry = getRegistry();
		registry.rebind(NAME_PUBLISHED_SERVICE, stub);
		log.fine("Service "+serviceName()+" binding to name: "
				+ NAME_PUBLISHED_SERVICE);
	}

	public void unBindRequestExecutorService() throws RemoteException {
		registry = getRegistry();
		try {
			registry.unbind(NAME_PUBLISHED_SERVICE);
			log.fine("Service "+serviceName()+" unbinding from name: "
					+ NAME_PUBLISHED_SERVICE);
		} catch (NotBoundException e) {
			log.warning("Exception when unbinding "+serviceName()+" from name: "
					+ NAME_PUBLISHED_SERVICE);
		}
	}

	public RequestExecutor lookupRequestExecutorServiceInRegistry()
			throws RemoteException, NotBoundException {
		Registry registry = getRegistry();
		RequestExecutor comp = (RequestExecutor) registry
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
	
	private String serviceName() {
		return RequestExecutor.class.getSimpleName();
	}

}
