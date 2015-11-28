/*
 * PlayWithClasses.cpp
 *
 *  Created on: Mar 13, 2014
 *      Author: martien
 */

#include "PlayWithClasses.h"

#include <log4cplus/logger.h>
#include <log4cplus/loggingmacros.h>
#include <vector>

// Static declaration of variable outside header file.
const log4cplus::Logger PlayWithClasses_logger = log4cplus::Logger::getInstance(
		LOG4CPLUS_TEXT("PlayWithClasses"));
int PlayWithClasses_id = 0;

PlayWithClasses::PlayWithClasses() {
	id = PlayWithClasses_id++;
	LOG4CPLUS_INFO(PlayWithClasses_logger,
			LOG4CPLUS_TEXT("PlayWithClasses::PlayWithClasses "+getId()));
}


void PlayWithClasses::itIsTimeToPlay() {
	LOG4CPLUS_INFO(PlayWithClasses_logger,
			LOG4CPLUS_TEXT("*** PlayWithClasses::itIsTimeToPlay start "+getId()));
	//
	std::vector<PlayWithClasses> playList;
	for(int i=0; i<10; i++) {
		PlayWithClasses *tmp = new PlayWithClasses();
		playList.push_back(*tmp);
	}
	//
	LOG4CPLUS_INFO(PlayWithClasses_logger,
			LOG4CPLUS_TEXT("*** PlayWithClasses::itIsTimeToPlay end "+getId()));

}

PlayWithClasses::~PlayWithClasses() {
	LOG4CPLUS_INFO(PlayWithClasses_logger,
			LOG4CPLUS_TEXT("PlayWithClasses::~PlayWithClasses "+getId()));
}

std::string PlayWithClasses::getId() {
	std::stringstream sstm;
	sstm << "id-" << id;
	return sstm.str();
}

