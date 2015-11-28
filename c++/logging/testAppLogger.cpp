/*
 * Filename: testAppLogger.cpp
 */
 
#include "gtest/gtest.h" 

#include "AppLogger.h"
#include "BoostLogging.h"

int AppLoggerTest() {
	BoostLogging::InitAddCommonAttributes();
	BoostLogging::InitLogToConsole();
	BoostLogging::InitSetLogLevelToDebug();
	
	APP_LOG_INFO << "This is the first message for the app logger";
	
	std::cerr << "Success" << std::endl;
	exit(0);
	return 0;
}

TEST(AppLoggerTest, basic) {
	EXPECT_EXIT(AppLoggerTest(), ::testing::ExitedWithCode(0),
			"Success");
}
