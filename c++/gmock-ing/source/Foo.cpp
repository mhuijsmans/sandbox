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

int Foo::One() {
	std::cout << "Foo::one()" << std::endl;
	return 1;
}

int Foo::Two() const {
	std::cout << "Foo::two()" << std::endl;
	return 2;
}

int Foo::OneArgument(int a) {
    std::cout << "Foo::OneArgument(), arg=" << a << std::endl;
    return a;
}
