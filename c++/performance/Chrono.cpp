#define _POSIX_C_SOURCE 200809L

#include <inttypes.h>
#include <math.h>
#include <time.h>
#include "Chrono.h"

Chrono::Chrono()
{
	reset();
}

long Chrono::elapsedTimeInMs()
{
	return nowMS() - now;
}

long Chrono::nowMS()
{
	// copied from: http://stackoverflow.com/questions/3756323/getting-the-current-time-in-milliseconds
	long ms; // Milliseconds
	time_t s;  // Seconds
	struct timespec spec;

	clock_gettime(CLOCK_REALTIME, &spec);

	s = spec.tv_sec;
	ms = round(spec.tv_nsec / 1.0e6); // Convert nanoseconds to milliseconds

	return s*1000+ms;
}

void Chrono::reset()
{
	now = nowMS();
}
