/*
 * Filename: MyException.cpp
 */

#include "MyException.h"

MyException::MyException(const std::string &where_, const std::string &what_) :
		std::runtime_error(what_), where(where_) {
}

MyException::~MyException() {
}

