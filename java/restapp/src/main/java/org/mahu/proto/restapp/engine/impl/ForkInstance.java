package org.mahu.proto.restapp.engine.impl;

class ForkInstance {
	
	private final String forkName;
	private int forkCntr = 0;
	
	ForkInstance(final String aForkName, final int aForkCntr) {
		forkCntr = aForkCntr;
		forkName = aForkName;
	}
	
	int processPathIsReady() {
		forkCntr--;
		return forkCntr;
	}
	
	@Override
	public String toString() {
		return "ForkInstance: "+forkName+" forkCntr: "+forkCntr;
	}

}
