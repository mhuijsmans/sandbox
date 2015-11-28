/*
 * StopWatch.cpp
 */

#include <ctime>
#include "StopWatch.h"

StopWatch::StopWatch() : start(), end() {
	start = std::chrono::system_clock::now();
	end = start;
}

StopWatch::~StopWatch() {
}

std::chrono::duration<double> StopWatch::ElapsedSeconds() {
	if (start == end) {
		end = std::chrono::system_clock::now();
	}
	return end - start;
}

