package org.mahu.proto.jnitest.cpptaskexecutor;

import java.util.LinkedList;
import java.util.List;

import org.mahu.proto.jnitest.logging.LoggerProxy;

public class TaskExecutor {
	
	private List<Task> tasks = new LinkedList<Task>();; 
	
	// A field used by C++ to store a pointer to a CPP instance
	// where coo context data is stored.
	public long pointerCppTaskExecutor=0;
	//
	public final LoggerProxy logger;	
	
	public TaskExecutor(final LoggerProxy logger) {
		this.logger = logger;
	}
	
	public void add(final Task task) {
		synchronized(tasks) {
			tasks.add(task);
		}
		
	}
	
	public Task getTask() {
		synchronized(tasks) {
			if (tasks.isEmpty()) {
				return null;
			} else {
			 return tasks.remove(0);
			}
		}
	}
	public int getNrOfQueuedTasks() {
		synchronized(tasks) {
			return tasks.size();
		}
	}

	public native void initCpp();	
	public native void executeCpp();
	public native void disposeCpp();

}
