/*
 * CountDownLatch.h
 */

#ifndef COUNTDOWNLATCH_H_
#define COUNTDOWNLATCH_H_

#include <cstdint>				// uint16_t
#include <mutex>              	// std::mutex, std::unique_lock
#include <condition_variable> 	// std::condition_variable

// CountDownLatch, inspired by Java CountDownLatch
// It is a thread safe class.
// Multiple users can be waiting.
class CountDownLatch {
public:

	explicit CountDownLatch(uint16_t count);
	/**
	 * Wait until the counter reaches zero.
	 */	
	void Wait();
	/**
	 * Wait a max period for the counter to reach zero.
	 * @return the value of the counter
	 */
	int Wait(int maxSecondsToWait);
	/**
	 * Decrement the counter. When it reaches zero, notify all waiters.
	 */
	void CountDown();

private:
	std::mutex mutex;
	std::condition_variable condition;
	volatile uint16_t count;

	// prohibiting copying
	// ref: http://www.stroustrup.com/C++11FAQ.html#default
	CountDownLatch(CountDownLatch const &) = delete;
	CountDownLatch &operator=(CountDownLatch const &) = delete;
};

#endif /* COUNTDOWNLATCH_H_ */
