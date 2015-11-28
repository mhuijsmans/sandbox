package org.mahu.proto.pipes.api;

public interface DataHandler {
	public void Handle(Data data);
	public DataHandler SetNext(DataHandler next);
}
