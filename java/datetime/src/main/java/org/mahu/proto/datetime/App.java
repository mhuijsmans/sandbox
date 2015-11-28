package org.mahu.proto.datetime;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.TimeZone;

public class App {
	static final String DATEFORMAT = "yyyy-MM-dd'T'HH:mm:ss";
	static final SimpleDateFormat sdf = new SimpleDateFormat(DATEFORMAT);
	static {
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
	}

	public static void main(String[] args) {
		Instant instant = Instant.now();

		// Output format is ISO-8601 with 3 fractional sub-second digits and Z-notation
		System.out.println("Now=" + instant);
		System.out.println("Now=" + GetUTCdatetimeAsString(instant));
		
		// ALso printig nano, becuase I observed that on my laptop micro and nano-seconds is always 0.
		// Milliseconds however is set. 
		System.out.println("Nano=" + instant.getNano());
	}

	public static String GetUTCdatetimeAsString(Instant instant) {
		final Date date = Date.from(instant);
		final int micro = instant.getNano() / 1000;
		return sdf.format(date) + String.format(".%06d", micro) + "Z";
	}
}
