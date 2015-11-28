#ifndef SAFEQUEUE_HPP_
#define SAFEQUEUE_HPP_

// This code is copied / modified from:
// http://gnodebian.blogspot.com.es/2013/07/a-thread-safe-asynchronous-queue-in-c11.html

#include <queue>
#include <list>
#include <mutex>
#include <thread>
#include <cstdint>
#include <condition_variable>

/**
 * A thread-safe asynchronous queue
 */
template<class T, class Container = std::list<T>>
class SafeQueue {

	typedef typename Container::value_type value_type;
	typedef typename Container::size_type size_type;
	typedef Container container_type;

public:

	/** Create safe queue. */
	SafeQueue() :
			queue(), mutex(), conditionItemPushed(), conditionItemPopped(), maxNrOfItemsInQueue(
					0), blockWhenMaxReached(false) {
	}
	SafeQueue(SafeQueue&& sq) :
			queue(), mutex(), conditionItemPushed(), conditionItemPopped(), maxNrOfItemsInQueue(
					0), blockWhenMaxReached(false) {
		queue = std::move(sq.queue);
	}
	SafeQueue(const SafeQueue& sq) :
			queue(), mutex(), conditionItemPushed(), conditionItemPopped(), maxNrOfItemsInQueue(
					0), blockWhenMaxReached(false) {
		std::lock_guard < std::mutex > lock(sq.mutex);
		queue = sq.queue;
	}

	/**
	 * Destroy safe queue.
	 */
	~SafeQueue() {
		std::lock_guard < std::mutex > lock(mutex);
		// No notification of blocked threads. This is something to add or
		// solve on application level.
		// One option is to add a queueClosed, ensuring that users do not get blocked.
		while (!queue.empty()) {
			queue.pop();
		}
	}

	/**
	 * Sets the maximum number of items in the queue. Defaults is 0: No limit
	 * \param[in] item An item.
	 */
	void setMaxNumItems(size_t max_num_items, bool blockWhenMaxReached=false  ) {
		this->maxNrOfItemsInQueue = max_num_items;
		this->blockWhenMaxReached = blockWhenMaxReached;
	}

	/**
	 *  Pushes the item into the queue.
	 * \param[in] item An item.
	 * \return true if an item was pushed into the queue
	 */
	bool push(const value_type& item) {
		std::lock_guard < std::mutex > lock(mutex);
		if (isAllowedToPushItem(lock)) {
			queue.push(item);
			conditionItemPushed.notify_one();
			return true;
		} else {
			return false;
		}
	}

	/**
	 *  Pushes the item into the queue.
	 * \param[in] item An item.
	 * \return true if an item was pushed into the queue
	 */
	bool push(const value_type&& item) {
		std::unique_lock < std::mutex > lock(mutex);
		if (isAllowedToPushItem(lock)) {
			queue.push(item);
			conditionItemPushed.notify_one();
			return true;
		} else {
			return false;
		}
	}

	/**
	 *  Pops item from the queue. If queue is empty, this function blocks until item becomes available.
	 * \param[out] item The item.
	 */
	void pop(value_type& item) {
		std::unique_lock < std::mutex > lock(mutex);
		waitUntilQueueNotEmpty(lock);
		queuePop(item);
	}

	bool popWaitMaxMs(value_type& item, const size_t maxWaitInMilliSeconds) {
		std::unique_lock < std::mutex > lock(mutex);
		conditionItemPushed.wait_for(lock,
				std::chrono::milliseconds(maxWaitInMilliSeconds),
				[this]() {return !queue.empty();});
		return queuePop(item);
	}

//	void pop_with_timeout() {
//	    auto now = std::chrono::system_clock::now();
//	    if(cv.wait_until(lk, now + std::chrono::milliseconds(idx*100), [](){return i == 1;}))
//	        std::cerr << "Thread " << idx << " finished waiting. i == " << i << '\n';
//	    else
//	        std::cerr << "Thread " << idx << " timed out. i == " << i << '\n';
//	}

	/**
	 *  Pops item from the queue using the contained type's move assignment operator, if it has one..
	 *  This method is identical to the pop() method if that type has no move assignment operator.
	 *  If queue is empty, this function blocks until item becomes available.
	 * \param[out] item The item.
	 */
	void movePop(value_type& item) {
		std::unique_lock < std::mutex > lock(mutex);
		waitUntilQueueNotEmpty(lock);
		queuePopMove(item);
	}

	/**
	 *  Tries to pop item from the queue.
	 * \param[out] item The item.
	 * \return False is returned if no item is available.
	 */
	bool tryPop(value_type& item) {
		std::unique_lock < std::mutex > lock(mutex);
		return queuePop(item);
	}

	/**
	 *  Tries to pop item from the queue using the contained type's move assignment operator, if it has one..
	 *  This method is identical to the try_pop() method if that type has no move assignment operator.
	 * \param[out] item The item.
	 * \return False is returned if no item is available.
	 */
	bool tryMovePop(value_type& item) {
		std::unique_lock < std::mutex > lock(mutex);
		return queuePopMove(item);
	}

	/**
	 *  Pops item from the queue. If the queue is empty, blocks for timeout microseconds, or until item becomes available.
	 * \param[out] t An item.
	 * \param[in] timeout The number of milliseconds to wait.
	 * \return true if get an item from the queue, false if no item is received before the timeout.
	 */
	bool timeoutPop(value_type& item, std::uint64_t timeout) {
		std::unique_lock < std::mutex > lock(mutex);
		if (queue.empty() && timeout > 0) {
			conditionItemPushed.wait_for(lock,
					std::chrono::milliseconds(timeout));
		}
		return queuePop(item);
	}

	/**
	 *  Pops item from the queue using the contained type's move assignment operator, if it has one..
	 *  If the queue is empty, blocks for timeout microseconds, or until item becomes available.
	 *  This method is identical to the try_pop() method if that type has no move assignment operator.
	 * \param[out] t An item.
	 * \param[in] timeout The number of milliseconds to wait.
	 * \return true if get an item from the queue, false if no item is received before the timeout.
	 */
	bool timeoutMovePop(value_type& item, std::uint64_t timeout) {
		std::unique_lock < std::mutex > lock(mutex);
		if (queue.empty() && timeout > 0) {
			conditionItemPushed.wait_for(lock,
					std::chrono::milliseconds(timeout));
		}
		return queuePopMove(item);
	}

	/**
	 *  Gets the number of items in the queue.
	 * \return Number of items in the queue.
	 */
	size_type size() const {
		std::lock_guard < std::mutex > lock(mutex);
		return queue.size();
	}

	/**
	 *  Check if the queue is empty.
	 * \return true if queue is empty.
	 */
	bool empty() const {
		std::lock_guard < std::mutex > lock(mutex);
		return queue.empty();
	}

	/**
	 *  Swaps the contents.
	 * \param[out] sq The SafeQueue to swap with 'this'.
	 */
	void swap(SafeQueue& sq) {
		if (this != &sq) {
			std::lock_guard < std::mutex > lock1(mutex);
			std::lock_guard < std::mutex > lock2(sq.mutex);
			queue.swap(sq.queue);

			if (!queue.empty())
				conditionItemPushed.notify_all();

			if (!sq.queue.empty())
				sq.conditionItemPushed.notify_all();
		}
	}

	/*! The copy assignment operator */
	SafeQueue& operator=(const SafeQueue& sq) {
		if (this != &sq) {
			std::lock_guard < std::mutex > lock1(mutex);
			std::lock_guard < std::mutex > lock2(sq.mutex);
			std::queue<T, Container> temp { sq.queue };
			queue.swap(temp);

			if (!queue.empty())
				conditionItemPushed.notify_all();
		}

		return *this;
	}

	/*! The move assignment operator */
	SafeQueue& operator=(SafeQueue && sq) {
		std::lock_guard < std::mutex > lock(mutex);
		queue = std::move(sq.queue);

		if (!queue.empty())
			conditionItemPushed.notify_all();

		return *this;
	}

private:

	bool queuePopMove(value_type& item) {
		if (queue.empty()) {
			return false;
		} else {
			item = std::move(queue.front());
			queuePop();
			return true;
		}
	}

	bool queuePop(value_type& item) {
		if (queue.empty()) {
			return false;
		} else {
			item = queue.front();
			queuePop();
			return true;
		}
	}

	void queuePop() {
		if (blockWhenMaxReached) {
			conditionItemPopped.notify_one();
		}
		queue.pop();
	}

	void waitUntilQueueNotEmpty(std::unique_lock < std::mutex > &lock) {
		conditionItemPushed.wait(lock, [this]() {return !queue.empty();});
	}

	bool isAllowedToPushItem(std::unique_lock < std::mutex > &lock) {
		// std::unique_lock can be locked/unlocked. Used by conditionVariable.
		if (maxNrOfItemsInQueue > 0 && queue.size() >= maxNrOfItemsInQueue) {
			if (blockWhenMaxReached) {
				conditionItemPopped.wait(lock,
						[this]() {return queue.size() < maxNrOfItemsInQueue;});
			} else {
				return false;
			}
		}
		return true;
	}

	std::queue<T, Container> queue;
	mutable std::mutex mutex;
	std::condition_variable conditionItemPushed;
	std::condition_variable conditionItemPopped;
	size_t maxNrOfItemsInQueue = 0;
	bool blockWhenMaxReached;
};

/*! Swaps the contents of two SafeQueue objects. */
template<class T, class Container>
void swap(SafeQueue<T, Container>& q1, SafeQueue<T, Container>& q2) {
	q1.swap(q2);
}
;
#endif /* SAFEQUEUE_HPP_ */
