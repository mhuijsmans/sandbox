/*
 * PlayWithStrings.cpp
 *
 *  Created on: Mar 13, 2014
 *      Author: martien
 */

#include "PlayWithStrings.h"

#include <log4cplus/logger.h>
#include <log4cplus/loggingmacros.h>
#include <string>

// Static declaration of variable outside header file.
const log4cplus::Logger PlayWithStrings_logger = log4cplus::Logger::getInstance(LOG4CPLUS_TEXT("PlayWithStrings"));

PlayWithStrings::PlayWithStrings() {
	LOG4CPLUS_INFO(PlayWithStrings_logger, LOG4CPLUS_TEXT("PlayWithStrings::PlayWithStrings"));

}

void PlayWithStrings::playWithStrings() {
	std::string string = "happy logging";
//	log4cplus::LogLevel logLevel = log4cplus::INFO_LOG_LEVEL;
//	PlayWithStrings_logger.setLogLevel( logLevel );
	LOG4CPLUS_INFO(PlayWithStrings_logger, LOG4CPLUS_TEXT(string));
	//
	// This appends data to the current string; Thus a string is muttable (not immuatable)
	string.append(" #martien");
	LOG4CPLUS_INFO(PlayWithStrings_logger, LOG4CPLUS_TEXT(string));
	//
	std::string::size_type idx = string.find('#');
	if (idx != std::string::npos) {
		LOG4CPLUS_INFO(PlayWithStrings_logger, LOG4CPLUS_TEXT("found # in: "+string));
	} else {
		LOG4CPLUS_INFO(PlayWithStrings_logger, LOG4CPLUS_TEXT("not found # in: "+string));
	}

	// Other string examples in:
	// http://www.mochima.com/tutorials/strings.html
}

PlayWithStrings::~PlayWithStrings() {
	LOG4CPLUS_INFO(PlayWithStrings_logger, LOG4CPLUS_TEXT("PlayWithStrings::~PlayWithStrings"));
}

