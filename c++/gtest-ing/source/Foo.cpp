/*
 * Filename: Foo.cpp
 */

#include <iostream>
#include <signal.h>
#include <stdexcept>
#include <stdio.h>
#include <unistd.h>

#include "Foo.h"

Foo::Foo() {
    std::cout << "Foo::Foo()" << std::endl;
}

Foo::~Foo() {
}

void Foo::ThrowException() {
	throw std::runtime_error("Foo::throwException(");
}
int Foo::ExitOnNegativeArgument(int v) {
	std::cout << "Foo::ExitOnNegativeArgument, pid=" << getpid() <<std::endl;
	if (v<0) {
		// test case checks output from cerr
		std::cerr << "Invalid argument: " << v <<std::endl;
		exit(1);
	}
	return 0;
}