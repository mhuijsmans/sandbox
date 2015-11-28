/*
 * StopWatch.h
 */

#ifndef STOPWATCH_H_
#define STOPWATCH_H_

#include <chrono>

class StopWatch {
public:
	StopWatch();
	virtual ~StopWatch();
	std::chrono::duration<double> ElapsedSeconds();
	std::chrono::time_point<std::chrono::system_clock> start, end;
};

#endif /* STOPWATCH_H_ */
