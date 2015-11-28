package org.mahu.proto.jnitest.logging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoggerProxyImpl implements LoggerProxy {

	private static Logger LOGGER = LogManager.getLogger(LoggerProxy.class);

	private final boolean logActive;

	public LoggerProxyImpl() {
		logActive = true;
	}

	public LoggerProxyImpl(final boolean logActive) {
		this.logActive = logActive;
	}

	public void info(String msg) {
		if (logActive) {
			LOGGER.info(msg);
		}
	}

}
