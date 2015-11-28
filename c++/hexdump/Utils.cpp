/*
 * Utils.cpp
 *
 *  Created on: Aug 28, 2014
 *      Author: martien
 */

#include <iomanip>
#include <iostream>
#include <sstream>
#include "Utils.h"

Utils::Utils() {
	// TODO Auto-generated constructor stub

}

Utils::~Utils() {
	// TODO Auto-generated destructor stub
}

void Utils::printHex(const void *p, const int nrOfBytes) {
	std::cout << Utils::ConvertToHexDump(p, nrOfBytes) << std::endl;
}

std::string Utils::ConvertToHexDump(const void *p, const int nrOfBytes) {
	const unsigned char * buffer = static_cast<const unsigned char *>(p);
	std::stringstream ss;
	ss << std::hex << std::setfill('0');
	for (int i = 0; i < nrOfBytes; ++i) {
		if (i > 0) {
			if (i % 16 == 0) {
				ss << std::endl;
				ss << std::setw(4) << i << ": ";
			} else if (i % 8 == 0) {
				ss << " - ";
				ss << std::setw(4) << i << ": ";
			} else {
				ss << " ";
			}
		} else {
			ss << std::setw(4) << i << ": ";
		}
		ss << std::setw(2) << static_cast<unsigned>(buffer[i]);
	}
	return ss.str();
}

