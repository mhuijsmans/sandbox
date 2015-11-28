package org.mahu.proto.restapp.task;

public interface TestTaskPauseNotifier {
	
	public void invoked();	
	public void readyToResume();
	public void resume();	
	public void cancelled();

}
