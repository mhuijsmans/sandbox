/*
 * Filename: testLogging.cpp
 */

#include <unistd.h> // getpid#include <cstdlib>
#include <iostream>

#include "SyslogExplorer.h"
#include "gtest/gtest.h"

TEST(SyslogTest, basics) {
	SysLogExplorer::SysLogOneCall();
}
