package org.mahu.tools.syslogservice;

public final class Logger {
    
    public static void syslogline(final String string) {
		System.out.println("SYS: "+ string);
	}
    
    public static void log(final String string) {
    	System.out.println("LOG: "+ string);
	}
       
}
