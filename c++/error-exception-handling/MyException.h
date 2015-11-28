/*
 * Filename: MyException.h
 */

#ifndef MYEXCEPTION_H_
#define MYEXCEPTION_H_

#include <exception>
#include <memory>
#include <stdexcept>

class MyException : public std::runtime_error {
public:
	MyException(const std::string &where, const std::string &what);
	virtual ~MyException();

	std::string where;
	// In case an exception is rethrown; I would like to have this,
	// but I do not know yet, how to implement this.
	std::unique_ptr<std::exception > cause;
};

#endif /* MYEXCEPTION_H_ */
