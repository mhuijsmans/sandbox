package org.mahu.proto.logging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

public class Log4j2RSysLogTest {

	static {
		Log4j2Util.setConfigFile("log4j2RSyslog.xml");
	}

	// rsyslog should run on port 514 (udp/tcp)
	// to check on linux use: netstart -nl | grep 514
	// In rsyslog runs on VM, also check that firewall allows remote access.
	@Test
	public void logViaRootLoggerWhichInNotInXml() {
		Logger log = LogManager.getLogger("rsyslog");
		log.info("Error");
	}

}
