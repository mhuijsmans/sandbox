/*
 * Bar.cpp
 *
 *  Created on: Dec 29, 2014
 *      Author: 310160231
 */

#include "Bar.h"

#include <iostream>

Bar::Bar(Foo &foo) : foo(foo) {
	std::cout << "Bar::Bar()" << std::endl;
}

Bar::~Bar() {
}

int Bar::doOne() {
	std::cout << "Bar::doOne() ENTER" << std::endl;
    int r = foo.One();
    std::cout << "Bar::doOne() LEAVE returns=" << r << std::endl;
    return r;
}

int Bar::doTwo() {
	std::cout << "Bar::doTwo() ENTER" << std::endl;
    int r = foo.Two();
    std::cout << "Bar::doTwo() LEAVE returns=" << r << std::endl;
    return r;
}

int Bar::OneArgument(int a) {
    std::cout << "Bar::OneArgument() ENTER arg=" << a << std::endl;
    int r = foo.OneArgument(a);
    std::cout << "Bar::OneArgument() LEAVE returns=" << r << std::endl;
    return r;
}
