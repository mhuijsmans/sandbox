/*
 * Filename: testLogging.cpp
 */

#include <chrono>
#include <cstdlib>
#include <ctime>
#include <iostream>
#include <unistd.h> // getpid#include "gtest/gtest.h"

#include "BoostLogging.h"
#include "IOUtils.h"
#include "FileGrep.h"
#include "StopWatch.h"

class UniqueString : public std::string {
public:
	UniqueString(const char *str) : std::string(str) {
		append(std::to_string(clock()));
	}
};

void PrintProcessId() {
	std::cout << "process-id: " << getpid() << std::endl;
}

int OutcomeIsOk() {
	std::cerr << "Success" << std::endl;
	exit(0);
	return 0;
}

int OutcomeIsNok(const char *msg) {
	std::cerr << msg << std::endl;
	exit(1);
	return 1;
}

int OutcomeIs(bool b, const char *msg) {
	return b ? OutcomeIsOk() : OutcomeIsNok(msg);
}

int TestLogUsingDefaults() {
	PrintProcessId();
	// First initialization, using defaults (i.e. Console) from Boost logging.
	BoostLogging::InitAddCommonAttributes();
	BoostLogging::InitSetLogLevelToDebug();
	// logging
	BoostLogging::LogAMessageForAllLevels();
	return OutcomeIsOk();
}

int TestLogToFile() {
	PrintProcessId();
	std::string fileName("logfile.txt");
//	if (!IOUtils::DeleteFile(fileName)) {
//		return OutcomeIsNok("Failed to delete file");
//	}
	BoostLogging::InitAddCommonAttributes();
	BoostLogging::InitLogToFile(fileName);
	BoostLogging::InitSetLogLevelToDebug();
	// logging
	BoostLogging::LogAMessageForAllLevels();
	return OutcomeIsOk();
}

int TestLogToConsoleAndFile() {
	PrintProcessId();
	std::string fileName("logfile.txt");
	BoostLogging::InitAddCommonAttributes();
	BoostLogging::InitLogToConsole();
	BoostLogging::InitLogToFile(fileName);
	BoostLogging::InitSetLogLevelToDebug();
	// logging
	BoostLogging::LogAMessageForAllLevels();
	return OutcomeIsOk();
}

int TestLogToSyslogViaPosix() {
	PrintProcessId();
	// First initialization, next simple logging
	BoostLogging::InitAddCommonAttributes();
	BoostLogging::InitSetLogLevelToDebug();
	BoostLogging::InitLogToConsole();
	BoostLogging::InitLogToSysLogViaPosix();
	// logging
	UniqueString suffixText("SyslogPosix-");
	BoostLogging::LogAMessageForAllLevels(suffixText);
	return OutcomeIs(FileGrep::GrepTextInVarLogMessages(suffixText),
			"String not found in log file: /var/log/messages");
}

int TestLogToSyslogViaPosixSpecialFormatter() {
	PrintProcessId();
	// First initialization, next simple logging
	BoostLogging::InitAddCommonAttributes();
	BoostLogging::InitSetLogLevelToDebug();
	BoostLogging::InitLogToConsole();
	BoostLogging::InitLogToSysLogViaPosix(0);
	// logging
	UniqueString suffixText("SyslogPosixSpecialFormatter-");
	BoostLogging::LogAMessageForAllLevels(suffixText);
	return OutcomeIs(FileGrep::GrepTextInVarLogMessages(suffixText),
			"String not found in log file: /var/log/messages");
}

// Check that UDP syslog reception has been enabled for rsyslog (in /etc/rsyslog.conf), 
// which looks like
// $ModLoad imudp
// $UDPServerRun 514
int TestLogToSyslogViaUDP() {
	PrintProcessId();
	// First initialization, next simple logging
	BoostLogging::InitAddCommonAttributes();
	BoostLogging::InitSetLogLevelToDebug();
	BoostLogging::InitLogToConsole();
	BoostLogging::InitLogToSysLogViaUDP();
	// logging
	UniqueString suffixText("SyslogUDP-");
	BoostLogging::LogAMessageForAllLevels(suffixText);
	return OutcomeIs(FileGrep::GrepTextInVarLogMessages(suffixText),
			"String not found in log file: /var/log/messages");
}

int TestLogLevelChangedInRuntime() {
	PrintProcessId();
	// First initialization, next simple logging
	BoostLogging::InitAddCommonAttributes();
	BoostLogging::InitSetLogLevelToDebug();
	BoostLogging::InitLogToConsole();
	BoostLogging::InitLogToSysLogViaPosix();
	// logging
	UniqueString msg("TestLogLevelChangedInRuntime-");
	BoostLogging::LogTraceMessage(msg);
	if (FileGrep::GrepTextInVarLogMessages(msg)) {
		return OutcomeIsNok("Error string found in log file: /var/log/messages");
	}
	BoostLogging::SetLogLevelToTrace();
	BoostLogging::LogTraceMessage(msg);
	return OutcomeIs(FileGrep::GrepTextInVarLogMessages(msg),
			"String not found in log file: /var/log/messages");
}

int TestLoggingOverhead_SingleThreaded_noSynchronisation1() {
	// First initialization, using defaults from Boost logging.
	BoostLogging::InitAddCommonAttributes();
	BoostLogging::InitSetLogLevelToDebug();
	BoostLogging::InitLogToDevNull();
	const int max = 1000000;
	{
		// trace messages are dropped, because min level is debug
		bool complexTraceMessage = false;
		StopWatch stopWatch;
		BoostLogging::LogTraceMessageManyTimes(max, complexTraceMessage);
		std::cout << "Simple trace message: elapsed time: " << stopWatch.ElapsedSeconds().count()
				<< ", max: " << max << std::endl;
	}
	{
	  // trace messages are dropped, because min level is debug
		bool complexTraceMessage = true;
		StopWatch stopWatch;
		BoostLogging::LogTraceMessageManyTimes(max, complexTraceMessage);
		std::cout << "Complex Trace message: elapsed time: " << stopWatch.ElapsedSeconds().count()
				<< ", max: " << max << std::endl;
	}
	{
	  // debug messages are processed and written to /dev/null, because min level is debug
		StopWatch stopWatch;
		BoostLogging::LogDebugMessageManyTimes(max);
		std::cout << "Writing debug message to /dev/null unsynchronized(1)): elapsed time: " << stopWatch.ElapsedSeconds().count()
				<< ", max: " << max << std::endl;
	}
	return OutcomeIsOk();
}

int TestLoggingOverhead_SingleThreaded_Synchronisation() {
	// First initialization, using defaults from Boost logging.
	BoostLogging::InitAddCommonAttributes();
	BoostLogging::InitSetLogLevelToDebug();
	BoostLogging::InitLogToDevNullSynchronized();
	const int max = 1000000;
	{
	  // debug messages are processed and written to /dev/null, because min level is debug
		StopWatch stopWatch;
		BoostLogging::LogDebugMessageManyTimes(max);
		std::cout << "Writing debug message to /dev/null synchronized): elapsed time: " << stopWatch.ElapsedSeconds().count()
				<< ", max: " << max << std::endl;
	}
	return OutcomeIsOk();
}

int TestLoggingOverhead_SingleThreaded_noSynchronisation2() {
	// First initialization, using defaults from Boost logging.
	BoostLogging::InitAddCommonAttributes();
	BoostLogging::InitSetLogLevelToDebug();
	BoostLogging::InitLogToDevNullUnsynchronized();
	const int max = 1000000;
	{
	  // debug messages are processed and written to /dev/null, because min level is debug
		StopWatch stopWatch;
		BoostLogging::LogDebugMessageManyTimes(max);
		std::cout << "Writing debug message to /dev/null nosynchronized(2)): elapsed time: " << stopWatch.ElapsedSeconds().count()
				<< ", max: " << max << std::endl;
	}
	return OutcomeIsOk();
}

TEST(FooDeathTest, testLogfileGrepWork) {
	ASSERT_EQ(1, FileGrep::GrepTextInVarLogMessages("localhost"));
	ASSERT_EQ(0,
			FileGrep::GrepTextInVarLogMessages(
					"some impossible message that is never logged"));
}

TEST(FooDeathTest, testUsingDefaults) {
	EXPECT_EXIT(TestLogUsingDefaults(), ::testing::ExitedWithCode(0),
			"Success");
}

TEST(FooDeathTest, testToFile) {
	EXPECT_EXIT(TestLogToFile(), ::testing::ExitedWithCode(0), "Success");
}

TEST(FooDeathTest, testConsoleAndFile) {
	EXPECT_EXIT(TestLogToConsoleAndFile(), ::testing::ExitedWithCode(0),
			"Success");
}

TEST(FooDeathTest, testConsoleAndSyslogPosix) {
	EXPECT_EXIT(TestLogToSyslogViaPosix(), ::testing::ExitedWithCode(0),
			"Success");
}

TEST(FooDeathTest, estLogToSyslogViaPosixSpecialFormatter) {
	EXPECT_EXIT(TestLogToSyslogViaPosixSpecialFormatter(), ::testing::ExitedWithCode(0),
			"Success");
}

TEST(FooDeathTest, testConsoleAndSyslogUdp) {
	EXPECT_EXIT(TestLogToSyslogViaUDP(), ::testing::ExitedWithCode(0),
			"Success");
}

TEST(FooDeathTest, testLogLevelChangedInRuntime) {
	EXPECT_EXIT(TestLogLevelChangedInRuntime(), ::testing::ExitedWithCode(0),
			"Success");
}

TEST(FooDeathTest, testLoggingOverhead_SingleThreaded_noSynchronisation1) {
	EXPECT_EXIT(TestLoggingOverhead_SingleThreaded_noSynchronisation1(), ::testing::ExitedWithCode(0), "Success");
}

TEST(FooDeathTest, testLoggingOverhead_SingleThreaded_noSynchronisation2) {
	EXPECT_EXIT(TestLoggingOverhead_SingleThreaded_noSynchronisation2(), ::testing::ExitedWithCode(0), "Success");
}

TEST(FooDeathTest, testLoggingOverhead_SingleThreaded_Synchronisation) {
	EXPECT_EXIT(TestLoggingOverhead_SingleThreaded_Synchronisation(), ::testing::ExitedWithCode(0), "Success");
}

// todo:
// Boost support different formatters. Compare efficiency
// http://boost-log.sourceforge.net/libs/log/doc/html/log/tutorial/formatters.html
