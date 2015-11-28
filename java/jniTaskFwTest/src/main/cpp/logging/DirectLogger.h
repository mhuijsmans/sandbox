#ifndef DirectLogger_H_
#define DirectLogger_H_

#include "JavaObject.h"
#include "JavaClassConst.h"
#include "LoggerBase.h"

class DirectLogger : public LoggerBase {
public:
	DirectLogger(JavaObject &logger_) : LoggerBase(), logger(logger_){
	}
	virtual ~DirectLogger() {
	}
	
	void traceLog(const char * msg) {
		logger.setString(JavaClassConst::METHODNAME_INFO, msg);
	}
private:
	JavaObject &logger;
};

#endif
