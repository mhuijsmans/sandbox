package org.mahu.proto.rmitest;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Remote interface to execute tasks
 */
public interface TaskExecutor extends Remote {
	
	public interface Task<T> {
	    T execute();
	}	
	
    <T> T executeTask(Task<T> t) throws RemoteException;
}
