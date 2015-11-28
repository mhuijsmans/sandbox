/*
 * Filename: BoostLogging.h
 */

#ifndef BOOSTLOGGING_H_
#define BOOSTLOGGING_H_

#include <string>

class BoostLogging {
public:
	BoostLogging();
	virtual ~BoostLogging();

	static void Init();
	static void InitAddCommonAttributes();
	static void InitSetLogLevelToDebug();
	static void InitLogToConsole();
	static void InitLogToFile(std::string &fileName);
	static void InitLogToSysLogViaPosix();
	// Boost logging supports different formatters.
	static void InitLogToSysLogViaPosix(int formatter);
	static void InitLogToSysLogViaUDP();

	static void LogAMessageForAllLevels();
	static void LogAMessageForAllLevels(const std::string &msg);

	static void LogDebugMessageManyTimes(const int max);
	static void LogTraceMessageManyTimes(const int max, bool complexMsg);
	static void LogTraceMessage(const std::string &msg);
	
	static void InitLogToDevNull();
	static void InitLogToDevNullSynchronized();
	static void InitLogToDevNullUnsynchronized();
	static void SetLogLevelToTrace();
private:
	static const char *syslogIdentityPosix;
	static const char *syslogIdentityUdp;
};

#endif /* BOOSTLOGGING_H_ */
