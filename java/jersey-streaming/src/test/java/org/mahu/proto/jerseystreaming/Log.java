package org.mahu.proto.jerseystreaming;

public class Log {
	
	public static void log(String s) {
		System.out.println(UTCTime.getUTCdatetimeAsString()+": "+ s);
	}

}
