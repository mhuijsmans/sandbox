#ifndef LoggerBase_H_
#define LoggerBase_H_

#include <string>

class LoggerBase {
public:
	LoggerBase() {}
	virtual ~LoggerBase() {}
	
	virtual void traceLog(const char * msg) {}
	virtual void traceLog(const std::string &msg) {
		traceLog(msg.c_str());
	}
};

#endif
