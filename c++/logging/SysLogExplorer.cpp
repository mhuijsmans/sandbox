
/*
 * Filename: SysLogExplorer.cpp
 */

#include <iostream>
#include <syslog.h>

#include "SysLogExplorer.h"

// ref: http://linux.die.net/man/3/syslog
// states that syslog impl. stores the ident pointer as-is.
const char *SysLogExplorer::ident = "SysLogExplorer";

SysLogExplorer::SysLogExplorer() {
}

SysLogExplorer::~SysLogExplorer() {
}

void SysLogExplorer::SysLogOneCall() {
	// Note: closelog(), openlog(), and syslog() functions shall not return a value
	std::cout <<"Opening syslog & writing data";
	int logopt = 0;
	int facility = LOG_USER;
	openlog(ident, logopt, facility);
	//
	MakeOneSyslogCalls();
	//
	closelog();

	std::cout << "Closed syslog";
}

void SysLogExplorer::SysLogManyCalls() {
	// Note: closelog(), openlog(), and syslog() functions shall not return a value
	std::cout <<"Opening syslog & writing data";
	int logopt = 0;
	int facility = LOG_USER;
	openlog(ident, logopt, facility);
	//
	MakeManySyslogCalls(100);
	//
	closelog();

	std::cout << "Closed syslog";
}



void SysLogExplorer::MakeManySyslogCalls(const int max) {
	const char *message =
			"- SysLogExplorer: this is a very long message that I uses for testing; so I need to type a lot of words to make sure I generate a lot of bytes";
	int priority = LOG_INFO;
	std::string msg;
	for (int i = 0; i < max; i++) {
		msg = std::to_string(i) + message;
		syslog(priority, msg.c_str());
	}
}

void SysLogExplorer::MakeOneSyslogCalls() {
	const char *message = "SysLogExplorer: this is a simple message";
	int priority = LOG_INFO;
	syslog(priority, message);
}

