/*
 * Filename: testSafeQueue.cpp
 */
#include <iostream>       // std::cout#include <thread>         // std::thread
#include "gtest/gtest.h"

#include "SafeQueue.hpp"
typedef SafeQueue<int> IntTaskQueue;

std::string nowWithMs() {
	std::chrono::high_resolution_clock::time_point p =
			std::chrono::high_resolution_clock::now();

	std::chrono::milliseconds ms = std::chrono::duration_cast
			< std::chrono::milliseconds > (p.time_since_epoch());

	std::chrono::seconds s = std::chrono::duration_cast < std::chrono::seconds > (ms);

	std::time_t t = s.count();
	std::size_t fractional_seconds = ms.count() % 1000;

	std::string ts = std::ctime(&t); // convert to calendar time
	ts.resize(ts.size() - 1);
	int length = ts.size();

	std::ostringstream sstream;
	// not very efficient
	sstream << ts.substr(0,length-5) << "." << std::setw(3) << std::setfill('0' ) << fractional_seconds << ts.substr(length-5);
	return sstream.str();
}

// copied greeting(..) from: http://www.justsoftwaresolutions.co.uk/threading/multithreading-in-c++0x-part-3.html
class Worker
{
public:
	Worker(const size_t maxTasksToExecute) : maxTasksToExecute(maxTasksToExecute) {
	}
    void executeTasks(IntTaskQueue &queue)
    {
    	std::cout << "worker ENTER, executing 3 tasks...\n";
    	for (size_t i = 0; i < maxTasksToExecute; i++) {
    		int task;
    		queue.pop(task);
    		std::cout << "worker received task: " << task << "\n";
    	}
    	std::cout << "worker LEAVE...\n";
    }
private:
    const size_t maxTasksToExecute;
};

TEST(SafeQueueTest, push_tasks_workerExecutesSomeTasks) {
	IntTaskQueue queue;
	size_t maxTasksToExecute = 3;
	Worker worker(maxTasksToExecute);
	std::thread t2(&Worker::executeTasks, &worker, std::ref(queue));
	queue.push(1);
	queue.push(3);
	queue.push(5);
	queue.push(8);
	t2.join();
	ASSERT_EQ(1, queue.size());
}

TEST(SafeQueueTest, popWaitMaxMs_noTask_methodReturnFalseAfterWaitTime) {
	IntTaskQueue queue;
	int task;
	size_t maxTimeToWaitMs = 100;
	auto start = std::chrono::system_clock::now();
	ASSERT_FALSE(queue.popWaitMaxMs(task, maxTimeToWaitMs));
	auto end = std::chrono::system_clock::now();
	long int delta = std::chrono::duration_cast<std::chrono::milliseconds>(end-start).count();
	ASSERT_GE(delta, 100);
	ASSERT_LT(delta, 120);
}

TEST(SafeQueueTest, popWaitMaxMs_taskQueued_methodReturnImmediatelyWithTask) {
	IntTaskQueue queue;
	queue.push(1);
	int task;
	size_t maxTimeToWaitMs = 100;
	auto start = std::chrono::system_clock::now();
	ASSERT_TRUE(queue.popWaitMaxMs(task, maxTimeToWaitMs));
	// check outcome
	auto end = std::chrono::system_clock::now();
	long int delta = std::chrono::duration_cast<std::chrono::milliseconds>(end-start).count();
	ASSERT_LT(delta, 20);
	ASSERT_EQ(1,task);
}

class WorkerThatFirstSleeps
{
public:
    void executeTasks(IntTaskQueue &queue)
    {
    	std::cout << "WorkerThatFirstSleeps ENTER, first going to sleep at " << nowWithMs() << "\n";
        std::chrono::milliseconds dura( 200 );
        std::this_thread::sleep_for( dura );
        std::cout << "WorkerThatFirstSleeps woke up at " << nowWithMs() << "\n";
   		int task;
   		queue.pop(task);
        std::cout << "WorkerThatFirstSleeps received task: " << task << " at " << nowWithMs() << "\n";
   		queue.pop(task);
   		std::cout << "WorkerThatFirstSleeps received task: " << task << " at " << nowWithMs() << "\n";
    	std::cout << "worker LEAVE\n";
    }
};

TEST(SafeQueueTest, push_pushOnFullQueue_pushBlockAndCompletesWhenItemRemoved) {
	IntTaskQueue queue;
	queue.setMaxNumItems(1,true);	// set max & block when max reached
	queue.push(5);
	WorkerThatFirstSleeps worker;
	std::thread t1(&WorkerThatFirstSleeps::executeTasks, &worker, std::ref(queue));
	auto start = std::chrono::system_clock::now();
	// next call will block
	queue.push(8);
	auto end = std::chrono::system_clock::now();
	t1.join();
	// check outcome
	long int delta = std::chrono::duration_cast<std::chrono::milliseconds>(end-start).count();
	ASSERT_GT(delta, 150);
}
