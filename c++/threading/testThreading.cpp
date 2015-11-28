/*
 * Filename: testThreading.cpp
 */

#include <chrono>         // std::chrono::seconds#include <iostream>       // std::cout#include <thread>         // std::thread
#include "gtest/gtest.h"

#include "SafeQueue.hpp"

void foo() {
	std::cout << "foo ENTER, going to sleep...\n";
	std::this_thread::sleep_for(std::chrono::seconds(1));
	std::cout << "foo LEAVE...\n";
}

void bar(int x) {
	std::cout << "bar ENTER, going to sleep...\n";
	std::this_thread::sleep_for(std::chrono::seconds(2));
	std::cout << "bar LEAVE...\n";
}

// copied from:
// http://www.cplusplus.com/reference/thread/thread/
// http://www.cplusplus.com/reference/thread/thread/join/
TEST(ThreadTest, basics) {
	std::thread first(foo);     // spawn new thread that calls foo()
	std::thread second(bar, 0);  // spawn new thread that calls bar(0)

	std::cout << "main, foo and bar now execute concurrently...\n";

	// synchronize threads:
	first.join();                // pauses until first finishes
	second.join();               // pauses until second finishes

	std::cout << "foo and bar completed.\n";
}

