package org.mahu.proto.restappext.event;

public interface SingleStepListener {

	// This method is called when when Process Execution has paused and thus
	// is ready to execute one task.
	public void ReadyToStep();
	
	public void SessionCompleted();

}
