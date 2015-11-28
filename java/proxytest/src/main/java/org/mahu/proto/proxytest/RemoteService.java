package org.mahu.proto.proxytest;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import org.mahu.proto.proxytest.remote.RequestExecutor;
import org.mahu.proto.proxytest.remote.RequestExecutorImpl;

public class RemoteService {
	
	private final RequestExecutorImpl re;

	public RemoteService (final Class<?>[] classes) {
		this.re = new RequestExecutorImpl(classes);
	}

	public void start() throws RemoteException {
		// Publish the Compute service in the registry so that it can be invoked
		re.bindRequestExecutorService();
	}
	
	public void stop() throws RemoteException {
		re.unBindRequestExecutorService();
	}
	
	public RequestExecutor getLocalRequestExecutor() throws RemoteException, NotBoundException {
		return re.lookupRequestExecutorServiceInRegistry();
	}
}
