#include <sys/time.h> // gettimeofday

#include <chrono>
#include <ctime>
#include <string>
#include <iostream>
#include <sstream>
#include <iomanip>

#include "StopWatch.h"

std::string asString(const std::chrono::system_clock::time_point& tp)
{
	// convert to system time:
	std::time_t t = std::chrono::system_clock::to_time_t(tp);
	std::string ts = std::ctime(&t); // convert to calendar time
	ts.resize(ts.size() - 1);         // skip trailing newline
	return ts;
}

std::string now()
{
	return asString(std::chrono::system_clock::now());
}

std::string nowWithMs()
{
	std::chrono::high_resolution_clock::time_point p =
			std::chrono::high_resolution_clock::now();

	std::chrono::milliseconds ms = std::chrono::duration_cast
			< std::chrono::milliseconds > (p.time_since_epoch());

	std::chrono::seconds s = std::chrono::duration_cast < std::chrono::seconds> (ms);

	std::time_t t = s.count();
	std::size_t fractional_seconds = ms.count() % 1000;

	std::string ts = std::ctime(&t); // convert to calendar time
	ts.resize(ts.size() - 1);
	int length = ts.size();

	std::ostringstream sstream;
	// not very efficient
	sstream << ts.substr(0, length - 5) << "." << std::setw(3)
			<< std::setfill('0') << fractional_seconds << ts.substr(length - 5);
	return sstream.str();
}

// copied from: http://www.drdobbs.com/cpp/logging-in-c/201804215
std::string NowTime()
{
	struct timeval tv;
	gettimeofday(&tv, 0);
	char buffer[100];
	tm r;
	strftime(buffer, sizeof(buffer), "%X", localtime_r(&tv.tv_sec, &r));
	char result[100];
	sprintf(result, "%s.%06ld", buffer, (long) tv.tv_usec);
	return result;
}

// c++11 style:
// Formatting
// http://en.cppreference.com/w/cpp/chrono/c/strftime
// time to gmtime (used below)
// http://en.cppreference.com/w/cpp/chrono/c/gmtime
std::string GetUTCTimeAsString()
{
	// replace the C++ global locale as well as the C locale with the user-preferred locale
	// std::locale::global(std::locale(""));

	std::time_t t = std::time(NULL);
	char mbstr[100];
	std::stringstream ss;
	// Format of UTC is ISO 8601
	//if (std::strftime(mbstr, sizeof(mbstr), "%Y-%m-%dT%H:%M:%SZ", std::localtime(&t)))
	if (std::strftime(mbstr, sizeof(mbstr), "%Y-%m-%dT%H:%M:%SZ", std::gmtime(&t)))
	{
		ss << mbstr;
	}
	else
	{
		ss << "ERROR";
	}
	return ss.str();
}

std::string GetUTCTimeWithMsAsString()
{
	// replace the C++ global locale as well as the C locale with the user-preferred locale
	// std::locale::global(std::locale(""));

	std::chrono::high_resolution_clock::time_point now = std::chrono::high_resolution_clock::now();
	std::chrono::milliseconds ms = std::chrono::duration_cast< std::chrono::milliseconds > (now.time_since_epoch());
	std::size_t fractional_seconds = ms.count() % 1000;
	std::time_t now_c = std::chrono::system_clock::to_time_t(now);

	char mbstr[100];
	std::stringstream ss;
	// Format of UTC is ISO 8601
	// if (std::strftime(mbstr, sizeof(mbstr), "%Y-%m-%dT%H:%M:%S.%f", std::localtime(&t)))
	if (std::strftime(mbstr, sizeof(mbstr), "%Y-%m-%dT%H:%M:%S", std::gmtime(&now_c)))
	{
		ss << mbstr;
		ss << "." << std::setw(3) << std::setfill('0') << fractional_seconds << "Z";
	}
	else
	{
		ss << "ERROR";
	}
	return ss.str();
}

std::string GetUTCTimeWithOffsetAsString()
{
	time_t now;
	time(&now);
	char ts[sizeof "1970-01-01T00:00:00+00:00"];
	strftime(ts, sizeof ts, "%FT%T%z", gmtime(&now));
	return std::string(ts);
}

/* outcome
now               : Mon May  4 23:00:09 2015
nowWithMs         : Mon May  4 23:00:09.749 2015
NowTime           : 23:00:09.749509
UTCTimeAsString   : 2015-05-04T21:00:09Z
UTCTimeWithMs     : 2015-05-04T21:00:09.749Z
UTCTimeWithOffset : 2015-05-04T21:00:09+0000
 */

int main()
{
	Stopwatch<> sw;

	std::cout << "now               : " << now() << std::endl;
	std::cout << "nowWithMs         : " << nowWithMs() << std::endl;
	std::cout << "NowTime           : " << NowTime() << std::endl;
	std::cout << "UTCTimeAsString   : " << GetUTCTimeAsString() << std::endl;
	std::cout << "UTCTimeWithMs     : " << GetUTCTimeWithMsAsString() << std::endl;
	std::cout << "UTCTimeWithOffset : " << GetUTCTimeWithOffsetAsString() << std::endl;
	std::cout << "functionToBeTimed took " << sw.Elapsed() << " microseconds\n";
}
