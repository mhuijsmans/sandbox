/*
 * StringOverload.cpp
 *
 *  Created on: Aug 10, 2014
 *      Author: martien
 */

#include "StringOverload.h"
#include <sstream>

// http://stackoverflow.com/questions/64782/how-do-you-append-an-int-to-a-string-in-c
std::string operator+(std::string const &a, int b) {
  std::ostringstream oss;
  oss<<a<<b;
  return oss.str();
}

