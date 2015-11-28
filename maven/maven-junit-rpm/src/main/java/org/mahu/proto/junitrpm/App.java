package org.mahu.proto.junitrpm;

import java.util.concurrent.TimeUnit;

public class App 
{
    public static void main( String[] args )
    {
    	//
    	System.setProperty("user.dir", args[0]);
    	//
        System.out.println( "Hello RPM World!" );
        boolean isRunning=true;
        while(isRunning) {
        	try {
				TimeUnit.SECONDS.sleep(30);
			} catch (InterruptedException e) {
				isRunning = false;
			}
        }
    }
}
