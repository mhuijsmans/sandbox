package org.mahu.proto.html.rest;

public class ActionMan {
	
	private static boolean isBusy = false;
	
	public static boolean isBusy() {
		boolean tmp = isBusy;
		isBusy = !isBusy ;
		return tmp;
	}

}
