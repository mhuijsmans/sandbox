package org.mahu.proto.proxytest.remote;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Remote interface to execute requests
 */
public interface RequestExecutor extends Remote {
	
	public interface Request<T> {
	    T execute(final Class<?> []registeredClasses) throws RemoteException ;
	}
	
    <T> T executeTask(Request<T> t) throws RemoteException;
}
