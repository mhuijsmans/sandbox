/*
 * MainApp.cpp
 *
 *  Created on: Mar 13, 2014
 *      Author: martien
 */

#include <iostream>
#include <log4cplus/configurator.h>
#include "PlayWithStrings.h"
#include "Test1.h"
#include "PlayWithClasses.h"

//int main() {
int main(int argc, char **argv) {
	// I want to have logging
	log4cplus::initialize();
	log4cplus::BasicConfigurator config;
    config.configure();

	// After logging, there is time for hello world
	std::cout << "!!!Hello World!!!" << std::endl;

	// Create class instance. Constructor and destructor are called automatically
	Test1 test;

	// Create class instance. Constructor and destructor are called automatically
	PlayWithStrings player;
	player.playWithStrings();

	//
	PlayWithClasses classyPlayer;
	classyPlayer.itIsTimeToPlay();

	return 0;
}

