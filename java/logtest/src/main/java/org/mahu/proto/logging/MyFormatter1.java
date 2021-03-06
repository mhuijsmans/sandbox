package org.mahu.proto.logging;

import java.lang.management.ManagementFactory;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class MyFormatter1 extends Formatter {

	public static volatile int logCntr = 0;

	//
	// Create a DateFormat to format the logger timestamp.
	//
	private static final DateFormat formatLocalTime = new SimpleDateFormat(
			"dd/MM/yyyy|hh:mm:ss.SSS");
	private static final SimpleDateFormat formatUTC = new SimpleDateFormat(
			"HH:mm");
	static {
		formatUTC.setTimeZone(TimeZone.getTimeZone("UTC"));
	}
	private final static String processId = getProcessId();

	public String format(LogRecord record) {
		logCntr++;
		// When creating a log message take care that what is logged is related
		// to the issuer of the log statement. Format is not (necessarily)
		// called in the same thread as the issues of the log statement and not
		// immediately after the actual call.
		Date logDate = new Date(record.getMillis());
		StringBuilder builder = new StringBuilder(1000);
		builder.append(formatLocalTime.format(logDate));
		builder.append("|").append(formatUTC.format(logDate));		
		builder.append("|").append(processId).append("|")
				.append(record.getThreadID());
		builder.append("|").append(record.getSourceClassName());
		builder.append("|").append(record.getSourceMethodName());
		builder.append("|logtest");
		builder.append("|").append(record.getLevel());
		builder.append("|").append(formatMessage(record));
		builder.append("\n");
		return builder.toString();
	}

	public String getHead(Handler h) {
		return super.getHead(h);
	}

	public String getTail(Handler h) {
		return super.getTail(h);
	}

	private static String getProcessId() {
		// Implementation doesn't work on all JVM's.
		// Format jvmName: '<pid>@<hostname>', at in SUN / Oracle JVMs
		final String jvmName = ManagementFactory.getRuntimeMXBean().getName();
		final int index = jvmName.indexOf('@');
		if (index < 1) {
			// part before '@' empty (index = 0) / '@' not found (index = -1)
			throw new AssertionError("Failed to determine processId");
		}
		// todo: regexp to verify format
		return jvmName.substring(0, index);
	}
}
