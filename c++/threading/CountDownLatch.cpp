/*
 * CountDownLatch.cpp
 */

#include <chrono>

#include "CountDownLatch.h"

CountDownLatch::CountDownLatch(uint16_t count_) :
		mutex(), condition(), count(count_) {
}

void CountDownLatch::Wait() {
	std::unique_lock < std::mutex > lock(mutex);
	while (count!=0) {
		condition.wait(lock);
	}
}

int CountDownLatch::Wait(int maxSecondsToWait) {
	std::unique_lock < std::mutex > lock(mutex);
	if (maxSecondsToWait > 0) {
		while (count != 0) {
			if (condition.wait_for(lock,
					std::chrono::milliseconds(maxSecondsToWait*1000))
					== std::cv_status::timeout) {
				return count;
			}
		}
		return 0;
	}
	return count;
}

void CountDownLatch::CountDown() {
	std::unique_lock < std::mutex > lock(mutex);
	if (count > 0) {
		count--;
		if (count == 0) {
			condition.notify_all();
		}
	}
}
